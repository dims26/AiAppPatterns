package com.dims.AiAppPatterns.tools.stores;

import com.dims.AiAppPatterns.tools.domain.HelpdeskTicket;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HelpdeskTicketStore {
    private final Map<String, HelpdeskTicket> tickets = new ConcurrentHashMap<>();

    public HelpdeskTicket save(HelpdeskTicket ticket) {
        tickets.put(ticket.id(), ticket);
        return ticket;
    }

    public Optional<HelpdeskTicket> find(String ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public Collection<HelpdeskTicket> findAll() {
        return tickets.values();
    }
}
