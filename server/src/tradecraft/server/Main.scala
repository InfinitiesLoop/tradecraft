package tradecraft.server

object Main {
  def main(args: Array[String]): Unit = {
    val server = new Server()
    server.run()
  }
}
