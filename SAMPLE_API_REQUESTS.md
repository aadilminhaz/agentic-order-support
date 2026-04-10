# Sample Requests for Order Agent Inference API

## API Endpoint
```
POST http://localhost:8080/agent
```

## Request Headers
```
Content-Type: application/json
```

## Sample Request Payloads

### Example 1: Basic Request (with minimal fields)
User ID and Session ID will be auto-generated if not provided.

```json
{
  "input": "What is the status of my order?"
}
```

### Example 2: Request with User ID
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "input": "I want to refund my order"
}
```

### Example 3: Complete Request (with all fields)
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionId": "660e8400-e29b-41d4-a716-446655440001",
  "input": "Can you help me track my order #12345?"
}
```

### Example 4: Complex Order Inquiry
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionId": "660e8400-e29b-41d4-a716-446655440001",
  "input": "I need to check the status of my order and potentially initiate a return if it hasn't been shipped yet"
}
```

## Sample Response

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionId": "660e8400-e29b-41d4-a716-446655440001",
  "response": "Your order #12345 is currently in processing and will be shipped within 2-3 business days. You'll receive a tracking number via email once it ships."
}
```

## cURL Examples

### Basic Request
```bash
curl -X POST http://localhost:8080/agent \
  -H "Content-Type: application/json" \
  -d '{
    "input": "What is the status of my order?"
  }'
```

### Request with User ID and Session ID
```bash
curl -X POST http://localhost:8080/agent \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "sessionId": "660e8400-e29b-41d4-a716-446655440001",
    "input": "Can you help me track my order?"
  }'
```

### Using Variables
```bash
USER_ID="550e8400-e29b-41d4-a716-446655440000"
SESSION_ID="660e8400-e29b-41d4-a716-446655440001"
INPUT="I want to refund my order"

curl -X POST http://localhost:8080/agent \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": \"$USER_ID\",
    \"sessionId\": \"$SESSION_ID\",
    \"input\": \"$INPUT\"
  }"
```

## Using with Postman

1. **Create a new POST request**
2. **URL**: `http://localhost:8080/agent`
3. **Headers Tab**:
   - Key: `Content-Type`
   - Value: `application/json`
4. **Body Tab** (select `raw` and `JSON`):
   ```json
   {
     "userId": "550e8400-e29b-41d4-a716-446655440000",
     "sessionId": "660e8400-e29b-41d4-a716-446655440001",
     "input": "What is the status of my order?"
   }
   ```
5. **Click Send**

## Request Field Details

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `userId` | UUID (String) | No | Unique identifier for the user. If not provided, a new UUID will be generated. |
| `sessionId` | UUID (String) | No | Session identifier to maintain conversation context. If not provided, a new UUID will be generated. |
| `input` | String | Yes | The user's query or request to the agent. |

## Response Field Details

| Field | Type | Description |
|-------|------|-------------|
| `userId` | UUID (String) | The user ID associated with the request. |
| `sessionId` | UUID (String) | The session ID for the conversation. |
| `response` | String | The agent's response to the user's input. |

## Notes

- If `userId` and `sessionId` are not provided, they will be auto-generated
- The same `sessionId` can be reused across multiple requests to maintain conversation context
- The `input` field is required and should contain the user's query
- Responses are returned synchronously

## Testing Tips

1. **Keep the same `sessionId`** to maintain conversation history
2. **Vary the `input`** to test different agent capabilities
3. **Use the same `userId`** for related requests to simulate a single user
4. Test with order-related queries like:
   - "What is the status of my order?"
   - "I want to refund my order"
   - "Can you help me track my shipment?"
   - "How do I return this product?"

