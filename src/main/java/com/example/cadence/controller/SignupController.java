package com.example.cadence.controller;

import com.example.cadence.workflow.SignupWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupController {

    private final WorkflowClient workflowClient;

    @Autowired
    public SignupController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @GetMapping("/signup")
    public String signup(
            @RequestParam String email,
            @RequestParam(defaultValue = "false") boolean fail) {

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue("SIGNUP_TASK_LIST")
                .build();

        SignupWorkflow workflow = workflowClient.newWorkflowStub(SignupWorkflow.class, workflowOptions);

        WorkflowClient.start(workflow::startSignup, email, fail);

        return "Workflow started for email: " + email;
    }
}
