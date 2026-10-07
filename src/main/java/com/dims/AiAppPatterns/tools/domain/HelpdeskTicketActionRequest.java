package com.dims.AiAppPatterns.tools.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.agent.tool.P;

public record HelpdeskTicketActionRequest(@P("Action to perform") HelpdeskTicketAction action,
                                          @P("Incident or request type (incident or request)") HelpdeskTicketType type,
                                          @P("Ticket summary") String summary,
                                          @P("Detailed description") String description,
                                          @P(value = "Existing ticket id", required = false) @JsonProperty(required = false) String ticketId,
                                          @P("Comment text") String comment) {}
