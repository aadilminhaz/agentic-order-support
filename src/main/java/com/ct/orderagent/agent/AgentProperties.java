package com.ct.orderagent.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "com.ct.orderagent.agent")
public record AgentProperties(
        String name,
        String description,
        String aiModel,
        Resource systemPrompt
) {}