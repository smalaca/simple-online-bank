Online Bank Application

A minimal online banking REST API built with Spring Boot 3 following a hexagonal-style architecture. It supports customers, accounts, deposits, withdrawals, and transfers (with fake FX rates). The app runs locally with H2 or in Docker with MySQL.

How to run
- Prerequisites: Java 21, Maven 3.9+, Docker (for container run)
- Run tests: mvn test
- Run locally (H2 in-memory DB):
  - mvn spring-boot:run
  - App: http://localhost:8080
  - H2 Console (optional): http://localhost:8080/h2-console (JDBC URL jdbc:h2:mem:onlinebank)
- Run with Docker + MySQL:
  - Build jar: mvn -q -DskipTests package
  - Start stack: docker compose up --build
  - App (default): http://localhost:8081
  - To use a different host port, set APP_PORT, e.g. PowerShell: `$env:APP_PORT=9090; docker compose up --build` (then open http://localhost:9090)
  - MySQL: localhost:3306 (user: bank, pw: bankpw)
- Profiles:
  - Default (local): H2, seeds initial demo data
  - docker: MySQL, no seeding component

REST Endpoints
- Base URL: http://localhost:8081 (Docker default) or http://localhost:8080 (local run)

Customers
- POST /api/customers
  - Body: {"customerNumber":"C011","name":"Alice"}
  - 201 Created, Location: /api/customers/C011
- GET /api/customers
  - 200 OK, example: [{"customerNumber":"C001","name":"Customer 1"}, ...]
- GET /api/customers/{number}
  - 200 OK, example: {"customerNumber":"C001","name":"Customer 1"}

Accounts
- POST /api/accounts
  - Body: {"customerNumber":"C001","currency":"USD"}
  - 201 Created, Location: /api/accounts/{accountNumber}
- GET /api/accounts/customer/{customerNumber}
  - 200 OK, example: [{"accountNumber":"...","customerNumber":"C001","currency":"USD","balance":1000.0000}]
- GET /api/accounts/{accountNumber}
  - 200 OK, example: {"accountNumber":"...","customerNumber":"C001","currency":"USD","balance":1000.0000}
- POST /api/accounts/{accountNumber}/deposit
  - Body: {"amount": 100}
  - 200 OK (no body)
- POST /api/accounts/{accountNumber}/withdraw
  - Body: {"amount": 50}
  - 200 OK (no body)
- POST /api/accounts/transfer
  - Body: {"sourceAccount":"<src>","targetAccount":"<tgt>","amount": 10}
  - 200 OK (no body)

Notes
- Amounts are decimals; server rounds to 4 fractional digits when applying operations.
- Currency codes are one of: USD, EUR, GBP.

Business rules
- Customers
  - A customer has a unique customerNumber and a name.
- Accounts
  - An account has a unique accountNumber, belongs to a customer, and has exactly one currency.
  - New accounts start with balance 0.0000.
  - A customer can have multiple accounts.
- Transactions
  - Supported operations: deposit, withdrawal, transfer.
  - Each transaction is stored with a unique UUID and timestamp.
  - Deposit increases balance; withdrawal decreases balance and requires sufficient funds.
  - Transfer debits the source and credits the target. If currencies differ, the amount is converted using a fake RatesEngine (USD base: EUR=0.9, GBP=0.8). Results rounded to 4 decimals.
- Validation & constraints
  - Amount must be positive for all operations.
  - Insufficient funds cause an error on withdrawal/transfer.
  - Unique constraints on customerNumber and accountNumber.

Initial demo data (local profile)
- On the app seeds demo data:
  - 15 customers: C001 .. C015
  - Accounts and starting balances (USD):
    - 5 customers with no account
    - 5 customers with 1 account each: 1000, 2000, 6340, 12, 123
    - 2 customers with 2 accounts each: 10000, 20879, 892, 8231
    - 3 customers with 3 accounts each: 2134, 4325, 5432, 1234, 5678, 98760 (cycled as needed)

Security
- Spring Security is enabled but currently permits all requests (HTTP Basic enabled). Rules can be tightened later.
