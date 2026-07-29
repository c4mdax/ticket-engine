package com.portfolio.ticket_engine;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/simulation")
public class TicketController {

    private final TicketService javaService;
    private final TicketService scalaService;

    public TicketController(
            @Qualifier("javaTicketService") TicketService javaService,
            @Qualifier("scalaTicketService") TicketService scalaService) {
        this.javaService = javaService;
        this.scalaService = scalaService;
    }

    public record SimulationRequest(int initialTickets, int totalBuyers, String engineType) {}

    @PostMapping("/run")
    public ResponseEntity<?> runSimulation(@RequestBody SimulationRequest request) throws InterruptedException {
        
        TicketService activeService = request.engineType().equalsIgnoreCase("scala") ? scalaService : javaService;
        
        activeService.initializeSale(request.initialTickets());

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(request.totalBuyers());
        AtomicInteger successfulBuys = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < request.totalBuyers(); i++) {
            final String buyerName = "Buyer-" + i;
            executor.submit(() -> {
                TicketResponse response = activeService.buyTicket(buyerName);
                if (response.success()) {
                    successfulBuys.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        return ResponseEntity.ok(Map.of(
                "engineUsed", request.engineType(),
                "timeMs", (endTime - startTime),
                "initialTickets", request.initialTickets(),
                "successfulBuys", successfulBuys.get(),
                "remainingTickets", activeService.getRemainingTickets(),
                "rejectedRequests", (request.totalBuyers() - successfulBuys.get())
        ));
    }
}
