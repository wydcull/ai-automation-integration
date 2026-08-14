# AI Chatbot Project

An AI-powered e-commerce customer support chatbot for **ShopEasy**. The application combines a Spring Boot REST API with a React frontend to help customers with product discovery, order tracking, shipping questions, and general support — backed by the Groq LLM API and a PostgreSQL database.

## Features

- **Conversational AI support** — Uses Groq (`llama-3.3-70b-versatile`) to generate natural, context-aware replies
- **E-commerce integration** — Looks up products and orders from the database before answering
- **Smart product search** — Synonym-aware search (e.g. laptop/notebook, phone/smartphone)
- **Order tracking** — Detects order numbers (`ORD-*`) and tracking IDs (`TRK-*`) in user messages
- **Session-based chat history** — Persists conversations per session with pagination and recent-message retrieval
- **Conversation summarization** — Automatically summarizes long conversations (20+ messages) for better context
- **Rate limiting & caching** — Groq API rate limiting (30 RPM) with Caffeine response caching
- **Modern React UI** — Chat window with typing indicator, suggestion chips, new chat, and clear history

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.2.5, Spring Data JPA, WebFlux |
| Database | PostgreSQL |
| AI Provider | Groq API (OpenAI-compatible chat completions) |
| Frontend | React 19, Vite 8, Axios |
| Other | Lombok, Guava (rate limiting), Caffeine (caching) |

## Project Structure

```
AI_chatbot_project/
├── src/main/java/org/wydcull/ai_chatbot_project/
│   ├── AiChatbotProjectApplication.java   # Entry point
│   ├── config/                            # CORS, cache, request logging
│   ├── controller/                        # REST API (ChatController)
│   ├── dto/                               # Request/response DTOs
│   ├── exception/                         # Custom exceptions & global handler
│   ├── model/                             # JPA entities (Product, Order, ChatMessage, etc.)
│   ├── repository/                        # Spring Data repositories
│   └── service/                           # Business logic (Chat, Groq, Ecommerce, History)
├── src/main/resources/
│   └── application.properties             # Server, DB, Groq, CORS, logging config
├── chatbot-frontend/                      # React + Vite frontend
│   ├── src/
│   │   ├── api/chatApi.js                 # Backend API client
│   │   ├── hooks/useChat.js               # Chat state & session management
│   │   └── components/                    # ChatWindow, MessageList, ChatInput, etc.
│   └── .env                               # VITE_API_BASE_URL
└── pom.xml
```

## Architecture

```
┌─────────────────┐     HTTP (REST)      ┌──────────────────────────┐
│  React Frontend │ ───────────────────► │  Spring Boot API         │
│  (port 3000)    │                      │  (port 8080)             │
└─────────────────┘                      └────────────┬─────────────┘
                                                      │
                              ┌───────────────────────┼───────────────────────┐
                              ▼                       ▼                       ▼
                       ┌────────────┐          ┌────────────┐          ┌────────────┐
                       │ PostgreSQL │          │ Groq API   │          │ Caffeine   │
                       │ (products, │          │ (LLM)      │          │ Cache      │
                       │  orders,   │          └────────────┘          └────────────┘
                       │  chat)     │
                       └────────────┘
```

### Chat flow

