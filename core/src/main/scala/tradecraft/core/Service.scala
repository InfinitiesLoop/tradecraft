package tradecraft.core

trait Service {
  def run(): Unit
  def close(): Unit
}
