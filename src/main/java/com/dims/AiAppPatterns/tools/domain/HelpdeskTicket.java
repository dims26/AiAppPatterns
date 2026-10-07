package com.dims.AiAppPatterns.tools.domain;

import java.util.List;

public record HelpdeskTicket(String id, HelpdeskTicketType type, String summary, String description,
                             HelpdeskTicketStatus status, List<String> comments) {}
