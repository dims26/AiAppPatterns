package com.dims.AiAppPatterns.rag.sources;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface TopicCategorizer {

    @SystemMessage("You are a categorizer model")
    @UserMessage("Group the following: {{topics}}")
    TopicCategories categorize(@V("topics") List<String> topics);
}
