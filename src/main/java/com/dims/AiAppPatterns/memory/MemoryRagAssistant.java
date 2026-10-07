package com.dims.AiAppPatterns.memory;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface MemoryRagAssistant {
    @UserMessage("Answer question: {{question}}")
    Result<String> ask(@MemoryId String sessionId, @V("question") String question);
}
