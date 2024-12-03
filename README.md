# Document Manager

## 📋 Project Description  
**Document Manager** is a microservice-based application for managing document flow and content in a media holding.

## ✨ Features  
- User registration and authentication (JWT).  
- Role-based access control (Admin/User).  
- CRUD operations for user and document management.  
- Structured data storage in **PostgreSQL**.  
- Unstructured data storage in **MongoDB**.  
- Content search with **Elasticsearch**.  
- Communication between services via **Kafka**.

## 🛠️ Tech Stack  
**Java 17**, **Spring Boot 3**, **PostgreSQL**, **MongoDB**, **Elasticsearch**, **Kafka**, **Docker**, **Docker Compose**, **Maven**, **Git**

## 🚀 Getting Started  
Clone the repository, build the project, and run it with Docker Compose:  
```bash
git clone https://github.com/your-username/document-manager.git && cd document-manager  
mvn clean install  
docker-compose up
