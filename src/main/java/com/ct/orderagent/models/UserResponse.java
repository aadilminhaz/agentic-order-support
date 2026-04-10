package com.ct.orderagent.models;


import java.util.UUID;

public class UserResponse {
    private UUID userId;
    private UUID sessionId;
    private String response;

    public UserResponse(UUID userId, UUID sessionId, String response) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.response = response;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
