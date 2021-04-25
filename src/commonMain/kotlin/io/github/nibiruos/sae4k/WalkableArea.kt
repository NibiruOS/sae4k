package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point

//  TODO: Use VectorPath + Shape2d ?  https://korlibs.soywiz.com/korma/
class WalkableArea(
    private val mainPolygon: Polygon,
    private val innerPolygons: List<Polygon>
) {
    private val vertices: List<Point>
    private val edges: Map<Point, List<Pair<Point, Double>>>

    init {
        val mutableVertices = mutableListOf<Point>()

        mutableVertices.addConcaveVertex(mainPolygon.points, true)
        for (polygon in innerPolygons) {
            mutableVertices.addConcaveVertex(polygon.points, false)
        }

        vertices = mutableVertices

        val mutableEdges = mutableMapOf<Point, List<Pair<Point, Double>>>()

        for (origin in vertices) {
            val reachablePoints = origin.reachablePoints()
            if (reachablePoints.isNotEmpty()) {
                mutableEdges[origin] = reachablePoints
            }
        }
        edges = mutableEdges
    }

    fun contains(point: Point) =
        mainPolygon.contains(point)
                && !innerPolygons.any { it.contains(point) }

    fun findPath(
        origin: Point,
        destination: Point
    ): List<Point> {
        return if (!contains(destination)) {
            listOf()
        } else if (origin.inLineOfSight(destination)) {
            listOf(destination)
        } else {
            val searchEdges = edges.let {
                if (it.containsKey(origin)) {
                    edges
                } else {
                    edges.toMutableMap()
                        .apply {
                            this[origin] = origin.reachablePoints()
                        }
                }
            }.mapValues { entry ->
                if (entry.key.inLineOfSight(destination)) {
                    entry.value.toMutableList()
                        .apply {
                            add(Pair(destination, destination.distanceTo(entry.key)))
                        }
                } else {
                    entry.value
                }
            }

            // A* search
            val openSet = mutableSetOf<SearchNode>()
            openSet.add(SearchNode(origin, 0.0, origin.distanceTo(destination), null))

            while (openSet.isNotEmpty()) {
                val current = openSet.removeLowestFScore()
                if (current.point == destination) {
                    val path = mutableListOf<Point>()
                    var node = current;
                    while (node.cameFrom != null) {
                        path.add(0, node.point)
                        node = node.cameFrom!!
                    }
                    return path
                } else {
                    searchEdges[current.point]?.forEach {
                        openSet.add(
                            SearchNode(
                                it.first,
                                current.gScore + it.second,
                                it.first.distanceTo(destination),
                                current
                            )
                        )
                    }
                }
            }

            // No path found
            listOf()
        }
    }

    fun draw(container: Container) {
        mainPolygon.draw(container, Colors.RED)
        innerPolygons.draw(container, Colors.RED)
        vertices.draw(container, Colors.GREEN)
    }

    private fun Point.inLineOfSight(destination: Point): Boolean {
        val pathSegment = Segment(this, destination)
        for (segment in mainPolygon.segments()) {
            if (pathSegment.cross(segment)) {
                return false
            }
        }
        for (polygon in innerPolygons) {
            for (segment in polygon.segments()) {
                if (pathSegment.cross(segment)) {
                    return false
                }
            }
        }
        return true
    }

    private fun Segment.cross(segment: Segment) =
        intersects(segment)
                && endpoint1 != segment.endpoint1
                && endpoint1 != segment.endpoint2
                && endpoint2 != segment.endpoint1
                && endpoint2 != segment.endpoint2

    private fun Point.reachablePoints(): List<Pair<Point, Double>> =
        vertices.filter {
            it != this && inLineOfSight(it)
        }.map {
            Pair(it, this.distanceTo(it))
        }
}

// TODO: Implement a priority queue
private fun MutableSet<SearchNode>.removeLowestFScore(): SearchNode {
    var searchNode = this.first()
    forEach {
        if (it.fScore < searchNode.fScore) {
            searchNode = it
        }
    }
    remove(searchNode)
    return searchNode
}

private fun MutableList<Point>.addConcaveVertex(points: List<Point>, concave: Boolean) {
    val size = points.size
    for ((index, point) in points.withIndex()) {
        if (concave == point.isVertexConcave(
                points[(index - 1).narrow(size)],
                points[(index + 1).narrow(size)]
            )
        ) {
            add(point)
        }
    }
}

private fun Point.isVertexConcave(previous: Point, next: Point): Boolean {
    val left = Point(x - previous.x, y - previous.y);
    val right = Point(next.x - x, next.y - y);
    val cross = (left.x * right.y) - (left.y * right.x);
    return cross < 0;
}

private fun Int.narrow(size: Int) = (this + size) % size

private data class SearchNode(
    val point: Point,
    val gScore: Double,
    val fScore: Double,
    val cameFrom: SearchNode?
)

class WalkableAreaBuilder(private val mainPolygon: Polygon) {
    private val innerPolygons = mutableListOf<Polygon>()

    fun innerPolygon(callback: PolygonBuilder.() -> Unit) =
        innerPolygons.add(polygon(callback))

    fun build() = WalkableArea(mainPolygon, innerPolygons)
}

fun walkableArea(
    mainCallback: PolygonBuilder.() -> Unit,
    callback: WalkableAreaBuilder.() -> Unit = {}
) = WalkableAreaBuilder(polygon(mainCallback)).apply(callback).build()
