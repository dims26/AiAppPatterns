package com.dims.AiAppPatterns.tools;

import com.dims.AiAppPatterns.tools.domain.HelpdeskTicket;
import dev.langchain4j.service.Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tooling")
public class ToolController {
    private final ItToolingAssistant toolingAssistant;
    private final HelpdeskTool helpdeskTool;

    public ToolController(@Qualifier("toolingAssistant") ItToolingAssistant toolingAssistant,
                          HelpdeskTool helpdeskTool) {
        this.toolingAssistant = toolingAssistant;
        this.helpdeskTool = helpdeskTool;
    }

    @PostMapping("/ask")
    public ToolResult askDefault(@RequestBody String prompt) {
        Result<String> result = toolingAssistant.ask(prompt);
        List<Map.Entry<String, String>> executions =
                result.toolExecutions()
                        .stream()
                        .map(toolExecution ->
                                Map.entry(toolExecution.request().name(), toolExecution.request().arguments()))
                        .collect(Collectors.toList());
        System.out.println(executions);
        return new ToolResult(result.content(), executions);
    }

    @GetMapping("/tickets")
    public List<HelpdeskTicket> tickets() {
        return helpdeskTool.getAllTickets();
    }
}
