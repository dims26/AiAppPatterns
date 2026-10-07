package com.dims.AiAppPatterns.rag;

import dev.langchain4j.service.*;

public interface ItKnowledgeWizard {
    @UserMessage("Answer question: {{question}}")
    Result<String> ask(@V("question") String question);

    @UserMessage("Answer question: {{question}}")
    Result<String> ask(String sessionId, @V("question") String question);
}
