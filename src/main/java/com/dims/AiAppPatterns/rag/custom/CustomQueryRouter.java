package com.dims.AiAppPatterns.rag.custom;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;

import java.util.Map;

public class CustomQueryRouter extends LanguageModelQueryRouter {

    public CustomQueryRouter(ChatModel chatModel, Map<ContentRetriever, String> retrieverDescriptionMap) {
        super(chatModel, retrieverDescriptionMap);
    }
}
