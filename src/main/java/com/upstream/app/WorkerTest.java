package com.upstream.app;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class WorkerTest {

    private static final BedrockRuntimeClient client = BedrockRuntimeClient.builder()
            .region(Region.of("us-west-2"))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    public static void main(String[] args) {
        System.out.println("Worker " + DefaultCredentialsProvider.create().resolveCredentials().toString());
        String modelId = "amazon.titan-text-express-v1";
        String prompt = "What is the capital of New Hampshire?";

        String payload = """
                {
                  "inputText": "%s",
                  "textGenerationConfig": {
                    "temperature": 0,
                    "topP": 0.9,
                    "maxTokenCount": 200,
                    "stopSequences": []
                  }
                }
                """.formatted(prompt);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(payload))
                .build();

        InvokeModelResponse response = client.invokeModel(request);
        String responseBody = response.body().asUtf8String();

        System.out.println("Response: " + responseBody);
    }
}