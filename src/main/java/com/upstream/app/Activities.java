package com.upstream.app;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

public class Activities {
    @ActivityInterface
    public interface FetchContentActivity {
        @ActivityMethod
        String fetch (String url);
    }

    @ActivityInterface
    public interface SummariseContentDiffActivity {
        @ActivityMethod
        String summariseContentDiff (String prevContent, String currentContent);
    }

    @ActivityInterface
    public interface SelectPromotionChannelActivity {
        @ActivityMethod
        String select (String summary);
    }

    @ActivityInterface
    public interface PromoteContentActivity {
        @ActivityMethod
        void promote (String summary, String channel);
    }
}