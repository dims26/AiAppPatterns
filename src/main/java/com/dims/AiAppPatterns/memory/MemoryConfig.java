package com.dims.AiAppPatterns.memory;

import com.dims.AiAppPatterns.SystemPrompts;
import com.dims.AiAppPatterns.routing.IntentClassifier;
import com.dims.AiAppPatterns.tools.HelpdeskTool;
import com.dims.AiAppPatterns.tools.ServiceStatusTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

    @Bean
    public MemoryToolingAssistant itToolingAssistant(
            ChatModel chatModel, HelpdeskTool helpdeskTool, ServiceStatusTool serviceStatusTool,
            MemoryChatProvider memoryChatProvider) {
        return AiServices.builder(MemoryToolingAssistant.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_TOOLING_ASSISTANT)
                .tools(helpdeskTool, serviceStatusTool)
                .chatMemoryProvider(id -> {
                    String chatId = (String) id;
                    return memoryChatProvider.getMemory(chatId);
                })
                .build();
    }

    @Bean
    public MemoryRagAssistant memoryRagAssistant(
            ChatModel chatModel, EmbeddingModel embeddingModel,
            @Qualifier("customItEmbedding") EmbeddingStore<TextSegment> embeddingStore,
            MemoryChatProvider memoryChatProvider) {
        return AiServices.builder(MemoryRagAssistant.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_KNOWLEDGE_WIZARD)
                .contentRetriever(EmbeddingStoreContentRetriever
                        .builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .build()
                )
                .chatMemoryProvider(id -> {
                    String chatId = (String) id;
                    return memoryChatProvider.getMemory(chatId);
                })
                .build();
    }

    @Bean("memoryIntentClassifier")
    public IntentClassifier intentClassifier(ChatModel chatModel) {
        return AiServices.builder(IntentClassifier.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.INTENT_CLASSIFIER)
                .build();
    }

    @Bean
    public OpenAiTokenCountEstimator tokenCountEstimator() {
        return new OpenAiTokenCountEstimator("gpt-5.4");
    }
}
