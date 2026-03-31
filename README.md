# Task Manager API

A RESTful backend application for managing tasks, built with 
Spring Boot and secured with user authentication.

## 🚀 Features
- User registration and login with authentication
- Create, update and delete tasks
- Tasks linked to authenticated users
- RESTful API architecture

# Task Manager API

A secure, full-featured RESTful Task Management backend built 
with Spring Boot, featuring role-based access control, JWT 
authentication, and email OTP verification.

## 🚀 Features

### 🔐 Security & Authentication
- User registration with *email OTP verification*
- Secure login with *JWT token* generation
- *Role-based access control* — separate Admin and User roles
- Protected routes — only authenticated users can access their data

### 👤 User Features
- Create, update and delete personal tasks
- View all assigned tasks

### 🛡️ Admin Features
- Admin-level login with elevated privileges
- Manage and oversee all users and tasks

## 🛠️ Built With
- Java
- Spring Boot
- Spring Security + JWT
- JavaMailSender (Email OTP)
- MySQL / PostgreSQL
- Maven

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/signup | Register new user |
| POST | /auth/validateOtp | Verify email OTP |
| POST | /auth/login | Login and receive JWT |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /Api/task/getAllTask/me | Get all user tasks |
| POST | /Api/task/create | Create a task |
| PATCH | /Api/task/updateTask/1 | Update a task |
| DELETE | /Api/task/deleteTask/1 | Delete a task |

## ⚙️ How to Run
1. Clone the repository
2. Configure database and mail settings in application.properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager
spring.mail.username=your-email
spring.mail.password=your-password
Run with mvn spring-boot:run
API runs on http://localhost:8080
🏗️ Architecture
REST API design
Stateless authentication via JWT
Layered architecture (Controller → Service → Repository)
Secured endpoints with Spring Security filter chain
2. Configure your database in application.properties
3. Run with mvn spring-boot:run
3. Configure your database in application.properties
4. Run with mvn spring-boot:run
5.
