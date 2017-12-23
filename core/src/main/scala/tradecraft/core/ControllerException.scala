package tradecraft.core

case class ControllerException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
