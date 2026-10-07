package com.dims.AiAppPatterns.memory;

import com.dims.AiAppPatterns.routing.IntentClassifier;
import com.dims.AiAppPatterns.routing.IntentDecision;
import com.dims.AiAppPatterns.routing.RoutedResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MemoryIntentRouter {

    private final IntentClassifier intentClassifier;
    private final MemoryRagAssistant ragAssistant;
    private final MemoryToolingAssistant toolingAssistant;

    public MemoryIntentRouter(
            @Qualifier("memoryIntentClassifier") IntentClassifier intentClassifier,
            MemoryRagAssistant ragAssistant,
            MemoryToolingAssistant toolingAssistant) {

        this.intentClassifier = intentClassifier;
        this.ragAssistant = ragAssistant;
        this.toolingAssistant = toolingAssistant;
    }

    public RoutedResponse route(String sessionId, String message) {

        IntentDecision decision = intentClassifier.classify(message);

        String response = switch (decision.intent()) {

            case KNOWLEDGE -> ragAssistant.ask(sessionId, message).content();
            case OPERATIONAL -> toolingAssistant.ask(sessionId, message).content();

            case HYBRID -> {
                String rag = ragAssistant.ask(sessionId, message).content();
                String tool = toolingAssistant.ask(sessionId, message).content();

                yield rag + "\n\n" + tool;
            }
        };

        return new RoutedResponse(decision.intent(), response);
    }
}
