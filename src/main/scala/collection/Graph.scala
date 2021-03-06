/**
 * Graph class, which is used to search the shorted path between 
 * source/destination stations.
 * 
 * Be CAREFUL about the relation of different "infinities".
 * Here we set the infinity of a vertex as 999999, the guard value
 * to find the vertex with the smallest dist metric as 99999999,
 * and the penalty value for transit in City.scala as 9999 (minutes).
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scasci.collection

import scala.collection.mutable
import scala.util.control.Breaks._

class Edge(val vertex: Int, val weight: Int) {
  override def toString = "e(" + vertex + " ," + weight + ")"
}

object Vertex {
  val Infinity = 999999
  val Undefined = -1  
}
/** 
 * We use adjacency list (http://en.wikipedia.org/wiki/Adjacency_list) to implement graph,
 * which is done in field neighbors. 
 * Fields like dist and prev are used for Dijkstra's algorithm.
 * Field tag is the name of the Vertex.  
 */ 
class Vertex(val tag: String, var neighbors: List[Edge]) {
  var dist: Int = Vertex.Infinity
  var prev: Int = Vertex.Undefined
}

object Graph {
  /** 
   * Construct a graph object based on the weight map.
   * We don't care whether the graph is directed or not.
   * If it is undirected, the caller should make sure to add
   * both u->v and v->u in the weightMap.  
   */
  def Graph(weightMap: mutable.HashMap[(String, String), Int]): Graph = {
    val pairs = weightMap.keys
    val tags = pairs.map(_._1).toSet ++ pairs.map(_._2).toSet
    val vertices = tags.toArray.map(new Vertex(_, List()))
    var tagIndexMap = new mutable.HashMap[String, Int]()    
    for (i <- 0 until vertices.size)
      tagIndexMap += (vertices(i).tag -> i)
      
    for (pair <- pairs) {
      val u = tagIndexMap(pair._1)
      val v = tagIndexMap(pair._2)
      val edge = new Edge(v, weightMap(pair))
      vertices(u).neighbors = edge :: vertices(u).neighbors          
    }

    val graph = new Graph(vertices)
    graph    
  }
}

class Graph(val vertices: Array[Vertex]) {
  /** Initialize dist and prev fields of each vertex. */
  private def initVertices() {
    for (vertex <- vertices) {
      vertex.dist = Vertex.Infinity
      vertex.prev = Vertex.Undefined
    }
  }
  
  /** Return the array of neighbor indices of the given vertex. */
  private def neighbors(vertex: Int) =
    vertices(vertex).neighbors.map(_.vertex)
    
  /** Return the shortest path (in a list of tags) with Dijkstra's algorithm in:
   *    http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
   *  
   *  @param source    index of source vertex
   *  @param targets   indices of target vertices
   *  
   *  We use Dijkstra's algorithm instead of A* algorithm because it is somehow 
   *  difficult to find one good heuristic function to take care minimum time and transits
   *  for metro route finding.
   *  
   *  In addition, we don't use priority queue as it might be challenging to implement 
   *  "decrease key" operation.
   */  
  def find(source: Int, targets: List[Int]): List[List[String]] = {
    /** Return the path given `target`. */
    def path(target: Int): List[String] = {
      var s: List[Int] = List()
      var u = target
      while (vertices(u).prev != Vertex.Undefined) {
        s = u :: s
        u = vertices(u).prev
      }
      s = source :: s
      s.map(vertices(_).tag)      
    }
    
    initVertices()
    var ts = targets.toSet
    
    vertices(source).dist = 0

    // Performance might be improved if we separate q to two sets, one set whose 
    // dist values not update, while dist is updated in another set. In this way, 
    // finding the vertex with minimum dist can be done in the latter set only.
    // However we will do such optimization until we see performance problems.
    var q = (0 until vertices.length).toSet
  
    breakable {
    	while (!q.isEmpty) {
        // Find the vertex with the smallest dist
    	  var u = -1
    	  var d = 99999999
    	  for (v <- q) {
    	    val dist = vertices(v).dist
    	    if (dist < d) {
    	      u = v
    	      d = dist
    	    }
    	  }
    	  
    	  ts -= u
    	  if (ts.isEmpty) break
    	  q -= u
        // Error input if all remaining vertices are inaccessible from source.    	  
    	  assert(vertices(u).dist != Vertex.Infinity)
    	    
    	  for (vv <- vertices(u).neighbors if q.contains(vv.vertex)) {
    	    val v = vv.vertex
    	    val alt = vertices(u).dist + vv.weight
    	    if (alt < vertices(v).dist) {
    	      vertices(v).dist = alt
    	      vertices(v).prev = u
    	    }
    	  }
    	}
    }
    
    targets.map(path _)
  }
  
  def find(sourceTag: String, targetTags: List[String]): List[List[String]] = {
    var sourceIndex = -1
    val targetIndices = targetTags.map(_ => -1).toArray
    for (i <- 0 to vertices.size - 1) {
      if (vertices(i).tag == sourceTag) sourceIndex = i
      for (j <- 0 to targetIndices.size - 1)
        if (vertices(i).tag == targetTags(j)) targetIndices(j) = i
    }
    assert (sourceIndex != -1 && targetIndices.forall(_ != -1))
    find(sourceIndex, targetIndices.toList)
  }
}
