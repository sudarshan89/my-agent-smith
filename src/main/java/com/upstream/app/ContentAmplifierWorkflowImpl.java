package com.upstream.app;


import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ContentAmplifierWorkflowImpl implements ContentAmplifierWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .build();

    private final Acitivities.FetchContentActivity fetcher = Workflow.newActivityStub(Acitivities.FetchContentActivity.class, options);
    private final Acitivities.SummariseContentDiffActivity diffSummariser = Workflow.newActivityStub(Acitivities.SummariseContentDiffActivity.class, options);
    private final Acitivities.SelectPromotionChannelActivity channelSelector = Workflow.newActivityStub(Acitivities.SelectPromotionChannelActivity.class, options);

    private final Acitivities.PromoteContentActivity promoteContentActivity = Workflow.newActivityStub(Acitivities.PromoteContentActivity.class, options);


    //change this
    private static final String MONITORED_URL = "https://example.com/blog";
    private String lastContentHash = "";
    private String lastContent = "";

    @Override
    public void run() {
        System.out.println("Hello Workflow " + Workflow.getInfo().getWorkflowId() + " Run Id " + Workflow.getInfo().getRunId());

        String content = fetcher.fetch(MONITORED_URL);
        String hash = Integer.toString(content.hashCode());

        if (!hash.equals(lastContentHash)) {
            String summary = diffSummariser.summariseContentDiff(lastContent, content);
            String channel = channelSelector.select(summary);
            promoteContentActivity.promote(summary,channel);
            lastContentHash = hash;
            lastContent = content;
        }
    }
}
