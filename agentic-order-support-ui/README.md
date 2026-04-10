# AGENTIC Order Support UI

A clean, minimal chat interface that connects to your AI agent backend.

## Features
- New `userId` and `sessionId` generated on every page load
- Filters raw `FunctionCall`/`FunctionResponse` noise from the API — shows only the final message
- Configurable backend URL via environment variable

---

## Run with Docker (recommended)

```bash
# Build and run on port 3000
docker compose up --build

# With a custom backend URL
REACT_APP_BASE_URL=http://<agent-inference-url>:8080 docker compose up --build
e.g.
REACT_APP_BASE_URL=http://localhost:8080 docker compose up --build
```

Visit: http://localhost:3000

---

## Run locally (dev)

```bash
npm install
REACT_APP_BASE_URL=http://localhost:8080 npm start
```

---

## Configuration

| Variable | Default | Description |
|---|---|---|
| `REACT_APP_BASE_URL` | `http://localhost:8080` | Base URL of your agent API |

The app calls `POST {BASE_URL}/agent` with:
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "sessionId": "660e8400-e29b-41d4-a716-446655440011",
  "input": "user message here"
}
```

---

## Session management - ID Generation Logic 

On every page load:
- `userId` starts with `550e8400-e29b-41d4-a716-44665544` + last 4 digits from current microsecond timestamp
- `sessionId` starts with `660e8400-e29b-41d4-a716-44665544` + last 4 digits from current microsecond timestamp
