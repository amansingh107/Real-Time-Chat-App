# Distributed Real-Time Chat System 🚀

A high-performance, real-time chat application architected to demonstrate **Distributed System** principles. 

Unlike a standard chat app that runs on a single server, this project is designed to scale horizontally. It uses **Spring WebFlux** for non-blocking concurrency, **Redis Pub/Sub** to synchronize state across multiple server instances, and **Nginx** to load balance WebSocket connections.

---

## 🏗 Architecture

The core challenge in distributed chat systems is **State Management**. If User A is connected to Server 1 and User B is connected to Server 2, Server 1 cannot send messages directly to User B.

**The Solution:**
1.  **Load Balancing:** Nginx acts as a reverse proxy, distributing incoming WebSocket connections across multiple backend containers using a Round-Robin algorithm.
2.  **Event Bus:** When a server receives a message, it publishes it to a **Redis Topic**.
3.  **Synchronization:** All server instances subscribe to Redis. When an event occurs (Message, Typing, Join), Redis broadcasts it to *all* servers, ensuring that a user connected to *any* instance receives the update.

### System Diagram
<img width="1408" height="768" alt="image" src="https://github.com/user-attachments/assets/63fdeb7f-244a-4af6-8658-c4edcecdf4bc" />


## ✨ Features

* **Real-Time Messaging:** Instant message delivery with sub-millisecond latency.
* **Distributed Architecture:** Multiple backend instances running simultaneously.
* **Typing Indicators:** Ephemeral "User is typing..." events synced across the cluster.
* **Presence Detection:** Real-time Join/Leave notifications.
* **Reactive Backend:** Built with Spring WebFlux to handle thousands of concurrent connections per thread.
* **Containerized:** Fully Dockerized environment (App + Redis + Nginx).

---

## 🛠 Tech Stack

* **Backend:** Java 17, Spring Boot 3 (WebFlux), Netty
* **Data/Messaging:** Redis (Reactive Pub/Sub)
* **Frontend:** React.js, Vite, CSS3
* **Infrastructure:** Docker, Docker Compose, Nginx
* **Build Tools:** Maven, npm

---

## 🚀 Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 17+ (JDK)
* Node.js & npm

### 1. Backend & Infrastructure Setup
We use Docker Compose to spin up the entire infrastructure (Redis, Nginx, and 2 replicas of the App).

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/your-username/distributed-chat.git](https://github.com/your-username/distributed-chat.git)
    cd distributed-chat
    ```

2.  **Build the Java JAR**
    *Note: You must rebuild the JAR whenever you change Java code.*
    ```bash
    mvn clean package -DskipTests
    ```

3.  **Start the Containers**
    This will start Redis, Nginx (Port 80), and 2 Backend Instances.
    ```bash
    docker-compose up --build
    ```

### 2. Frontend Setup
Open a new terminal window.

1.  **Navigate to frontend**
    ```bash
    cd frontend
    ```

2.  **Install Dependencies**
    ```bash
    npm install
    ```

3.  **Run the Client**
    ```bash
    npm run dev
    ```
    Open your browser to `http://localhost:5173`.

## 🧪 How to Verify Distributed Scaling

To prove the system is truly distributed:

1.  Open the App in **Tab 1**. (You are likely connected to *Container A*).
2.  Open the App in **Incognito Mode** (Tab 2). (Nginx should route you to *Container B*).
3.  Send a message from Tab 1.
4.  **Result:** Tab 2 receives the message instantly via the Redis Bridge.

You can also view the logs to see which container processed the request:
```bash
docker logs -f distributed-chat-app-1
docker logs -f distributed-chat-app-2
```
## 📂 Project Structure

```bash
├── src/main/java/com/chat/system
│   ├── config/          # WebSocket & Redis Configuration
│   ├── handler/         # WebSocket Handler (Incoming/Outgoing logic)
│   ├── model/           # Data Models (ChatMessage, Enum Types)
│   └── service/         # Redis Pub/Sub Service
├── nginx/               # Nginx configuration for Load Balancing
├── frontend/            # React Application
├── Dockerfile           # Backend container definition
├── docker-compose.yml   # Infrastructure orchestration
└── pom.xml              # Java Dependencies
```

## 🔮 Future Improvements
1. Authentication: Integrate JWT / OAuth2 to secure WebSocket connections.
2. Message Persistence: Store chat history in MongoDB or PostgreSQL.
3. Private Rooms: Logic to handle dynamic room creation and subscription.
