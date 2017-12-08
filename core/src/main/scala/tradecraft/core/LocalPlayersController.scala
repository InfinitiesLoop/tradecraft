package tradecraft.core

import scala.io.StdIn

class LocalPlayersController extends PlayersController {
  override def run(): Unit = {
    while (running) {
      val command = StdIn.readLine("[Command]: ")
      queue.add(GameCommand(command))
    }
  }
}
