package com.dims.AiAppPatterns.basic_chat;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/chat")
public class ChatController {

    private final ChatModel chatModel;
    private final ConceptSummariser conceptSummariser;

    public ChatController(ChatModel chatModel, ConceptSummariser conceptSummariser) {
        this.chatModel = chatModel;
        this.conceptSummariser = conceptSummariser;
    }

    @PostMapping("/prompt")
    String prompt(@RequestBody String prompt) {
        return chatModel.chat(
                SystemMessage.from("You are a TestCo chatbot"),
                UserMessage.from(prompt)
        ).aiMessage().text();
    }

    @PostMapping("/summary")
    SummarisedResponse summary(@RequestBody String prompt) {
        return conceptSummariser.summarise(prompt);
    }
}
