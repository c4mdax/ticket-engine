package com.portfolio.ticket_engine;

public interface TicketService {
	
    void initializeSale(int totalTick);
    TicketResponse buyTicket(String user);
    int getRemainingTickets();
}

