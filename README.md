# Taskflow API
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-✔️-blue.svg)](https://www.docker.com/)

A **production-style** task management REST API built with modern Java and Spring Boot. Designed to demonstrate professional backend architecture, secure authentication, and containerized development.

## 📋 Overview

TaskFlow API is a multi-user task management backend inspired by tools like Jira and Trello. It provides a secure REST API for user authentication, project management, and task organization.

This project was built to showcase:
- **Clean Architecture**
- **Production-ready** Spring Boot configuration
- **Stateless authentication** using JWT
- **Containerized development** with Docker Compose
- **Modern Java features**

---

## 🧱 Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 25 LTS |
| **Framework** | Spring Boot | 4.0.5 |
| **Security** | Spring Security + JWT | 6.5+ |
| **Database** | PostgreSQL | 15 (Alpine) |
| **ORM** | Spring Data JPA / Hibernate | - |
| **Build Tool** | Maven | - |
| **Containerization** | Docker + Docker Compose | - |
| **Utilities** | Lombok | - |

---

## ✨ Features

### ✅ Implemented

| Feature | Description |
|---------|-------------|
| **User Registration** | Create new account with email/password |
| **User Login** | Authenticate and receive JWT token |
| **JWT Authentication** | Stateless, secure token-based auth |
| **Project CRUD** | Create, read, update, and delete projects |
| **User-Project Association** | Each project belongs to an owner |
| **Input Validation** | Request validation with meaningful error messages |
| **Password Encryption** | BCrypt hashing for secure storage |
| **Containerized Database** | PostgreSQL running in Docker |

### 🚧 In Progress

| Feature | Status |
|---------|--------|
| Task Management (CRUD) | Entities & Repos Complete |
| Comments System | Entities & Repos Complete |
| Role-Based Access Control | Coming Soon |
| Global Exception Handling | Coming Soon |
| API Documentation (Swagger) | Coming Soon |

---

## 🏗️ Architecture

### Layered Architecture Flow

```mermaid
graph TB
    subgraph Client["Client Application"]
        REQ[HTTP Request]
        RES[HTTP Response]
    end
    
    subgraph Controller["Controller Layer"]
        AC[AuthController]
        PC[ProjectController]
        HC[HealthController]
    end
    
    subgraph Service["Service Layer"]
        AS[AuthService]
        PS[ProjectService]
        CDS[CustomUserDetailsService]
        JS[JwtService]
    end
    
    subgraph Repository["Repository Layer"]
        UR[UserRepository]
        PR[ProjectRepository]
        TR[TaskRepository]
        CR[CommentRepository]
    end
    
    subgraph Model["Model Layer"]
        U[User Entity]
        P[Project Entity]
        T[Task Entity]
        C[Comment Entity]
    end
    
    subgraph Database["Database"]
        PG[(PostgreSQL)]
    end
    
    REQ --> Controller
    Controller --> Service
    Service --> Repository
    Repository --> Model
    Model --> PG
    PG --> Model
    Model --> Repository
    Repository --> Service
    Service --> Controller
    Controller --> RES
```

### Security Authentication Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant AuthManager
    participant UserDetailsService
    participant JwtService
    participant Database
    
    Note over Client,Database: Registration Flow
    Client->>AuthController: POST /api/auth/register
    AuthController->>AuthService: register(request)
    AuthService->>Database: Check email exists
    AuthService->>AuthService: Hash password (BCrypt)
    AuthService->>Database: Save user
    AuthService->>JwtService: generateToken(user)
    JwtService-->>AuthService: JWT token
    AuthService-->>Client: AuthResponse with token
    
    Note over Client,Database: Login Flow
    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: login(request)
    AuthService->>AuthManager: authenticate(credentials)
    AuthManager->>UserDetailsService: loadUserByUsername(email)
    UserDetailsService->>Database: Find user by email
    Database-->>UserDetailsService: User entity
    UserDetailsService-->>AuthManager: UserDetails
    AuthManager->>AuthManager: Verify password
    AuthManager-->>AuthService: Authentication
    AuthService->>JwtService: generateToken(user)
    JwtService-->>AuthService: JWT token
    AuthService-->>Client: AuthResponse with token
    
    Note over Client,Database: Protected Request Flow
    Client->>JwtFilter: Request + Bearer Token
    JwtFilter->>JwtService: validateToken(token)
    JwtService-->>JwtFilter: Valid/Invalid
    alt Token Valid
        JwtFilter->>UserDetailsService: loadUserByUsername(email)
        UserDetailsService->>Database: Find user
        Database-->>UserDetailsService: User entity
        JwtFilter->>JwtFilter: Set SecurityContext
        JwtFilter->>Controller: Forward request
        Controller-->>Client: Protected resource
    else Token Invalid
        JwtFilter-->>Client: 403 Forbidden
    end
```

### JWT Authentication Filter Chain

```mermaid
graph LR
    subgraph Filters["Spring Security Filter Chain"]
        direction LR
        F1[CsrfFilter<br/>Disabled]
        F2[CorsFilter]
        F3[JwtAuthenticationFilter<br/>Custom]
        F4[AuthenticationFilter]
        F5[AuthorizationFilter]
    end
    
    REQ[HTTP Request] --> F1
    F1 --> F2
    F2 --> F3
    F3 --> F4
    F4 --> F5
    F5 --> CTRL[Controller]
    
    F3 -.->|Extracts Token| JWT[JWT Service]
    JWT -.->|Validates| F3
    F3 -.->|Sets| SC[SecurityContext]
```

### Package Structure

```mermaid
graph TB
    subgraph Base["com.matthewholmes.taskflow"]
        direction TB
        CONFIG[config/<br/>SecurityConfig]
        CONTROLLER[controller/<br/>AuthController<br/>ProjectController<br/>HealthController]
        DTO[dto/<br/>AuthResponse<br/>RegisterRequest<br/>LoginRequest<br/>ProjectRequest<br/>ProjectResponse]
        MODEL[model/<br/>User<br/>Project<br/>Task<br/>Comment]
        REPO[repository/<br/>UserRepository<br/>ProjectRepository<br/>TaskRepository<br/>CommentRepository]
        SECURITY[security/<br/>JwtService<br/>JwtAuthenticationFilter]
        SERVICE[service/<br/>AuthService<br/>ProjectService<br/>CustomUserDetailsService]
    end
    
    CONFIG --> SECURITY
    CONTROLLER --> SERVICE
    CONTROLLER --> DTO
    SERVICE --> REPO
    SERVICE --> SECURITY
    SERVICE --> DTO
    REPO --> MODEL
```

### Entity Relationship Diagram

```mermaid
erDiagram
    USERS {
        uuid id PK
        string email UK
        string password
        string full_name
        enum role
        timestamp created_at
        timestamp updated_at
    }
    
    PROJECTS {
        uuid id PK
        string name
        string description
        uuid owner_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    TASKS {
        uuid id PK
        string title
        string description
        enum status
        enum priority
        uuid project_id FK
        uuid assignee_id FK
        uuid creator_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    COMMENTS {
        uuid id PK
        string content
        uuid task_id FK
        uuid author_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    USERS ||--o{ PROJECTS : owns
    USERS ||--o{ TASKS : assigned_to
    USERS ||--o{ TASKS : creates
    USERS ||--o{ COMMENTS : writes
    PROJECTS ||--o{ TASKS : contains
    TASKS ||--o{ COMMENTS : has
```

### Request Processing Flow

```mermaid
flowchart TD
    A[Client Request] --> B{Has Auth Header?}
    B -->|No| C{Is Public Endpoint?}
    B -->|Yes| D[Extract Bearer Token]
    
    C -->|Yes /api/auth/**| E[Process Request]
    C -->|No| F[Return 403 Forbidden]
    
    D --> G[Validate JWT Signature]
    G -->|Invalid| F
    G -->|Valid| H[Extract User Email]
    H --> I[Load User from Database]
    I -->|Not Found| F
    I -->|Found| J[Set SecurityContext]
    J --> E
    
    E --> K[Controller Layer]
    K --> L[Service Layer]
    L --> M[Repository Layer]
    M --> N[Database]
    N --> O[Return Response]
```

## 🏃 Quick Start

### Prerequisites
- **Java 25** or higher
- **Docker Desktop** (for PostgreSQL)
- **Maven**

### Step 1: Clone the repository
```bash
git clone https://github.com/matayoh14/taskflow-api.git
cd taskflow
```

### Step 2: Start PostgreSQL with Docker
```bash
docker-compose up -d
```
#### Verify it's running:
```bash
docker ps
```

### Step 3: Run the Application
```bash
./mvnw spring-boot:run      # Mac/Linux
mvnw.cmd spring-boot:run    # Windows
```
#### Or using IDE:
Run TaskflowApplication.java as a Java application.

### Step 4: Test the API
The sever starts on http://localhost:8080

---

## 📡 API Documentation

### Base URL
http://localhost:8080/api

### Authentication Endpoints

#### Register New User
Creates a new user account.

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login`    | Login, returns JWT | No |

**Register Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```
**Login Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securePass123"
}
```

**Auth Response Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "MEMBER"
}
```

### Project Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /projects | Create new project | Yes |
| GET | /projects | Get all user's projects | Yes |
| GET | /projects/{id} | Get project by ID | Yes |
| PUT | /projects/{id} | Update project | Yes |
| DELETE | /projects/{id} | Delete project | Yes |

**All Authenticated requests require header:**
```text
Authorization: Bearer <your_jwt_token>
```

### Health Check
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /health | Service health status (requires auth) |

---

## Database Schema
```mermaid
erDiagram
    USERS {
        uuid id PK
        string email UK
        string password
        string full_name
        enum role
        timestamp created_at
        timestamp updated_at
    }
    
    PROJECTS {
        uuid id PK
        string name
        string description
        uuid owner_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    TASKS {
        uuid id PK
        string title
        string description
        enum status
        enum priority
        uuid project_id FK
        uuid assignee_id FK
        uuid creator_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    COMMENTS {
        uuid id PK
        string content
        uuid task_id FK
        uuid author_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    USERS ||--o{ PROJECTS : owns
    USERS ||--o{ TASKS : assigned_to
    USERS ||--o{ TASKS : creates
    USERS ||--o{ COMMENTS : writes
    PROJECTS ||--o{ TASKS : contains
    TASKS ||--o{ COMMENTS : has
```

---

## What's Next
This project is actively being developed. Upcoming feature include:
```mermaid
timeline
    title TaskFlow API Roadmap
    section Completed
        User Authentication : JWT login/register
        Project CRUD : Full project management
        Database Setup : PostgreSQL + Docker
    section In Progress
        Task Management : CRUD operations
        Comments System : Task discussions
        Role-Based Access : Admin vs Member
    section Planned
        Global Exception Handling : Clean error responses
        Swagger Docs : Interactive API docs
        CI/CD Pipeline : GitHub Actions
        Deployment : Render/Railway/AWS
```
- **Task CRUD Operations** - Full task management within projects
- **Comments System** - Add and view comments on tasks
- **Role-Based Access Control** - Admin vs Member permissions
- **Global Exception Handling** - Consistent error responses
- **Swagger/OpenAPI Docs** - Interactive API documentation
- **CI/CD Pipeline** - GitHub Actions for automated testing
- **Deployment - Render/Railway/AWS deployment

---

## Author
**Matthew Holmes**

---

## 📄 License
This project is for portfolio demonstration purposes.

---

🙏 Acknowledgments

- Spring Boot team for the amazing framework
- JJWT library for JWT implementation
- Docker for containerization

