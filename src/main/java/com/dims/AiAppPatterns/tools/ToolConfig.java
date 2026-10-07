package com.dims.AiAppPatterns.tools;

import com.dims.AiAppPatterns.SystemPrompts;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean(name = "toolingAssistant")
    public ItToolingAssistant itToolingAssistant(
            ChatModel chatModel, HelpdeskTool helpdeskTool, ServiceStatusTool serviceStatusTool) {
        return AiServices.builder(ItToolingAssistant.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_TOOLING_ASSISTANT)
                .tools(helpdeskTool, serviceStatusTool)
                .build();
    }
}
