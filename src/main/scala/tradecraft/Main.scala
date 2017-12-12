package tradecraft

import tradecraft.core.LocalPlayersController
import tradecraft.mod.netty.NioPlayersController
import tradecraft.server.Server

object Main {
  def main(args: Array[String]): Unit = {
    val server = new Server()
    //server.addService(new LocalPlayersController())
    server.addService(new NioPlayersController())
    server.run()
  }
}
