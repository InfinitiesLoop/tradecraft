package tradecraft.core.model

class Player(id: Option[Long]) extends GameObject("player", id) {
  // note that the `id` in this is the GameObject ID, which is not the same thing as a
  // _user_ ID. A user could in theory have multiple players in the same game or it could change.
  // Basically, Player is the game notion of an object in the game's universe. User is the notion of a real
  // person that authenticates and is associated with a Player, usually via a player spawning phase.

  override def copyWithId(id: Option[Long]): GameObject = {
    new Player(id)
  }
}
