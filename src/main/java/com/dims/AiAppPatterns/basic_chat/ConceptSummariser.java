package com.dims.AiAppPatterns.basic_chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ConceptSummariser {

    @SystemMessage(value = "You are a concept summariser model")
    @UserMessage(value = "Summarise {{concept}}")
    SummarisedResponse summarise(@V("concept") String concept);
}
