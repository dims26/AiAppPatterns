package com.dims.AiAppPatterns.routing;

import com.dims.AiAppPatterns.SystemPrompts;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {

    @Bean
    public IntentClassifier intentClassifier(ChatModel chatModel) {
        return AiServices.builder(IntentClassifier.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.INTENT_CLASSIFIER)
                .build();
    }
}
