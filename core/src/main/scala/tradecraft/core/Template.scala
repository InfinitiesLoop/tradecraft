package tradecraft.core

import scala.io.Source

abstract class Template(val name: String) {
  def content(): String
}

object ResourceTemplate {
  def apply(name: String, resourcePath: String, classLoader: ClassLoader) = new ResourceTemplate(name, resourcePath, classLoader)
}
class ResourceTemplate(name: String, resourcePath: String, classLoader: ClassLoader) extends Template(name) {
  override def content(): String = Source.fromResource(resourcePath, classLoader).mkString
}
