package com.dims.AiAppPatterns.routing;

import com.dims.AiAppPatterns.rag.ItKnowledgeWizard;
import com.dims.AiAppPatterns.tools.ItToolingAssistant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class IntentRouter {

    private final IntentClassifier intentClassifier;

    private final ItKnowledgeWizard ragAssistant;
    private final ItToolingAssistant toolingAssistant;

    public IntentRouter(
            IntentClassifier intentClassifier,
            @Qualifier("customKnowledgeWizard") ItKnowledgeWizard ragAssistant,
            @Qualifier("toolingAssistant") ItToolingAssistant toolingAssistant) {

        this.intentClassifier = intentClassifier;
        this.ragAssistant = ragAssistant;
        this.toolingAssistant = toolingAssistant;
    }

    public RoutedResponse route(String message) {

        IntentDecision decision = intentClassifier.classify(message);

        String response = switch (decision.intent()) {

            case KNOWLEDGE -> ragAssistant.ask(message).content();
            case OPERATIONAL -> toolingAssistant.ask(message).content();
            case HYBRID -> {
                String rag = ragAssistant.ask(message).content();
                String tool = toolingAssistant.ask(message).content();

                yield rag + "\n\n" + tool;
            }
        };

        return new RoutedResponse(decision.intent(), response);
    }
}
