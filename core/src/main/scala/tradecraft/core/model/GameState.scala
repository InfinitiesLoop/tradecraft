package tradecraft.core.model

class GameObject {}
class GameState {
  private val graph: Graph[GameObject] = new Graph[GameObject]()

  def addObject(gameObject: GameObject): Unit = graph.addNode(gameObject)

  def connect(from: GameObject, to: GameObject, name: String, bidirectional: Boolean = false): Boolean
    = graph.connect(from, to, name, bidirectional)
  def disconnect(from: GameObject, to: GameObject, name: String, bidirectional: Boolean = false): Boolean
    = graph.disconnect(from, to, name, bidirectional)

  def getConnectionsIn(from: GameObject, name: String): collection.Set[GameObject] = graph.ins(from, name)
  def getConnectionsOut(to: GameObject, name: String): collection.Set[GameObject] = graph.outs(to, name)
}
