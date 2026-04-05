# Finance Dashboard Backend

A production-grade REST API backend for a finance dashboard system, built with **Spring Boot 4**, **Spring Security 7 + JWT**, and **MongoDB Atlas**.

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4 (Spring MVC) |
| Security | Spring Security 7 + JJWT 0.11.5 |
| Database | MongoDB Atlas (cloud) |
| ODM | Spring Data MongoDB |
| Validation | Jakarta Bean Validation |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Language | Java 17 |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+ (or use `./mvnw`)
- Internet access (for MongoDB Atlas connection)

### Run the Application
```bash
./mvnw spring-boot:run
# Windows:
.\mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080**

### API Documentation (Swagger UI)
Once running, open: **http://localhost:8080/swagger-ui.html**

---

## 🔐 Authentication Flow

The API uses **JWT Bearer token authentication**.

1. **Register** a user: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → copy the `token` from the response
3. **Use the token** in the `Authorization` header: `Bearer <token>`

### Roles

| Role | Permissions |
|---|---|
| **VIEWER** | Read-only: view records & dashboard data |
| **ANALYST** | Read-only with access to summary & insights |
| **ADMIN** | Full access: create, update, delete records and manage users |

---

## 📡 API Reference

### Auth Endpoints (`/api/auth`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | ❌ Public | Register a new user |
| `POST` | `/api/auth/login` | ❌ Public | Login and get JWT token |

**Register Request:**
```json
{
  "name": "John Admin",
  "email": "john@example.com",
  "password": "secret123",
  "role": "ADMIN"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "id": "68...",
  "name": "John Admin",
  "email": "john@example.com",
  "role": "ADMIN"
}
```

---

### Financial Records (`/api/records`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/records` | VIEWER/ANALYST/ADMIN | List records (paginated, filterable) |
| `GET` | `/api/records/{id}` | VIEWER/ANALYST/ADMIN | Get single record |
| `POST` | `/api/records` | ADMIN only | Create a record |
| `PUT` | `/api/records/{id}` | ADMIN only | Update a record |
| `DELETE` | `/api/records/{id}` | ADMIN only | Soft delete a record |

**Query Parameters for GET /api/records:**

| Param | Type | Example | Description |
|---|---|---|---|
| `type` | string | `INCOME` or `EXPENSE` | Filter by record type |
| `category` | string | `Rent` | Filter by category |
| `startDate` | date | `2025-01-01` | Filter start date (ISO 8601) |
| `endDate` | date | `2025-03-31` | Filter end date (ISO 8601) |
| `page` | int | `0` | Page number (0-indexed, default: 0) |
| `size` | int | `10` | Page size (default: 10) |

**Create/Update Record Request:**
```json
{
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2025-03-01",
  "notes": "Monthly salary"
}
```

---

### Dashboard (`/api/dashboard`)

All dashboard endpoints require: VIEWER, ANALYST, or ADMIN role.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Total income, expenses, net balance |
| `GET` | `/api/dashboard/category-summary` | Category-wise totals (all types) |
| `GET` | `/api/dashboard/monthly-trends` | Month-by-month income/expense breakdown |
| `GET` | `/api/dashboard/recent-activity` | Last 10 financial records by date |

**Summary Response:**
```json
{
  "totalIncome": 65000.00,
  "totalExpense": 15500.00,
  "netBalance": 49500.00
}
```

---

### User Management (`/api/users`)

All user management endpoints require **ADMIN** role.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create a user (admin-created) |
| `PUT` | `/api/users/{id}` | Update user name/email/password |
| `PATCH` | `/api/users/{id}/role` | Change user role |
| `PATCH` | `/api/users/{id}/status` | Activate/deactivate user |
| `DELETE` | `/api/users/{id}` | Delete user permanently |

---

## ❌ Error Responses

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2025-03-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Financial record not found with id: xyz",
  "path": "/api/records/xyz",
  "validationErrors": null
}
```

| Status | Cause |
|---|---|
| `400` | Validation failure or bad input (see `validationErrors` field) |
| `401` | Missing or invalid JWT token |
| `403` | Authenticated but insufficient role |
| `404` | Resource not found |
| `500` | Unexpected server error |

---

## 📮 Postman Collection

Import `Finance_Dashboard_API.postman_collection.json` into Postman.

**Collection variables are auto-set:**
- `token` — set automatically after login/register
- `userId` — set after registration or get-user
- `recordId` — set after record creation

**Recommended test order:**
1. Auth > Register Admin User
2. Auth > Login (Admin) ← token is auto-stored
3. Financial Records > Create Record (Income, Expense, etc.)
4. Dashboard > Get Summary, Category Summary, Monthly Trends
5. User Management > Get All Users, Update Role, etc.

---

## 🏗️ Project Design Decisions

### 1. Soft Delete for Financial Records
Financial records use a `deleted` boolean flag. Deletion via `DELETE /api/records/{id}` sets `deleted=true` rather than physically removing the document. This preserves audit history.

### 2. Role-Based Access via @PreAuthorize
Role enforcement is implemented using Spring Security's method-level `@PreAuthorize` annotations. Roles are embedded as JWT claims at login time.

### 3. MongoDB Aggregation for Dashboard
Dashboard summary and monthly trends use native MongoDB aggregation pipelines through `MongoTemplate` for efficient server-side computation.

### 4. Separate DTOs for Input/Output
The API uses dedicated DTOs (`RegisterRequest`, `FinancialRecordRequest`, `CreateUserRequest`, etc.) instead of exposing domain entities directly, preventing over-posting attacks.

### 5. Password Security
All passwords are hashed using BCrypt before storage. Password updates also go through BCrypt encoding.

---

## 🧪 Assumptions

- The first registered user can specify their own role (for development flexibility)
- In production, self-registration should be restricted or role should always default to VIEWER
- Financial records are never hard-deleted; only soft-deleted
- MongoDB Atlas is used as the cloud database (credentials in `application.properties`)
