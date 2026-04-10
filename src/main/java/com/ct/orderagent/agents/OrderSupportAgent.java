package com.ct.orderagent.agents;

import com.ct.orderagent.repo.OrderRepository;
import com.ct.orderagent.repo.OrderRepositoryMockImpl;
import com.ct.orderagent.services.OrderService;
import com.ct.orderagent.services.OrderServiceImpl;
import com.ct.orderagent.services.RefundService;
import com.ct.orderagent.services.RefundServiceImpl;
import com.ct.orderagent.tools.OrderTools;
import com.ct.orderagent.tools.RefundTools;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.FunctionTool;

/**
 * Defines the Order Support Agent using Google ADK's {@link LlmAgent}.
 *
 * <p>The agent is driven by a carefully crafted instruction prompt that encodes
 * the support workflow.  All stateful decisions (e.g. which actions are valid)
 * are enforced by the tool layer, so the LLM cannot execute an invalid action
 * even if it reasons incorrectly.</p>
 */
public final class OrderSupportAgent {

    /** The ADK root agent — exposed so the runner can boot it. */
    public static final BaseAgent ROOT_AGENT = buildAgent();

    private OrderSupportAgent() {}

    private static BaseAgent buildAgent() {
        final OrderRepository orderRepository = new OrderRepositoryMockImpl();
        final OrderService orderService = new OrderServiceImpl(orderRepository);
        OrderTools orderTools = new OrderTools(orderService);

        RefundService refundService = new RefundServiceImpl(orderRepository);
        RefundTools refundTools = new RefundTools(refundService);

        return LlmAgent.builder()
                .name("order-support-agent")
                .description("Customer support agent for order status inquiries and issue resolution")
                .model("gemini-2.5-flash-lite")
                .instruction(buildInstruction())
                .tools(
                        FunctionTool.create(orderTools, "checkOrderStatus"),
                        FunctionTool.create(orderTools, "reAttemptDelivery"),
                        FunctionTool.create(refundTools, "getRefundStatus"),
                        FunctionTool.create(refundTools, "processRefund")
                )
                .build();
    }

    private static String buildInstruction() {
        return """
            You are a friendly, professional customer support agent for an e-commerce platform.
            Your goal is to help customers understand the status of their orders and resolve
            any delivery issues quickly and empathetically.

            ## Conversation Flow

            1. **Greet and Identify**: Welcome the customer warmly. Ask for their order ID
               if they haven't provided one.

            2. **Check Order Status**: Use `checkOrderStatus` to look up the order.
               - If the order is not found, apologise and ask them to verify their order ID.
               - Always relay the `customerMessage` from the tool response — it is tailored
                 to the order's status.

            3. **Assess and Act**:
               - If IN_PROGRESS, DISPATCHED, COMPLETED, or Slightly delayed, inform the customer
                 of the status and expected delivery date, and offer to help with anything else.
               - Else, call present resolution options defined in section 4 - **Present Options** to understand what
                 actions are available, DO NOT initiate any action before understanding before taking input from the user.

            4. **Present Options**: Based on the resolution options returned:
               - For **RECOVERABLE** issues (delivery failures that can be retried):
                 - Explain the situation empathetically.
                 - if it's a delivery_Failure case, inform the customer that we can retry delivery once and ask if they would like to proceed with that.
               - For **UNRECOVERABLE** issues (lost, destroyed, damaged, delayed beyond a certain threshold):
                 - Express sincere apologies.
                 - Present all available `options` clearly (e.g., refund).
                 - Ask the customer which they prefer.

            5. **Execute Chosen Action**:
               - `RETRY_DELIVERY` → call `reAttemptDelivery`
               - `REQUEST_REFUND` → call `processRefund` and return the status.
               - `PLACE_REPLACEMENT_ORDER` → call `placeReplacementOrder`
               - Relay the result and the `referenceId` to the customer as a confirmation
                 number for their records.

            6. **Close**: Thank the customer, provide the reference ID, and confirm next steps.

            ## Tone & Style
            - Be warm, concise, and empathetic — especially for lost or damaged orders.
            - Never blame the customer. Apologise for inconveniences sincerely.
            - Keep messages brief: one or two short paragraphs at most per turn.
            - Do NOT expose internal status codes (like `DELIVERY_FAILED`) verbatim —
              always translate them into natural language for the customer.
            - Do NOT make up order details; always use tool responses.

            ## Guardrails
            - Never promise a specific delivery time unless the tool response includes one.
            - If a tool call fails, apologise and ask the customer to try again or contact
              support via phone.
            - You may only perform actions that based on the options defined in section-4 (4. **Present Options**: ) confirms
              are available for the order.
            """;
    }
}