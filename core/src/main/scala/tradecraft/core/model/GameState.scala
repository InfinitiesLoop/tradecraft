package tradecraft.core.model

import java.util.concurrent.atomic.AtomicLong

import scala.collection.mutable

object GameObject {
  def apply(kind: String, data: Map[String, Any]): GameObject =
    new GameObject(kind, None, Some(data))
}
class GameObject(val kind: String,
                 val id: Option[Long] = None,
                 _data: Option[Map[String, Any]] = None) {

  val data = new mutable.HashMap[String, Any]()
  _data.foreach(d => d.foreach(e => data(e._1) = e._2))

  def copy(): GameObject = copyWithId(id)
  def copyWithId(id: Option[Long]): GameObject = new GameObject(kind, id, Some(data.toMap))

  def apply[T](name: String): Option[T] = data.get(name).map(v => v.asInstanceOf[T])
  def update(name: String, value: Any): Unit = data(name) = value
  def contains(name: String): Boolean = data.contains(name)
  def contains(name: String, value: Any): Boolean = data.get(name).contains(value)
}

class GameState {
  // every game object gets a unique auto incrementing ID.
  // using Atomic even though in general this class isn't thread safe at all. It's easy enough....
  // will eventually either make this class thread safe or require users of it to synchronize on it.
  var currentId = new AtomicLong(0)

  // stores all the game objects that exist, indexed by their id.
  private val gameObjects = new mutable.HashMap[Long, GameObject]
  // stores all the game objects that exist, indexed by their kind.
  private val gameObjectsByKind = new mutable.HashMap[String, scala.collection.mutable.Set[GameObject]]
    with mutable.MultiMap[String, GameObject]

  // stores the relationship that game objects have with one another, using only their IDs as graph nodes,
  // so that the graph never needs mutation as game objects are themselves mutated.
  private val graph = new Graph[Long]()

  // inserts an object by giving it a unique ID, and then storing it in both the object map
  // and the object graph.
  def insertObject(gameObject: GameObject): GameObject = {
    val id = currentId.addAndGet(1)
    val newObject = gameObject.copyWithId(Some(id))
    graph.addNode(id)
    gameObjects(id) = newObject
    gameObjectsByKind.addBinding(gameObject.kind, gameObject)
    newObject
  }

  // if a caller needs to mutate a game object, they should do so by creating a copy of it that's in the correct state,
  // or that is then mutated, and then call updateObject to store the new instance in place of the old one. This preserves
  // graph relationships the object has, doesn't change the ID, and is a more thread safe way of doing the mutation.
  def updateObject(gameObject: GameObject): Unit = {
    gameObjects(gameObject.id.get) = gameObject
    gameObjectsByKind.removeBinding(gameObject.kind, gameObject)
    gameObjectsByKind.addBinding(gameObject.kind, gameObject)
  }

  def getObject(id: Long): Option[GameObject] = gameObjects.get(id)
  def getObjects(kind: String): Set[GameObject] = gameObjectsByKind.getOrElse(kind, mutable.Set()).toSet
  def getObjects(kind: String, key: String, value: Any): Set[GameObject] =
    gameObjectsByKind.get(kind).map(_.filter(_.contains(key, value))).getOrElse(mutable.Set()).toSet

  def connect(from: GameObject, to: GameObject, name: String, bidirectional: Boolean = false): Boolean
    = graph.connect(from.id.get, to.id.get, name, bidirectional)
  def disconnect(from: GameObject, to: GameObject, name: String, bidirectional: Boolean = false): Boolean
    = graph.disconnect(from.id.get, to.id.get, name, bidirectional)

  def getConnectionsIn(to: GameObject, name: String): collection.Set[GameObject] =
    graph.ins(to.id.get, name).flatMap(gameObjects.get)
  def getConnectionsOut(from: GameObject, name: String): collection.Set[GameObject] =
    graph.outs(from.id.get, name).flatMap(gameObjects.get)
}
