package com.upstream.app;

import com.upstream.app.LLMManager.BedrockLLM;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;


public class WorkerApp {

    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        // @TODO: Uncomment to use bedrock implementation
        //ActivityImplementations.llmClient = new LLMManager.BedrockLLM();
        // @TODO: Mock implementation
        ActivityImplementations.llmClient = new LLMManager.MockLLMClient();

        Worker worker = factory.newWorker("CONTENT_AMPLIFIER_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(ContentAmplifierWorkflowImpl.class);
        worker.registerActivitiesImplementations(
                new ActivityImplementations.FetchContentActivityImpl(),
                new ActivityImplementations.SummariseContentDiffActivityImpl(),
                new ActivityImplementations.SelectPromotionChannelActivityImpl(),
                new ActivityImplementations.PromoteContentActivityImpl()
        );
        factory.start();
        System.out.println("Worker started.");
    }
}
