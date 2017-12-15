package tradecraft.core.model

import scala.collection.mutable

/**
  * Simple directed graph with labeled edges.
  * Definitely NOT type safe.
  * TNode should implement a stable hashCode and equals.
  * @tparam TNode
  */
class Graph[TNode] {
  // InnerNode for wrapping the actual node and storing the in and out edges
  private object InnerNode {
    def apply(value: TNode) = new InnerNode(value)
  }
  // Note that InnerNode's value is is mutable. Evil, right?
  // It makes life easy -- the objects in the graph are immutable, so we need to be able to
  // replace them with new copies of the same object with mutations are needed, without losing
  // the existing edges on that node. Simplest way is to just mutate the node value.
  private class InnerNode(var value: TNode) {
    def outs: mutable.HashSet[Edge] = new mutable.HashSet[Edge]()
    def ins: mutable.HashSet[Edge] = new mutable.HashSet[Edge]()

    def connectTo(to: TNode, label: String, bidirectional: Boolean = false): Boolean = {
      // edges are directional, but each node keeps track of what it points to and what points to it.
      // just makes things easier later on :)
      val toNode = Graph.this.nodes.get(to)
      if (toNode.isEmpty) {
        false
      } else {
        val fromNode = this
        val fromEdge = Edge(fromNode, label)
        val toEdge = Edge(toNode.get, label)
        fromNode.outs.add(toEdge)
        toNode.get.ins.add(fromEdge)
        if (bidirectional) {
          fromNode.ins.add(toEdge)
          toNode.get.outs.add(fromEdge)
        }
        true
      }
    }

    def disconnectFrom(to: TNode, label: String, bidirectional: Boolean = false): Boolean = {
      val toNode = Graph.this.nodes.get(to)
      if (toNode.isEmpty) {
        false
      } else {
        val fromNode = this
        val fromEdge = Edge(fromNode, label)
        val toEdge = Edge(toNode.get, label)
        fromNode.outs.remove(toEdge)
        toNode.get.ins.remove(fromEdge)
        if (bidirectional) {
          fromNode.ins.remove(toEdge)
          toNode.get.outs.remove(fromEdge)
        }
        true
      }
    }
  }

  // Represent edges as a node it points to/from and a label.
  private case class Edge(node: InnerNode, label: String)

  private def nodes = new mutable.HashMap[TNode, InnerNode]()

  /**
    * Add a node to the graph, if it isn't already in the graph.
    * @param value
    */
  def addNode(value: TNode): Unit = {
    val existing = nodes.get(value)
    if (existing.isDefined) {
      existing.get.value = value
    } else {
      nodes(value) = InnerNode(value)
    }
  }

  /**
    * Test whether a node is already in the graph.
    * @param value
    * @return True if the graph contains the node.
    */
  def hasNode(value: TNode): Boolean = nodes.contains(value)

  /**
    * Connect node 'from' to node 'to' with the given label, optionally doing the reverse as well.
    * @param from
    * @param to
    * @param label
    * @param bidirectional
    * @return true if the connection was made or exists, false otherwise, such as if either node isn't in the graph.
    *         If the connection already exists, no change is made and true is returned.
    */
  def connect(from: TNode, to: TNode, label: String, bidirectional: Boolean = false): Boolean = {
    nodes.get(from).exists(n => n.connectTo(to, label, bidirectional))
  }

  /**
    * Disconnect node 'from' to node 'to' with the given label, optionally doing the reverse as well.
    * @param from
    * @param to
    * @param label
    * @param bidirectional
    * @return true if the connection was removed or didn't exist, false otherwise, such as if either node isn't in the graph.
    *         If the connection already did not exist, no change is made and true is returned.
    */
  def disconnect(from: TNode, to: TNode, label: String, bidirectional: Boolean = false): Boolean = {
    nodes.get(from).exists(n => n.disconnectFrom(to, label, bidirectional))
  }

  /**
    * Gets the outgoing connections from the given node with the given label.
    * @param from
    * @param label
    * @return Set of nodes the given node points to with the given label.
    */
  def outs(from: TNode, label: String): scala.collection.Set[TNode] = {
    nodes.get(from)
      .map(_.outs.filter(_.label == label).map(_.node.value))
      .getOrElse(Set[TNode]())
  }

  /**
    * Gets the incoming connections to the given node with the given label.
    * @param to
    * @param label
    * @return Set of nodes that point to the given node with the given label.
    */
  def ins(to: TNode, label: String): scala.collection.Set[TNode] = {
    nodes.get(to)
      .map(_.ins.filter(_.label == label).map(_.node.value))
      .getOrElse(Set[TNode]())
  }
}
