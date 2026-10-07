package com.dims.AiAppPatterns.memory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemoryChatProvider {

    private final TokenCountEstimator tokenCountEstimator;

    public MemoryChatProvider(OpenAiTokenCountEstimator tokenCountEstimator) {
        this.tokenCountEstimator = tokenCountEstimator;
    }

    private final Map<String, ChatMemory> chatMemoryMap = new ConcurrentHashMap<>();

    public ChatMemory getMemory(String chatId) {
        return chatMemoryMap.computeIfAbsent(
                chatId,
                id -> TokenWindowChatMemory.withMaxTokens(30_000, tokenCountEstimator)
        );
    }
}
