Loan Management API
===================

Loan Management API is a **Java Spring Boot** application for managing customers, loans, and installments.
It supports creating and retrieving customers, assigning loans, making installment payments, and viewing loan details.
With integrated Swagger UI, developers can easily test and explore available endpoints.

---

 Table of Contents
--------------------
1. Features
2. Technologies Used
3. Project Architecture
4. Data Flow
5. Installation & Run
6. API Documentation
7. Example API Endpoints
8. Example Requests & Responses
9. Error Handling
10. Database Information
11. Security Details
12. Future Improvements
13. License

---

 Features
-----------
- **Customer Management**: Add and retrieve customer details.
- **Loan Management**: Create loans for customers and check loan status.
- **Installment Management**: Pay installments for existing loans.
- **Swagger UI** for interactive API documentation.
- **Spring Security** for basic authentication and endpoint protection.

---

 Technologies Used
--------------------
- Java 17+
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- Gradle
- Swagger (Springfox)
- Lombok
- H2 Database (in-memory, default configuration)

---

 Project Architecture
-----------------------
```
src/main/java/com/creditmodule/loanmanagementapi/
│
├── config/          # Security & Swagger configuration
├── controller/      # REST controllers handling API requests
├── dto/             # Data Transfer Objects (request/response)
│   ├── request/     # Input payload structures
│   └── response/    # Output payload structures
├── entity/          # JPA entities representing database tables
└── LoanmanagementapiApplication.java  # Application entry point
```

---

 Data Flow
------------
1. **Client sends a request** to an endpoint (e.g., POST `/api/customers`).
2. **Controller** validates and forwards data to the Service layer (if implemented).
3. **Entity** is created or updated using JPA Repository.
4. **Response DTO** is returned to the client.
5. Swagger UI can be used to visualize this process.

---

 Installation & Run
---------------------
1. **Clone the Repository**
```
git clone https://github.com/erginbalta/LoanManagementApi.git
cd LoanManagementApi
```
2. **Build with Gradle**
```
./gradlew build
```
Windows:
```
gradlew.bat build
```
3. **Run the Application**
```
./gradlew bootRun
```
Windows:
```
gradlew.bat bootRun
```

---

🌐 API Documentation
--------------------
Once the app is running, Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

---

 Example API Endpoints
------------------------
| Method | Endpoint                | Description |
|--------|-------------------------|-------------|
| POST   | /api/customers           | Add a new customer |
| GET    | /api/customers/{id}      | Get customer by ID |
| POST   | /api/loans               | Create a loan |
| GET    | /api/loans/{id}          | Get loan details |
| POST   | /api/installments/pay    | Pay an installment |

---

📤 Example Requests & Responses
--------------------------------

**Create Customer Request**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "123456789"
}
```

**Create Customer Response**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "123456789",
  "createdAt": "2025-08-11T10:15:30"
}
```

**Create Loan Request**
```json
{
  "customerId": 1,
  "amount": 5000,
  "termMonths": 12,
  "interestRate": 5.5
}
```

**Create Loan Response**
```json
{
  "id": 10,
  "customerId": 1,
  "amount": 5000,
  "termMonths": 12,
  "interestRate": 5.5,
  "status": "ACTIVE"
}
```

---

 Error Handling
-----------------
The API uses standard HTTP status codes:
- **200 OK**: Successful operation
- **201 Created**: Resource successfully created
- **400 Bad Request**: Invalid input data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected error

Error responses are returned in JSON format:
```json
{
  "timestamp": "2025-08-11T10:20:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found",
  "path": "/api/customers/99"
}
```

---

 Database Information
-----------------------
- **Default DB**: H2 in-memory
- Access Console: `http://localhost:8080/h2-console`
- Example JDBC URL: `jdbc:h2:mem:testdb`
- Tables:
  - `customers`
  - `loans`
  - `installments`
- username: sa
-password: password
-h2 console jdbc url : jdbc:h2:mem:loandb

---

 Security Details
-------------------
-Admin
  -username: admin
  -password: admin123
-Customer
  -username: customer
  -password: customer123
- Basic authentication configured via **Spring Security**.
- Unauthorized requests return HTTP `401 Unauthorized`.
- Authentication logic can be extended for JWT or OAuth2.

---

 Future Improvements
-----------------------
- Implement role-based access control (RBAC).
- Add pagination for listing customers and loans.
- Introduce unit and integration tests.
- Support multiple database engines.
- Add email/SMS notifications for payment reminders.


