package tradecraft.core

object UserCommand {
  def apply(userId: Long, connectedUser: ConnectedUser, command: String): UserCommand = {
    new UserCommand(userId, connectedUser, command)
  }
  def Refresh = ""
}

class UserCommand(val userId: Long, val connectedUser: ConnectedUser, val command: String) {
}
