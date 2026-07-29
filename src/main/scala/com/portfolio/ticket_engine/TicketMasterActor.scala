package com.portfolio.ticket_engine

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object TicketMasterActor{
  sealed trait Command
  final case class Initialice(total: Int, replyTo: ActorRef[Done]) extends Command
  final case class BuyTicket(user: String, replyTo: ActorRef[TicketResponse]) extends Command
  final case class GetRemaining(replyTo: ActorRef[Int]) extends Command

  // Solo para referencia, para ver si se inicializó correctamente
  final case class Done()

  def apply(): Behavior[Command] = active(0)

  private def active(availableTickets: Int): Behavior[Command] = Behaviors.receiveMessage{
    case Initialice(total, replyTo) =>
      replyTo ! Done()
      active(total) //recursivo

    case BuyTicket(user, replyTo) =>
      if (availableTickets > 0){
        replyTo ! new TicketResponse(true, s"Compra exitosa para $user", availableTickets-1)
        active(availableTickets-1)
      } else {
        replyTo ! new TicketResponse(false, s"Sould out. Compra rechazada para $user", 0)
        Behaviors.same
      }

    case GetRemaining(replyTo) =>
      replyTo ! availableTickets
      Behaviors.same
  }
}
