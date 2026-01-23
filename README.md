# Polyglot Distributed SMS Service

## Overview

This project is a **local polyglot microservices setup** that demonstrates refactoring a monolithic SMS notification system into two communicating services:

* **SMS Sender** – Java / Spring Boot (API Gateway + Kafka Producer)
* **SMS Store** – Go / net/http (Kafka Consumer + MongoDB persistence)

The system uses **Kafka** for event-driven communication, **Redis** for user blocking, and **MongoDB** for message storage.

### Data Flow

1. Client calls **SMS Sender** API to send an SMS
2. Java service validates user (Redis block list)
3. Java service mocks a 3rd‑party SMS vendor call
4. Java service publishes an SMS event to Kafka
5. Go service consumes event and stores SMS record in MongoDB
6. Client can query message history from Go service

---

## Starting the Application (Local)

### Start All Services

Use the provided script:

```bash
./start.sh
```
---

## Service 1: SMS Sender (Java / Spring Boot)

### API: Send SMS

**POST** `/v1/sms/send`

#### Request Body

```json
{
  "phoneNumber": "9876543210",
  "message": "Hello Java SMS Service"
}
```

#### Response

```json
{
  "status": "SUCCESS",
  "message":"SMS processed"
}
```
---

## Service 2: SMS Store (Go / net/http)

### API: Get SMS History

**GET** `/v1/user/{userId}/messages`

#### Example

```bash
curl http://localhost:9092/v1/user/123/messages
```

#### Response

```json
[
  {
    "phoneNumber": "9876543210",
    "message": "Hello Java SMS Service",
    "status": "SUCCESS / PROVIDER_ERROR",
  }
]
```

---

## Kafka Topics

* `sms-messages`

  * Produced by: SMS Sender (Java)
  * Consumed by: SMS Store (Go)

