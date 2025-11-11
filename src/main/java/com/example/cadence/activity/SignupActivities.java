package com.example.cadence.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface SignupActivities {
    void createUser(String email);
    void sendWelcomeEmail(String email, boolean forceFailure);
    void deleteUser(String email);
    void sendConfirmationEmail(String customerId);
    void chargeCustomer(String customerId);
}
