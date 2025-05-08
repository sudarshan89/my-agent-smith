package com.upstream.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ActivityImplementations {
    static LLMManager.LLMClient llmClient = new LLMManager.MockLLMClient();

    public static class FetchContentActivityImpl implements Acitivities.FetchContentActivity {
        @Override
        public String fetch(String url) {
            System.out.println("Invoked FetchContentActivity");
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch content from " + url, e);
            }
        }
    }

    public static class SummariseContentDiffActivityImpl implements Acitivities.SummariseContentDiffActivity {

        @Override
        public String summariseContentDiff(String prevContent, String currentContent) {
            System.out.println("Invoked SummariseContentDiffActivity");
            return llmClient.summariseDiff(prevContent, currentContent);
        }
    }

    public static class SelectPromotionChannelActivityImpl implements Acitivities.SelectPromotionChannelActivity {

        @Override
        public String select(String summary) {
            System.out.println("Invoked SelectPromotionChannelActivity");
            return llmClient.select(summary);
        }
    }

    public static class PromoteContentActivityImpl implements Acitivities.PromoteContentActivity {
        @Override
        public void promote(String summary, String channel) {
            System.out.printf("🚀 Promoting to %s:\n%s\n", channel, summary);
            // Optionally: Add real Slack/GitHub posting logic here.
        }
    }
}