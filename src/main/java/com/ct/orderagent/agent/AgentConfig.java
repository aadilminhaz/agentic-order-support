package com.ct.orderagent.agent;

import com.ct.orderagent.tools.OrderTools;
import com.ct.orderagent.tools.RefundTools;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.FunctionTool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.Charset;

@Configuration
@EnableConfigurationProperties(AgentProperties.class)
public class AgentConfig {

    @Bean
    public BaseAgent agent(AgentProperties agentProperties, OrderTools orderTools, RefundTools refundTools) throws IOException {

        return LlmAgent.builder()
                .name(agentProperties.name())
                .description(agentProperties.description())
                .model(agentProperties.aiModel())
                .instruction(agentProperties.systemPrompt().getContentAsString(Charset.defaultCharset()))
                .tools(
                        FunctionTool.create(orderTools, "checkOrderStatus"),
                        FunctionTool.create(orderTools, "reAttemptDelivery"),
                        FunctionTool.create(refundTools, "getRefundStatus"),
                        FunctionTool.create(refundTools, "processRefund")
                )
                .build();
    }
}
