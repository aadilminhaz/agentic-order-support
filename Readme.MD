# Agentic Order Support

Agentic Order support powered by Google ADK & Gemini, built with Java 21.

### Setup

- Setup Gemini key in the sprint boot application's environment variable section:
- > GEMINI_API_KEY=<your_gemini_api_key>
- Note: No need to write "export" also don't use the key under quotes

### Run
There are two ways to run the agent:
- via the command-line:
mvn compile exec:java -Dexec.mainClass=com.ct.orderagent.AgentCliRunner
- Or the ADK Dev UI:
mvn compile exec:java \
-Dexec.mainClass="com.google.adk.web.AdkWebServer" \
-Dexec.classpathScope="compile"