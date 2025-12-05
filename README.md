# Cadence Saga Orchestration Example

This project is a complete, runnable Spring Boot application that demonstrates the Saga Orchestration pattern using the Cadence orchestration engine. The example workflow is for a user signup process, which involves creating a user and sending a welcome email.

The key part of the demonstration is how the Saga pattern handles failures. If the "send welcome email" step fails, a compensating transaction is automatically executed to delete the user, ensuring data consistency.

## Project Structure

- `pom.xml`: Maven project setup with Spring Boot and Cadence dependencies.
- `docker-compose.yml`: Runs a local Cadence server for development.
- `src/main/resources/application.properties`: Connects the Spring application to the local Cadence server.
- `src/main/java/com/example/saga/`: The main application package.
  - `CadenceApplication.java`: The main Spring Boot application class.
  - `workflow/`: Contains the Cadence workflow interface and implementation (the orchestrator).
  - `activity/`: Contains the Cadence activity interface and implementation (the business logic).
  - `controller/`: A simple REST controller to trigger the workflow.

## Verifiable Test Plan

This plan provides step-by-step instructions to run the application and verify that both the success and failure (Saga compensation) paths are working correctly.

### Step 1: Start the Cadence Server

Open a terminal and run the following command to start the Cadence server in the background.

```bash
docker-compose up -d
```

This will download and run the `uber/cadence-server:master-auto-setup` image.

### Step 2: Run the Spring Boot Application

In a new terminal window, build and run the Spring Boot application using Maven.

```bash
mvn spring-boot:run
```

Wait for the application to start. You will see logs indicating that the Cadence worker has started and is polling for tasks on the `SIGNUP_TASK_LIST`.

### Step 3: Access the Cadence Web UI

Open your web browser and navigate to the Cadence Web UI:

- **URL:** [http://localhost:8088](http://localhost:8088)

In the UI, make sure the **`demo`** domain is selected in the top navigation bar. This is where you will see the workflows you are about to trigger.

### Step 4: Execute Test Cases

You will now trigger the workflow using `curl` and verify the outcome in both the Spring console logs and the Cadence Web UI.

---

### Test Case 1: The Success Path

This test simulates a successful user signup where all steps complete without errors.

#### Action:

Run the following `curl` command in your terminal.

```bash
curl -X GET 'http://localhost:8080/signup?email=success@example.com'
```

#### Verification:

1.  **Check your Spring Console Logs:** Look at the terminal where you ran `mvn spring-boot:run`. You must see the logs appear in the following specific order:
    ```
    ✅ ACTIVITY: Creating user success@example.com...
    ✉️ ACTIVITY: Sending welcome email to success@example.com...
    ✉️ ACTIVITY: Email sent successfully to success@example.com.
    ```

2.  **Check the Cadence Web UI:**
    - Refresh the UI at [http://localhost:8088](http://localhost:8088).
    - You will see a new workflow instance for `SignupWorkflow`.
    - **Verify that its status is "Completed".**

---

### Test Case 2: The Failure & Compensation (Saga) Path

This test simulates a failure during the "send email" step, which must trigger the compensating transaction ("delete user").

#### Action:

Run the following `curl` command. The `&fail=true` parameter tells the application to simulate a failure.

```bash
curl -X GET 'http://localhost:8080/signup?email=fail@example.com&fail=true'
```

#### Verification:

1.  **Check your Spring Console Logs:** Look at the application logs. You must see the logs appear in the following specific order, demonstrating the failure and the compensation:
    ```
    ✅ ACTIVITY: Creating user fail@example.com...
    ✉️ ACTIVITY: Sending welcome email to fail@example.com...
    🔥 SIMULATING FAILURE for fail@example.com!
    🔄 COMPENSATION: Deleting user fail@example.com...
    ```
    The presence of the `COMPENSATION` log is the key indicator that the Saga pattern worked.

2.  **Check the Cadence Web UI:**
    - Refresh the UI at [http://localhost:8088](http://localhost:8088).
    - You will see another new workflow instance.
    - **Verify that its status is "Failed".** This is the expected outcome, as the orchestrator correctly re-threw the exception after running the compensation logic.
