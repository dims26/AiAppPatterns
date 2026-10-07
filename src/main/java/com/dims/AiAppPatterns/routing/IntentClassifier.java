package com.dims.AiAppPatterns.routing;

import dev.langchain4j.service.UserMessage;

public interface IntentClassifier {

    @UserMessage("Classify {{prompt}}")
    IntentDecision classify(String prompt);
}
