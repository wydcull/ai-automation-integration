# ShopEasy Chatbot — Frontend

React + Vite chat UI for the AI Chatbot backend. Connects to the Spring Boot API at `/api/chat` and provides a session-based support chat experience.

## Prerequisites

- **Node.js 18+** and npm
- Backend running at `http://localhost:8080` (see the [root README](../README.md))

## Quick Start

### 1. Install dependencies

```bash
npm install
```

### 2. Configure environment

Create a `.env` file in this folder:

```env
VITE_API_BASE_URL=http://localhost:8080/api/chat
```

### 3. Start the dev server

```bash
npm run dev
```

Open **http://localhost:3000** in your browser.

## Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start dev server with HMR (port 3000) |
| `npm run build` | Build for production (`dist/`) |
| `npm run preview` | Preview the production build locally |
| `npm run lint` | Run ESLint |

## Project Structure

```
src/
├── api/
│   └── chatApi.js          # Axios calls to backend
├── hooks/
│   └── useChat.js          # Session, messages, send/clear logic
├── components/
│   ├── ChatWindow.jsx      # Main layout (header, list, input)
│   ├── MessageList.jsx     # Message list + suggestion chips
│   ├── MessageBubble.jsx   # Single user/bot message bubble
│   └── ChatInput.jsx       # Text input + send button
├── App.jsx                 # Root component
└── App.css                 # Chat UI styles
```

## How It Works

### Session management

- A session ID is generated on first visit and stored in `localStorage` under `chatSessionId`
- The same session is reused across page refreshes so chat history persists
- **New chat** creates a fresh session ID and clears the current view
- **Clear chat** deletes history on the backend for the current session

### API integration

All requests go through `src/api/chatApi.js`:

| Function | Backend endpoint |
|----------|------------------|
| `sendMessage(sessionId, message)` | `POST /send` |
| `getRecentHistory(sessionId, limit)` | `GET /history/{sessionId}/recent` |
| `clearHistory(sessionId)` | `DELETE /history/{sessionId}` |
| `checkHealth()` | `GET /health` |

On load, `useChat` fetches recent history for the current session and displays it.

### UI features

- **Suggestion chips** — Quick prompts when the chat is empty (products, order tracking, return policy)
- **Typing indicator** — Shown while waiting for the AI response
- **Error banner** — Displays API errors (validation, rate limit, service unavailable)
- **Enter to send** — Press Enter to send; Shift+Enter for a new line
- **Message limit** — Input capped at 1000 characters (matches backend validation)

## Environment Variables

| Variable | Required | Example |
|----------|----------|---------|
| `VITE_API_BASE_URL` | Yes | `http://localhost:8080/api/chat` |

Vite only exposes variables prefixed with `VITE_`. Restart the dev server after changing `.env`.

## Production Build

```bash
npm run build
npm run preview
```

The build output is in `dist/`. Serve it with any static file host (Nginx, Vercel, Netlify, etc.).

For production, set `VITE_API_BASE_URL` to your deployed backend URL and ensure CORS on the backend allows your frontend origin (see `cors.allowed-origins` in the backend `application.properties`).

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `Network Error` / CORS | Confirm backend is running and `cors.allowed-origins` includes `http://localhost:3000` |
| Empty responses | Check backend logs and Groq API key configuration |
| History not loading | Verify `VITE_API_BASE_URL` ends with `/api/chat` |
| Session validation error | Session IDs must be alphanumeric with `-` and `_` only |

## Related

- [Root project README](../README.md) — Backend setup, API reference, database, and architecture
