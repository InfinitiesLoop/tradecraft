package tradecraft.core.model

import scala.collection.mutable
import scala.reflect.ClassTag

class ObjectStore {
  private def objectMap = mutable.Map[String, mutable.Map[Long, GameObject]]()

  def getObject[T <: GameObject](objectId: GameObjectId): Option[T] = {
    objectMap.synchronized {
      objectMap.get(objectId.objectType) match {
        case Some(m) =>
          m.get(objectId.id).map(o => o.asInstanceOf[T])
        case _ =>
          None
      }
    }
  }

  def insertObject[T <: GameObject](objectId: GameObjectId, obj: T): Option[GameObjectId] = {
    objectMap.synchronized {
      val m = objectMap.getOrElseUpdate(objectId.objectType, mutable.Map[Long, GameObject]())

      if (objectId.id > 0) {
        if (m.contains(objectId.id)) {
          throw new IllegalStateException(s"Object ${objectId.objectType}#${objectId.id} already exists.")
        }
        m(objectId.id) = obj
        Some(GameObjectId(objectId.objectType, objectId.id))
      } else {
        m(m.size + 1) = obj
        Some(GameObjectId(objectId.objectType, m.size))
      }
    }
  }

  //def objectNameOf(obj: GameObject): Option[String] = Some(obj.getClass.getName)
  //def objectNameFromClass[T](clazz: Class[T]): Option[String] = Some(clazz.getName)
}
