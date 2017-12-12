package tradecraft

import tradecraft.server.Server

object Main {
  def main(args: Array[String]): Unit = {
    val server = new Server()
    server.run()
  }
}
