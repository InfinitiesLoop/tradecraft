package tradecraft.core

import java.util.concurrent.ConcurrentLinkedQueue

abstract class PlayersController extends Service {
  protected val queue = new ConcurrentLinkedQueue[UserCommand]
  protected var running = true

  def pollCommand: Option[UserCommand] = Option(queue.poll())

  override def close(): Unit = {
    running = false
  }
}
