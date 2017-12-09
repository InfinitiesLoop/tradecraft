package tradecraft.core

import scala.io.StdIn

class LocalPlayersController extends PlayersController {
  override def run(): Unit = {
    while (running) {
      queue.add(UserCommand(readCommand()))
    }
  }

  private def readCommand(): String = {
    StdIn.readLine() match {
      case s if s.length == 1 =>
        // got a char, k
        s
      case s if s.length == 0 =>
        // just hit enter ey?
        readCommand()
      case Int(i) =>
        // entered a number which is allowed as a whole command
        i.toString
      case s =>
        // else it was a long string and we toss all but the first
        s.take(1)
    }
  }
}

object Int {
  def unapply(s : String) : Option[Int] = try {
    Some(s.toInt)
  } catch {
    case _ : java.lang.NumberFormatException => None
  }
}