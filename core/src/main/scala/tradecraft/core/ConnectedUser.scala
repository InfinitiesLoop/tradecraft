package tradecraft.core

import scala.collection.mutable

abstract class ConnectedUser() {
  private val data = mutable.Map[String, Any]()
  private var authenticatedAs: Option[Long] = None
  def authenticatedAs(userId: Long): Unit = authenticatedAs = Some(userId)

  def getUserId: Option[Long] = authenticatedAs
  def isAuthenticated: Boolean = authenticatedAs.isDefined

  def sendResponse(response: Response)

  def setData(name: String, value: Any): Unit = {
    data.synchronized {
      data(name) = value
    }
  }

  def getData[T](name: String): Option[T] = {
    data.synchronized {
      data.get(name).map(d => d.asInstanceOf[T])
    }
  }
}
