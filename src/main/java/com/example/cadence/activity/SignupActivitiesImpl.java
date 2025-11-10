package com.example.cadence.activity;

import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "SIGNUP_TASK_LIST")
public class SignupActivitiesImpl implements SignupActivities {

    private static final Logger logger = LoggerFactory.getLogger(SignupActivitiesImpl.class);

    @Override
    public void createUser(String email) {
        logger.info("✅ ACTIVITY: Creating user {}...", email);
        // Simulate database interaction
    }

    @Override
    public void sendWelcomeEmail(String email, boolean forceFailure) {
        logger.info("✉️ ACTIVITY: Sending welcome email to {}...", email);

        if (forceFailure) {
            logger.warn("🔥 SIMULATING FAILURE for {}!", email);
            throw new RuntimeException("Simulated Email Service Failure!");
        }

        logger.info("✉️ ACTIVITY: Email sent successfully to {}.", email);
        // Simulate email service call
    }

    @Override
    public void deleteUser(String email) {
        logger.info("🔄 COMPENSATION: Deleting user {}...", email);
        // Simulate database interaction to roll back user creation
    }
}
