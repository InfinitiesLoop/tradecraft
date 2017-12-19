package tradecraft.core

object UserCommand {
  def apply(connectedUser: ConnectedUser, commandInfo: CommandInfo): UserCommand = {
    new UserCommand(connectedUser, commandInfo)
  }
  def Refresh = ""
}

class UserCommand(val connectedUser: ConnectedUser,
                  val commandInfo: CommandInfo) {
}

case class CommandInfo(`type`: String, route: String, param: String)