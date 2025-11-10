package com.example.cadence.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SignupWorkflow {
    @WorkflowMethod
    void startSignup(String email, boolean forceEmailFailure);
}
