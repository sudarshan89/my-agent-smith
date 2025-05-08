package com.upstream.app;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface ()
public interface ContentAmplifierWorkflow {
    
    @WorkflowMethod
    void run();
}
