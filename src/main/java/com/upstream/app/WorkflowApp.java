package com.upstream.app;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.util.UUID;


public class WorkflowApp {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        String workflowId = UUID.randomUUID().toString();
        System.out.println("Starting a new workflow " + workflowId);
        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setTaskQueue("CONTENT_AMPLIFIER_TASK_QUEUE")
            .setWorkflowId(workflowId)
            .setCronSchedule("*/1 * * * *") // every 1 minutes
            .build();

        ContentAmplifierWorkflow workflow = client.newWorkflowStub(ContentAmplifierWorkflow.class, options);
        WorkflowClient.start(workflow::run);

        System.out.println("Workflow started and the app is terminating. Long live the workflow ...");
    }
}
