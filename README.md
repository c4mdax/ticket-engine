# TicketEngine: Concurrency & Actor Model Benchmark
URL: https://ticket-engine-demo.onrender.com/
## Overview
A proof-of-concept (PoC) backend engine designed to evaluate and compare two concurrency paradigms under stress: Threads using Java/VirtualThreads and the Actor Model using Scala/Pekko. It exposes a REST API that orchestrates 10,000 concurrent requests competing for a shared resource (100 available tickets), ensuring zero race conditions.
___
## 🔘 Architecture & Technical Decisions

### 1. Hybrid Multi-Language Core
- **Interoperability:** The project seamlessly integrates Java 21 and Scala 3 within the same Maven build lifecycle (`scala-maven-plugin`). This allows Spring Boot controllers (Java) to communicate directly with Actor systems (Scala).
- **Zero Database Overhead:** To strictly measure CPU and memory-level concurrency without network latency interference, all states are handled purely in-memory.

### 2. Concurrency Paradigms 

To solve the classic "Race Condition" problem in ticket sales, I implemented two distinct engines:

#### Java Engine: Shared Memory (ReentrantLock)
* **Mechanism:** Utilizes standard thread synchronization using `java.util.concurrent.locks.ReentrantLock`.
* **Behavior:** When a thread arrives, it requests the lock, blocking all other threads from accessing the `availableTickets` variable until the transaction is complete.
* **Performance:** Extremely fast in a single-node environment (nanoseconds to execute) due to direct hardware/memory access without Garbage Collector overhead.

#### Scala Engine: Actor Model (Apache Pekko)
* **Mechanism:** Implements the **Actor Model** using `org.apache.pekko`.
* **Behavior:** Instead of sharing memory, state is encapsulated within an Actor. Concurrent threads send asynchronous messages (commands) to the Actor's mailbox. The Actor processes these messages sequentially, one by one.
* **Performance:** Introduces computational overhead locally (instantiating Futures, Messages, and Mailboxes), but provides a lock-free, thread-safe architecture that is inherently ready to scale horizontally across distributed clusters.

### 3. REST API
- **Framework:** Built with **Spring Boot 3**.
- **Load Generation:** The simulation endpoint purposefully spawns thousands of concurrent asynchronous tasks at once to replicate a high-traffic DDoS-style ticket purchasing event.

### 4. Frontend Interface
- **Tech Stack:** Vanilla JavaScript, HTML, and CSS served statically by Spring Boot (I don't like doing frontend jeje).
___
## 🔘 Quick start Instructions

### 1. Prerequisites
- Java 21 (JDK)
- Git
- Maven (Embedded via wrapper)

### 2. Installation
```bash
git clone [https://github.com/your-username/ticket-engine.git](https://github.com/your-username/ticket-engine.git)
cd ticket-engine
# Compile both Java and Scala sources
./mvnw clean compile
```

### 3. Running Locally
**Start the Spring Boot Server**
```bash
./mvnw spring-boot:run
```
Navigate to `http://localhost:8080/`
___
## 🔘 Assumptions made
To ensure the reliability of the benchmark, the following technical assumptions were implemented:

1. **Transaction Simplicity:** The act of "buying a ticket" is abstracted to a simple mathematical subtraction. This ensures that the measured time reflects the concurrency mechanism's overhead, not external I/O delays (like database calls or payment gateways).
2. **Local vs. Distributed Reality:** It is assumed that while the Java `ReentrantLock` wins in local execution speed, it would catastrophically fail in a multi-server setup (Cluster). The Scala Pekko engine is intentionally over-engineered for this local test to demonstrate knowledge of distributed systems patterns.
3. **Hardware Constraints:** The baseline test implies 100 tickets and 10,000 buyers. The expectation is exactly 100 successful buys and 9,900 rejections. Any deviation indicates a race condition failure.
___
## 🔘 Result examples
### Without GUI (API JSON Response)
- **Java Engine Result (Fast local execution):**
```json
{
  "engineUsed": "java",
  "timeMs": 16,
  "initialTickets": 100,
  "successfulBuys": 100,
  "remainingTickets": 0,
  "rejectedRequests": 9900
}
```
- **Scala Engine Result (Actor Model overhead):**
```json
{
  "engineUsed": "scala",
  "timeMs": 238,
  "initialTickets": 100,
  "successfulBuys": 100,
  "remainingTickets": 0,
  "rejectedRequests": 9900
}
```
### With GUI
<img width="631" height="430" alt="ui" src="https://github.com/user-attachments/assets/207cda4e-6034-4919-9e59-62b2d6eafdad" />


___
## Cloud Deployment
The application is containerized using a **Multi-stage Dockerfile** to handle the hybrid Java/Scala compilation and is hosted on a Render free instance.
- **Live URL:** https://ticket-engine-demo.onrender.com/
___
