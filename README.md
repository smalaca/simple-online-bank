Online Bank Application

A minimal online banking REST API built with Spring Boot 3 following a hexagonal-style architecture. It supports customers, accounts, deposits, withdrawals, and transfers (with fake FX rates). The app runs locally with H2 or in Docker with MySQL.

How to run
- Prerequisites: Java 21, Maven 3.9+, Docker (for container run)
- Run tests: mvn test

Environments
- DEV
  - Profile: dev
  - Port: 8080
  - DB: H2 in-memory
  - Security: no security at all (all endpoints are open)
  - Data: demo data seeded on startup
- UAT
  - Profile: uat
  - Port: 8081
  - DB: MySQL (docker service uat-db)
  - Security: HTTP Basic with a single user; username uat, password uatpw
  - Data: no demo data
- PROD
  - Profile: prod
  - Port: 8082
  - DB: MySQL (docker service prod-db)
  - Security: HTTP Basic with a single user; username prod, password prodpw
  - Data: no demo data

Security notes
- In all environments, the endpoints /health and /offers/accounts are publicly accessible (no authentication required).
- In UAT/PROD, all other endpoints require HTTP Basic auth (see credentials above).

Run locally (DEV, H2)
- mvn spring-boot:run -Dspring-boot.run.profiles=dev
- App: http://localhost:8080
- H2 Console (optional): http://localhost:8080/h2-console (JDBC URL jdbc:h2:mem:onlinebank)

Run all environments with Docker
- Build jar: mvn -q -DskipTests package
- Start everything: docker compose up --build
- Apps:
  - DEV:  http://localhost:8080
  - UAT:  http://localhost:8081 (auth: uat/uatpw)
  - PROD: http://localhost:8082 (auth: prod/prodpw)
- Databases:
  - UAT MySQL:  localhost:3307 (user: bank, pw: bankpw)
  - PROD MySQL: localhost:3308 (user: bank, pw: bankpw)

Stopping containers
- docker compose down

REST Endpoints
- Base URL: depends on environment (see above)

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
