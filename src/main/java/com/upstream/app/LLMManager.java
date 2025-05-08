package com.upstream.app;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;



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

    public static class MockLLMClient implements LLMClient{

    }

    public class BedrockLLM implements LLMClient {

        /*
         * @TODO: update credentials to use implementation
         */
        private final BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.of("us-east-1")) // use an enabled Bedrock region
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY")
                        )
                )
                .build();
        private final ObjectMapper objectMapper = new ObjectMapper();

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
                String escapedPrompt = prompt.replace("\"", "\\\"");

                String jsonPayload = """
                        {
                          "prompt": "Human: %s
                        Assistant:",
                          "max_tokens": 300
                        }
                        """.formatted(escapedPrompt);

                InvokeModelRequest request = InvokeModelRequest.builder()
                        .modelId("anthropic.claude-v2") // or any available model
                        .contentType("application/json")
                        .accept("application/json")
                        .body(SdkBytes.fromUtf8String(jsonPayload))
                        .build();

                InvokeModelResponse response = client.invokeModel(request);
                String responseString = response.body().asUtf8String();
                JsonNode root = objectMapper.readTree(responseString);

                return root.has("completion") ? root.get("completion").asText() : responseString;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}