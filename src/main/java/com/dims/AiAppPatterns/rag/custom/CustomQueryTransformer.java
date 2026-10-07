package com.dims.AiAppPatterns.rag.custom;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class CustomQueryTransformer implements QueryTransformer {

    private final ChatModel chatModel;
    private static final String SYSTEM_PROMPT = """
            Based on IT support context, convert the user prompt into a more general concept
            Break the concept into at most 3 sub-problems
            Generate and return a search query for each sub-problem, one per line
            """;

    public CustomQueryTransformer(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Collection<Query> transform(Query query) {

        String result = chatModel
                .chat(SystemMessage.from(SYSTEM_PROMPT), UserMessage.from(query.text()))
                .aiMessage().text();

        return Arrays.stream(result.split("\n"))
                .filter(s -> !s.isBlank())
                .map(s -> new Query(s.trim()))
                .toList();
    }
}
