# 🏋️‍♂️ AI-Powered Fitness Application

An intelligent **microservices-based fitness tracking and recommendation system** built with **Spring Boot and AI integration**.  
Users provide their workout details in JSON format, and the system generates **personalized analysis, improvements, workout suggestions, and safety guidelines** using the **Gemini AI API**.  

---

## 🚀 Features

- 📊 **Activity Tracking** – Input structured fitness activity data (type, duration, calories, metrics).  
- 🤖 **AI-Powered Recommendations** – Detailed performance analysis with improvements, workout suggestions, and safety tips.  
- 🔐 **Authentication & Authorization** – Integrated with **Keycloak** for secure access.  
- ☁️ **Microservices Architecture** – Managed with **Spring Cloud (Eureka, Config Server, API Gateway)**.  
- 📨 **Asynchronous Messaging** – **RabbitMQ** used for communication and event handling.  
- 💾 **Database Persistence** – **MySQL** for storing user activities and recommendations.  
- 🐳 **Containerization** – Docker support for deployment.  

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot, Spring Cloud (Eureka, Config Server, API Gateway)  
- **Security:** Keycloak  
- **Messaging:** RabbitMQ  
- **Database:** MySQL  
- **AI Integration:** Gemini API  
- **Tools:** Docker, Postman  

---

## 📋 Prerequisites

Before running this project, make sure you have:

- **Java 17+**  
- **Maven 3.9+**  
- **MySQL** (running on port `3306`)  
- **RabbitMQ** (running on port `5672`, management UI on `15672`)  
- **Keycloak** (configured with a realm and client for authentication)  
- **Gemini API Key** (for AI integration)  
- **Docker & Docker Compose** *(optional, for containerized setup)*  

---

## 📥 Input Example

Send a JSON payload like this to the API (via Postman or any client):

```json
{
  "userId": "your user id",
  "type": "RUNNING",
  "duration": 45,
  "caloriesBurned": 400,
  "additionalMetrics": {
    "distance": 5.2,
    "averageSpeed": 6.9
  }
}
```

## 🔎 How It Works

### 1. User Input (API Request)
- A client (Postman, web app, etc.) sends a JSON payload with activity data (type, duration, calories, metrics).

### 2. API Gateway & Authentication
- Request goes through the **API Gateway** which routes to backend services.  
- **Keycloak** handles authentication and authorization; only valid tokens are allowed.  

### 3. Activity Service (Spring Boot)
- Validates and persists the incoming activity to **MySQL**.  
- Publishes an **activity-recorded event** to **RabbitMQ** for downstream processing.  

### 4. AI Service (Gemini API Integration)
- Consumes the RabbitMQ event (or API-triggered task).  
- Builds a **prompt** based on activity details and calls the **Gemini API**.  
- Parses the AI response into structured JSON:  
  - `analysis`  
  - `improvements`  
  - `suggestions`  
  - `safety`  

### 5. Response Back to User
- The generated recommendation is returned via the **API**.  
- Optionally stored for **future retrieval**.  

---

## 🤝 Contributions

We welcome contributions from the community! Whether it's a **bug fix, new feature, or improvement**—your help is appreciated.  

### Steps to Contribute

1. **Fork the Repository**  
   Click the **Fork** button on the repo page to create your own copy.  

2. **Clone your fork**  
   ```bash
   git clone https://github.com/SubediBinod/ai-fitness-app.git
   cd ai-fitness-app
   ```
3. **Create a feature branch**
```bash
   git checkout -b feature-name
```
4. **Make changes & commit**
   ``` bash
   git add .
   git commit -m "Brief description of your changes"
   ```
   
5. **Push changes**
 ``` bash
 git push origin feature-name
```

6. **Open a Pull Request (PR)**
   
   --Go to the original repository and submit a pull request explaining your changes.




   
 
