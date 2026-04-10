package com.ct.orderagent.inference;

import com.ct.orderagent.models.UserRequest;
import com.ct.orderagent.models.UserResponse;
import com.google.adk.agents.BaseAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class InferenceService {
    private final InMemoryRunner runner;
    private final ConcurrentMap<String, Session> inMemorySessionCache = new ConcurrentHashMap<>();

    public InferenceService(BaseAgent baseAgent) {
        this.runner = new InMemoryRunner(baseAgent);
    }

    public UserResponse inference(UserRequest request) {
        UUID userId = request.getUserId() != null ? request.getUserId() : UUID.randomUUID();
        UUID sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID();

        String cacheKey = userId + ":" + sessionId;
        Session session = inMemorySessionCache.computeIfAbsent(cacheKey, key ->
                runner.sessionService()
                        .createSession(runner.appName(), userId.toString(), null, sessionId.toString())
                        .blockingGet()
        );

        Content userMessage = Content.fromParts(Part.fromText(request.getInput()));
        StringBuilder answerBuilder = new StringBuilder();
        runner.runAsync(userId.toString(), session.id(), userMessage)
                .blockingForEach(event -> {
                    String content = event.stringifyContent();
                    if (content != null && !content.isBlank()) {
                        answerBuilder.append(content);
                    }
                });

        return new UserResponse(userId, sessionId, answerBuilder.toString());
    }


}
