package com.dims.AiAppPatterns.tools;

import com.dims.AiAppPatterns.tools.domain.HelpdeskTicket;
import com.dims.AiAppPatterns.tools.domain.HelpdeskTicketActionRequest;
import com.dims.AiAppPatterns.tools.domain.HelpdeskTicketResult;
import com.dims.AiAppPatterns.tools.domain.HelpdeskTicketStatus;
import com.dims.AiAppPatterns.tools.stores.HelpdeskTicketStore;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class HelpdeskTool {

    private final HelpdeskTicketStore store;

    public HelpdeskTool(HelpdeskTicketStore store) {
        this.store = store;
    }


    @Tool("""
            Perform actions in the IT helpdesk.
            
            Use this tool when the user asks for a helpdesk action to be performed.
            Examples:
            - create ticket
            - raise incident
            - create request
            - update ticket
            - add comment
            - close ticket
            - get ticket status
            """)
    public HelpdeskTicketResult performAction(HelpdeskTicketActionRequest request) {
        return switch (request.action()) {
            case CREATE -> createTicket(request);
            case UPDATE -> updateTicket(request);
            case COMMENT -> commentTicket(request);
            case STATUS -> getTicketStatus(request);
            case CLOSE -> closeTicket(request);
        };
    }

    private HelpdeskTicketResult createTicket(HelpdeskTicketActionRequest request) {
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        HelpdeskTicket ticket = new HelpdeskTicket(id, request.type(), request.summary(),
                request.description(), HelpdeskTicketStatus.OPEN, new ArrayList<>());

        store.save(ticket);

        return new HelpdeskTicketResult(id, HelpdeskTicketStatus.OPEN.toString(), "Ticket created");
    }

    private HelpdeskTicketResult updateTicket(HelpdeskTicketActionRequest request) {
        HelpdeskTicket ticket = store.find(request.ticketId()).orElseThrow();

        HelpdeskTicket updated = new HelpdeskTicket(ticket.id(), ticket.type(),
                request.summary() != null ? request.summary() : ticket.summary(),
                request.description() != null ? request.description() : ticket.description(),
                ticket.status(), ticket.comments());

        store.save(updated);

        return new HelpdeskTicketResult(updated.id(), updated.status().toString(), "Ticket updated");
    }

    private HelpdeskTicketResult commentTicket(HelpdeskTicketActionRequest request) {

        HelpdeskTicket ticket = store.find(request.ticketId()).orElseThrow();

        List<String> comments = new ArrayList<>(ticket.comments());

        comments.add(request.comment());

        HelpdeskTicket updated = new HelpdeskTicket(ticket.id(), ticket.type(), ticket.summary(),
                ticket.description(), ticket.status(), comments);

        store.save(updated);

        return new HelpdeskTicketResult(updated.id(), updated.status().toString(), "Comment added");
    }

    private HelpdeskTicketResult closeTicket(HelpdeskTicketActionRequest request) {

        HelpdeskTicket ticket = store.find(request.ticketId())
                .orElseThrow();

        HelpdeskTicket closed = new HelpdeskTicket(ticket.id(), ticket.type(), ticket.summary(),
                ticket.description(), HelpdeskTicketStatus.CLOSED, ticket.comments());

        store.save(closed);

        return new HelpdeskTicketResult(closed.id(), closed.status().toString(), "Ticket closed");
    }

    private HelpdeskTicketResult getTicketStatus(HelpdeskTicketActionRequest request) {

        Optional<HelpdeskTicket> ticketOpt = store.find(request.ticketId());

        if (ticketOpt.isEmpty()) {
            return new HelpdeskTicketResult("", "", "Ticket not found");
        }

        HelpdeskTicket ticket = ticketOpt.get();

        return new HelpdeskTicketResult(ticket.id(), ticket.status().toString(), "Ticket status retrieved");
    }

    public List<HelpdeskTicket> getAllTickets() {
        return List.copyOf(store.findAll());
    }
}
