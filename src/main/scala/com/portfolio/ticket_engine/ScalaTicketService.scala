package com.portfolio.ticket_engine

import org.springframework.stereotype.Service
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import org.apache.pekko.actor.typed.Scheduler

@Service("scalaTicketService")
class ScalaTicketService extends TicketService{

  private val system: ActorSystem[TicketMasterActor.Command] = ActorSystem(TicketMasterActor(), "TicketEngineSystem")

  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val scheduler: Scheduler = system.scheduler

  override def initializeSale(totalTickets: Int): Unit = {
    val futureResult = system.ask[TicketMasterActor.Done](ref => TicketMasterActor.Initialice(totalTickets, ref))
    Await.result(futureResult, 3.seconds)
  }

  override def buyTicket(user:String): TicketResponse = {
    val futureResult = system.ask[TicketResponse](ref => TicketMasterActor.BuyTicket(user, ref))
    Await.result(futureResult, 3.seconds)
  }

  override def getRemainingTickets(): Int  = {
    val futureResult = system.ask[Int](ref => TicketMasterActor.GetRemaining(ref))
    Await.result(futureResult, 3.seconds)
  }
}
