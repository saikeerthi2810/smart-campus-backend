# Smart Campus Service Request & Complaint Management System — Backend

A Spring Boot REST API for managing student service requests and campus complaints at Anurag University.

## 🚀 Technology Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Hibernate
- MySQL 8+
- Maven
- JUnit and Mockito

## ✨ Features

- User authentication and role-based access
- Student service request and complaint submission
- Complaint status tracking
- Staff assignment by administrators
- Staff workflow for resolving complaints
- Student feedback and rating submission
- RESTful API endpoints
- Centralized exception handling
- Input validation
- Unit and integration tests

## 👥 User Roles

| Role | Responsibilities |
|---|---|
| Student | Submit complaints, track status, and provide feedback |
| Admin | View complaints, assign staff, and manage status |
| Staff | View assigned complaints and update progress |

## 📋 Prerequisites

Install the following before running the application:

- Java 17
- Maven
- MySQL Server
- Git

## ⚙️ Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE smart_campus;