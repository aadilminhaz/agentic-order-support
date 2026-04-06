# Agentic Order Support

Agentic Order support powered by Google ADK & Gemini, built with Java 21.

### Run
There are two ways to run the agent:
- via the command-line:
mvn compile exec:java -Dexec.mainClass=com.ct.orderagent.AgentCliRunner
- Or the ADK Dev UI:
mvn compile exec:java \
-Dexec.mainClass="com.google.adk.web.AdkWebServer" \
-Dexec.classpathScope="compile"