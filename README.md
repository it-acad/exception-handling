# Practical Java Course

## To-Do List Project

### Exception Handling and Error Pages

### 📋 Task Overview

### 1. **Implement Global Exception Handling:**
- Create a package named `exception` and implement a `GlobalExceptionHandler` class to handle exceptions globally within the application.
- Add custom exceptions:
    - **`NullEntityReferenceException`**: Thrown when attempting to create or update an empty object in the service layer.
    - **`EntityNotFoundException`**: Thrown when attempting to read or delete a non-existent object in the service layer.

### 2. **Create Custom Error Pages:**
- Develop a generic **Error page** that displays details about exceptions (e.g., exception messages).
- Create a **404 page** for "Page Not Found" errors.
- Create a **500 page** for "Internal Server Error" scenarios.
- Implement exception handling that redirects users to the relevant error pages when exceptions occur.

### 3. **Enhance Service Layer Methods:**
- Modify the service layer to throw custom exceptions:
    - The **`create`** and **`update`** methods should throw `NullEntityReferenceException` if the input object is null.
    - The **`find...`** and **`delete`** methods should throw `EntityNotFoundException` if the entity does not exist.

### 4. **Final Submission:**
- Upload the updated project to a **GitHub repository**.
- Record a **short video** (2-5 minutes) demonstrating the exception handling and the custom error pages in action.

## 📌 Notes
- Include the **links** to the GitHub repository and the demo video as part of the final submission.
- Ensure that the error pages display meaningful information to the user, and logs capture detailed information for debugging purposes.

## 🛠️ Requirements
- Create an `exception` package for custom exceptions and the `GlobalExceptionHandler`.
- Use a **logging framework** (e.g., SLF4J, Logback) to log exception details.
- Design custom HTML error pages (`error.html`, `404.html`, `500.html`) to handle different types of errors.

Feel free to explore the project and watch the demonstration video for a detailed walkthrough of the implemented functionality.