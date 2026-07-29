package com.portfolio.ticket_engine;

public record TicketResponse(
		boolean success,
		String message,
		int remainingTickets) {}
