package com.dims.AiAppPatterns.tools;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ItToolingAssistant {

    @UserMessage("User prompt: {{prompt}}")
    Result<String> ask(@V("prompt") String question);

    @UserMessage("User prompt: {{prompt}}")
    Result<String> ask(String sessionId, @V("prompt") String question);
}
