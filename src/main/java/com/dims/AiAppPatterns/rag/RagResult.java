package com.dims.AiAppPatterns.rag;

import dev.langchain4j.service.Result;

public record RagResult(String content, int inputTokenCount, int outputTokenCount, int totalTokenCount) {
    public RagResult(Result<String> result) {
        this(result.content(), result.tokenUsage().inputTokenCount(),
                result.tokenUsage().outputTokenCount(), result.tokenUsage().totalTokenCount());
    }
}
