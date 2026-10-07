package com.dims.AiAppPatterns.rag;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final ItKnowledgeWizard itKnowledgeWizard;
    private final ItKnowledgeWizard customItKnowledgeWizard;
    private final ItKnowledgeWizard customAugItKnowledgeWizard;

    public RagController(@Qualifier("defaultKnowledgeWizard") ItKnowledgeWizard itKnowledgeWizard,
                         @Qualifier("customKnowledgeWizard") ItKnowledgeWizard customItKnowledgeWizard,
                         @Qualifier("customAugmentedKnowledgeWizard") ItKnowledgeWizard customAugItKnowledgeWizard) {
        this.itKnowledgeWizard = itKnowledgeWizard;
        this.customItKnowledgeWizard = customItKnowledgeWizard;
        this.customAugItKnowledgeWizard = customAugItKnowledgeWizard;
    }

    @PostMapping("/ask-default")
    public RagResult ask(@RequestBody String prompt) {
        return new RagResult(itKnowledgeWizard.ask(prompt));
    }

    @PostMapping("/ask-custom")
    public RagResult askCustom(@RequestBody String prompt) {
        return new RagResult(customItKnowledgeWizard.ask(prompt));
    }

    @PostMapping("/ask-custom-aug")
    public RagResult askCustomAug(@RequestBody String prompt) {
        return new RagResult(customAugItKnowledgeWizard.ask(prompt));
    }
}
