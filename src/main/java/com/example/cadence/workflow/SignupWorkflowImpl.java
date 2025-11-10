package com.example.cadence.workflow;

import com.example.cadence.activity.SignupActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class SignupWorkflowImpl implements SignupWorkflow {

    private static final Logger logger = Workflow.getLogger(SignupWorkflowImpl.class);

    private final SignupActivities activities;

    public SignupWorkflowImpl() {
        this.activities = Workflow.newActivityStub(SignupActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumAttempts(1)
                                .build())
                        .build());
    }

    @Override
    public void startSignup(String email, boolean forceEmailFailure) {
        logger.info("Workflow started for email: {}", email);

        try {
            // 1. Create the user
            activities.createUser(email);

            // 2. Try to send the welcome email
            activities.sendWelcomeEmail(email, forceEmailFailure);

        } catch (ActivityFailure e) {
            // This is the compensation path (Saga)
            logger.error("Email failed for {}. Starting compensation.", email, e);
            activities.deleteUser(email); // Compensating transaction
            throw e; // Rethrow to fail the workflow
        }

        logger.info("Workflow completed successfully for email: {}", email);
    }
}
