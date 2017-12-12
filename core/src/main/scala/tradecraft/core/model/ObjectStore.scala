package tradecraft.core.model

import scala.collection.mutable

class ObjectStore {
  private def objectMap = mutable.Map[String, mutable.Map[Long, GameObject]]()

  def getObject[T <: GameObject](objectId: Long): Option[T] = {
    objectNameOf(classOf[T]) match {
      case Some(name) =>
        objectMap.synchronized {
          objectMap.get(name) match {
            case Some(m) =>
              m.get(objectId).orElse[T](None)
            case _ =>
              None
          }
        }
      case _ =>
        None
    }
  }

  def insertObject[T <: GameObject](obj: T): Option[Long] = {
    objectNameOf(classOf[T]) match {
      case Some(name) =>
        objectMap.synchronized {
          objectMap.get(name) match {
            case Some(m) =>
              m(m.size + 1) = obj
              Some(m.size)
            case _ =>
              objectMap(name) = mutable.Map[Long, GameObject]({ 1L -> obj.asInstanceOf[GameObject] })
              Some(1)
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

  def objectNameOf[T <: GameObject](clazz: Class[T]): Option[String] = {
    clazz.getAnnotationsByType(classOf[GameObjectName]) match {
      case a if a.length == 1 =>
        Option(a(0).name)
      case _ =>
        None
    }
  }
}