1. User sends a message from the React UI with a session ID (stored in `localStorage`)
2. Backend saves the user message and loads recent conversation history
3. Relevant product/order data is extracted from PostgreSQL based on the message content
4. A system prompt + database context + history is sent to Groq
5. The AI reply is saved and returned to the frontend

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 18+** and npm
- **PostgreSQL** (running locally or remotely)
- **Groq API key** — [Get one at console.groq.com](https://console.groq.com)

## Getting Started

### 1. Database setup

Create a PostgreSQL database:

```sql
CREATE DATABASE "Ai_chatbot";
```

Tables are created automatically on startup via Hibernate (`spring.jpa.hibernate.ddl-auto=update`). Populate the `products`, `customers`, `orders`, and related tables with your e-commerce data before testing product and order queries.

Example test prompts (assuming matching data exists):

- `Do you have laptops?`
- `Track order ORD-12345`
- `Show me headphones`
- `What is your return policy?`

### 2. Backend configuration

Update `src/main/resources/application.properties` with your database credentials and Groq API key:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Ai_chatbot
spring.datasource.username=your_username
spring.datasource.password=your_password

groq.api.key=your_groq_api_key
groq.api.model=llama-3.3-70b-versatile
```

> **Security note:** Do not commit real API keys or database passwords. Consider using environment variables or a secrets manager for production deployments.

### 3. Run the backend

From the project root:

```bash
mvn spring-boot:run
```

The API starts at **http://localhost:8080**.

Verify it is running:

```bash
curl http://localhost:8080/api/chat/health
```

Expected response:

```json
{"status":"UP","service":"AI Chatbot API"}
```

### 4. Frontend configuration

Create or update `chatbot-frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api/chat
```

### 5. Run the frontend

```bash
cd chatbot-frontend
npm install
npm run dev
```

The UI opens at **http://localhost:3000**.

## API Reference

Base URL: `http://localhost:8080/api/chat`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Health check |
| `POST` | `/send` | Send a chat message |
| `GET` | `/history/{sessionId}` | Paginated chat history |
| `GET` | `/history/{sessionId}/recent` | Recent messages for UI/context |
| `GET` | `/session/{sessionId}/info` | Session statistics |
| `POST` | `/session/{sessionId}/summarize` | Generate conversation summary |
| `POST` | `/session/{sessionId}/extend` | Extend session TTL |
| `DELETE` | `/history/{sessionId}` | Clear chat history |
| `DELETE` | `/session/{sessionId}` | Delete entire session |

### Send message

**Request**

```http
POST /api/chat/send
Content-Type: application/json

{
  "sessionId": "session-1234567890-abc123",
  "message": "Do you have wireless headphones?"
}
```

**Response**

```json
{
  "sessionId": "session-1234567890-abc123",
  "reply": "I found 2 headphones for you: ...",
  "timestamp": "2026-08-14T10:30:00"
}
```

### Validation rules

- `sessionId` — Required, 1–100 characters, alphanumeric with `-` and `_` only
- `message` — Required, 1–1000 characters

## Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Backend server port |
| `groq.api.model` | `llama-3.3-70b-versatile` | Groq model name |
| `groq.api.temperature` | `0.7` | Response creativity |
| `groq.api.max-tokens` | `1000` | Max tokens per response |
| `chat.history.session-ttl-days` | `30` | Session expiration |
| `chat.history.summarization-threshold` | `20` | Messages before summarization |
| `cors.allowed-origins` | `http://localhost:3000,...` | Allowed frontend origins |

## Frontend Scripts

From `chatbot-frontend/`:

| Command | Description |
|---------|-------------|
| `npm run dev` | Start development server (port 3000) |
| `npm run build` | Production build |
| `npm run preview` | Preview production build |
| `npm run lint` | Run ESLint |

## Database Entities

| Entity | Table | Purpose |
|--------|-------|---------|
| `Product` | `products` | Catalog items (name, price, category, stock) |
| `Customer` | `customers` | Customer records |
| `Order` | `orders` | Orders with status, tracking, shipping |
| `OrderItem` | `order_items` | Line items per order |
| `ChatMessage` | `chat_messages` | User and assistant messages |
| `ChatSession` | `chat_sessions` | Session metadata, TTL, summaries |

## Error Handling

The API returns structured error responses with a trace ID for debugging:

```json
{
  "timestamp": "2026-08-14T10:30:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "AI service rate limit exceeded. Please try again in a moment.",
  "errorCode": "RATE_LIMIT_EXCEEDED",
  "path": "/api/chat/send",
  "traceId": "a1b2c3d4"
}
```

Common error codes: `VALIDATION_ERROR`, `SESSION_NOT_FOUND`, `RATE_LIMIT_EXCEEDED`, `AI_SERVICE_ERROR`, `EXTERNAL_SERVICE_ERROR`.

## Running Tests

```bash
mvn test
```

## License

This project is part of the Wydcull AI automation workspace.
