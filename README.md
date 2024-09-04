# Java Online Marathon

## To-Do List Project

### Exception Handling

Create package exception and implement GlobalExceptionHandler - exception handling for custom exception.

Create "Error" page. 
Page should contain information about exception (message exception).

Create 404 page and 500 page.

Implement exception handling with redirection to "Error" page.

On `service` layer methods

- create and update should throw NullEntityReferenceException when user try to create or update empty object
- find and delete should throw EntityNotFoundException exception from jakarta.persistence package when user try to read or delete unexisted object



Three users with ADMIN and USER roles are stored in the database.

| Login         | Password | Role  |
|---------------|:--------:|:-----:|
| mike@mail.com |   1111   | ADMIN |
| nick@mail.com |   2222   | USER  |
| nora@mail.com |   3333   | USER  |

A user with the ADMIN role has full access to all resources.
