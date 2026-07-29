package com.portfolio.ticket_engine;

import org.springframework.stereotype.Service;
import java.util.concurrent.locks.ReentrantLock;

@Service("javaTicketService")
public class JavaTicketService implements TicketService {
    private int availableTickets = 0;
    private final ReentrantLock lock = new ReentrantLock();
	
    @Override
    public void initializeSale(int totalTickets) {
        lock.lock();
        try {
            this.availableTickets = totalTickets;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TicketResponse buyTicket(String user) {
        lock.lock();
        try {
            if (availableTickets > 0) {
                availableTickets--; // Operación no-atómica protegida por el lock
                return new TicketResponse(true, "Compra exitosa para " + user, availableTickets);
            } else {
                return new TicketResponse(false, "Sold out. Rechazado: " + user, 0);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getRemainingTickets(){
	lock.lock();
	try{
	    return availableTickets;
	} finally {
	    lock.unlock();
	}
    }
}
	
	

	
	
		
		
			
		
			
		
	

	
	
		
		
		
		
			
			
				
