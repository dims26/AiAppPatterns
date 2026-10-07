package com.dims.AiAppPatterns.memory;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface MemoryToolingAssistant {
    @UserMessage("User prompt: {{prompt}}")
    Result<String> ask(@MemoryId String sessionId, @V("prompt") String question);
}
