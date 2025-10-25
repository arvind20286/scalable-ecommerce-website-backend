# Scalable Ecommerce Website Backend

A backend system built for a scalable ecommerce website, composed of microservices, containerised and ready for production.

---

## 🧱 Project Structure

This project uses the following services (each in its own module):
- `eureka-service-api` — service registry
- `gateway-service-api` — API gateway / routing layer
- `user-service-api` — handles user data, authentication
- `product-service-api` — handles products, catalog
- `shopping-service-api` — handles shopping cart, wishlist
- `order-service-api` — order processing
- `payment-service-api` — payment handling
- `notification-service-api` — sends notifications (email/SMS)
- `docker-compose.yml` — orchestrates all services using Docker

---

## ✅ Features

- Microservices architecture: each service handles a specific domain.
- Scalability: services can be scaled independently.
- Docker‑based deployment: easy to spin up all services locally or in a cloud.
- Clean separation of concerns: user, product, order, payment, etc.
- Future‑ready: you can add new services (e.g., analytics, review service) easily.

---

## 🚀 Getting Started (Local)

### Prerequisites
- Java JDK (version used in project)
- Docker & Docker Compose installed
- Git

### Setup
1. Clone the repo:
   ```bash  
   git clone https://github.com/arvind20286/scalable-ecommerce-website-backend.git  
   cd scalable-ecommerce-website-backend  
   ```  
2. Start all services:
   ```bash  
   docker-compose up --build  
   ```  
3. Access the services (for example):
    - Gateway at `http://localhost:<gateway-port>`
    - User Service at `http://localhost:<user-service-port>/...`  
      Replace `<gateway-port>` etc. with the ports configured in each service.
4. Test endpoints using Postman / CURL.

---

## 🧩 API Endpoints

### 🔐 Authentication Service (`http://localhost:8081/api/auth`)

#### **1. Register (Admin / Client)**
**POST** `/register`

**Request:**
```json
{
  "name": "Arvind",
  "lastName": "Kumar",
  "email": "ak1@gmail.com",
  "password": "1234",
  "role": "CLIENT",
  "address": "F-32 Malviya Nagar, New Delhi-110022",
  "phone": "0000000001"
}
```

**Response:**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

---

#### **2. Login (Admin / Client)**
**POST** `/login`

**Request:**
```json
{
  "email": "ak@gmail.com",
  "password": "1234"
}
```

**Response:**  
Same as registration response (JWT token)

---

### 🛒 Product Service (`http://localhost:8083/api/product`)

#### **3. Add / List a Product**
**POST** `/save`

**Request:**
```json
{
  "name": "iPhone 16",
  "description": "iPhone 16(Space grey) 256GB",
  "price": 6.5,
  "category": "A",
  "stock": 2
}
```

**Response:**
```json
{
  "id": 2,
  "name": "iPhone 16",
  "description": "iPhone 16(Space grey) 256GB",
  "price": 6.5,
  "category": "A",
  "stock": 2
}
```

---

### 🛍 Shopping Cart Service (`http://localhost:8082/api/shopping`)

#### **4. Add Item to Cart**
**POST** `/add-to-cart`

**Request:**
```json
{
  "idUser": 3,
  "idProduct": 1,
  "quantity": 1
}
```

**Response:**
```json
{
  "id": 2,
  "idUser": 3,
  "email": "ak1@gmail.com",
  "total": 6.5,
  "cartItems": [
    {
      "id": 2,
      "idProduct": 1,
      "nameProduct": "iPhone 16",
      "quantity": 1,
      "unitPrice": 6.5
    }
  ]
}
```

---

### 📦 Order Service (`http://localhost:8085/api/orders`)

#### **5. Create Order for Client**
**POST** `/create/{userId}`

**Example:**  
`/create/3`

**Response:**
```json
{
  "orderId": 1,
  "name": "Arvind",
  "lastName": "Kumar",
  "email": "ak1@gmail.com",
  "role": "CLIENT",
  "address": "F-32 Malviya Nagar, New Delhi-110022",
  "phone": "0000000001",
  "items": [
    {
      "id": 1,
      "idProduct": 1,
      "nameProduct": "iPhone 16",
      "quantity": 1,
      "unitPrice": 6.5,
      "totalPrice": 6.5
    }
  ],
  "totalAmount": 6.5,
  "orderStatus": "PENDING",
  "orderDate": "2025-10-19T20:32:46.219855"
}
```

---

## 🛠 Tech Stack

- Backend language: **Java** (Spring Boot)
- Service registry: **Eureka**
- API Gateway: **Spring Cloud Gateway**
- Containerisation: **Docker**
- Communication: **REST APIs** between services
- Persistence: **MySQL / PostgreSQL / MongoDB** (per service basis)
- (Optional) **Message queue / event bus** for async flows

---

## 🔧 Configuration

Each service exposes configuration properties under `application.yml` or `application.properties`. Key areas to configure:
- Service ports
- Database connection settings
- Service registry URL(s)
- Gateway routing rules
- Logging & monitoring settings

---

## 📦 Deployment

To deploy to production (example):
1. Update environment‑specific configs (prod DB, prod service registry).
2. Build Docker images for each service:
   ```bash  
   docker build -t <repo>/user-service:latest ./user-service-api  
   ```  
3. Push Docker images to a registry (Docker Hub / AWS ECR / GCP Container Registry).
4. Use `docker-compose.yml` or Kubernetes manifests for orchestration.
5. Monitor logs and performance; scale services as needed.

---

## 🤝 Contributing

Contributions are welcome! Here’s how you can help:
- Fork the repository and create a pull request.
- Fix bugs, improve services, add documentation.
- Add unit/integration tests for services.
- Improve CI/CD pipeline or monitoring setup.

Before submitting a pull request, please make sure:
- Code builds and all tests pass.
- New features are documented in this README (or separate docs).
- Services still integrate properly (registry, gateway, etc).

---

## 📚 Resources & References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Netflix / Eureka](https://spring.io/projects/spring-cloud)
- [Docker Documentation](https://docs.docker.com/)
- [Microservices Patterns](https://microservices.io/patterns/index.html)

---
