package com.upstream.app;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.UUID;

public class LLMManager {

    public interface LLMClient {

        default String summariseDiff(String prevConent, String currContent) {
            return "Returns a mocked summary of the differences which changes on each invocation becase it is appended with a UUID - " + UUID.randomUUID().toString();
        }

        default String select(String prompt) {
            if (prompt.toLowerCase().contains("api")) {
                return "GitHub";
            } else if (prompt.toLowerCase().contains("security")) {
                return "Slack";
            } else {
                return "InternalPortal";
            }
        }
    }

    public static class MockLLMClient implements LLMClient {

    }

    public static class BedrockLLM implements LLMClient {


        private final BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.of("us-west-2")) // use an enabled Bedrock region
                .credentialsProvider(DefaultCredentialsProvider.create()) // Leverages IMDS credentials which are picked up in the AWS VSCode environment set up for the hackathon
                .build();

        @Override
        public String summariseDiff(String prevContent, String currContent) {
            String prompt = """
                    Compare the following two versions of content and generate a short, high-level summary of the meaningful differences:

                    --- PREVIOUS VERSION ---
                    %s

                    --- CURRENT VERSION ---
                    %s

                    Respond in plain English with the most important updates developers should know about.
                    """.formatted(prevContent, currContent);
            return invokeBedrock(prompt);
        }

        public String select(String differenceSummary) {
            String prompt = "Review the difference summary provided here " + differenceSummary + " and suggest all the engagement channels this summary should be posted on, the engagement channel options are Github, Slack #security, Slack #APIs, Slack #DevOps, InternalDevPortal";
            return invokeBedrock(prompt);
        }

        private String invokeBedrock(String prompt) {
            try {
                String titanPrompt = "\n\nHuman: " + prompt + "\n\nAssistant:";

                String jsonPayload = """
                        {
                                  "inputText": %s,
                                  "textGenerationConfig": {
                                    "temperature": 0.5,
                                    "topP": 0.9,
                                    "maxTokenCount": 5000,
                                    "stopSequences": []
                                  }
                                }
                        """.formatted(jsonEscape(titanPrompt));

                InvokeModelRequest request = InvokeModelRequest.builder()
                        .modelId(Constants.MODEL_ID) // or any available model
                        .contentType("application/json")
                        .accept("application/json")
                        .body(SdkBytes.fromUtf8String(jsonPayload))
                        .build();

                InvokeModelResponse response = client.invokeModel(request);
                String responseText = extractOutputText(response.body().asUtf8String());
                System.out.println("Model output " + responseText);
                return responseText;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String jsonEscape(String s) {
            return "\"" + s
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "") + "\"";
        }

        private String extractOutputText(String json) {
            // Basic manual parsing; replace with a JSON parser in production
            int start = json.indexOf("\"outputText\":");
            if (start == -1)
                return json;
            int quoteStart = json.indexOf("\"", start + 13);
            int quoteEnd = json.indexOf("\"", quoteStart + 1);
            return quoteStart != -1 && quoteEnd != -1
                    ? json.substring(quoteStart + 1, quoteEnd)
                    : json;
        }
    }
}
