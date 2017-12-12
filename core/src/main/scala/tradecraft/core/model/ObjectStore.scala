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

  def insertObject[T <: GameObject](obj: T)(implicit classTag: ClassTag[T]): Option[GameObjectId] = {
    objectNameFromClass(classTag.runtimeClass) match {
      case Some(name) =>
        objectMap.synchronized {
          objectMap.get(name) match {
            case Some(m) =>
              m(m.size + 1) = obj
              Some(GameObjectId(name, m.size))
            case _ =>
              objectMap(name) = mutable.Map[Long, GameObject]({ 1L -> obj.asInstanceOf[GameObject] })
              Some(GameObjectId(name, 1))
          }
        }
      case _ =>
        None
    }
  }

  def objectNameOf(obj: GameObject): Option[String] = {
    obj.getClass.getAnnotationsByType(classOf[GameObjectName]) match {
      case a if a.length == 1 =>
        Option(a(0).name)
      case _ =>
        None
    }
  }

  def objectNameFromClass[T](clazz: Class[T]): Option[String] = {
    clazz.getAnnotationsByType(classOf[GameObjectName]) match {
      case a if a.length == 1 =>
        Option(a(0).name)
      case _ =>
        None
    }
  }
}
