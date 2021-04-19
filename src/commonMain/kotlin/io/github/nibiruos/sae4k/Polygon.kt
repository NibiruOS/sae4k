package io.github.nibiruos.sae4k

import com.soywiz.kmem.isOdd
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point

class Polygon(val points: List<Point>) {
    init {
        check(points.size > 2) { "Polygon needs at least 3 points" }
    }

    fun contains(point: Point): Boolean {
        val segmentToOutside = Segment(point, Point(Double.MAX_VALUE, Double.MAX_VALUE))
        var intersections = 0
        for (segment in segments()) {
            if (segment.intersects(segmentToOutside)) {
                intersections++
            }
        }
        return intersections.isOdd
    }

    fun segments(): List<Segment> {
        var previous = points.last()
        return points.map {
            val segment = Segment(previous, it)
            previous = it
            segment
        }
    }

    fun draw(container: Container, color: RGBA) {
        points.draw(container, color)
    }
}

class PolygonBuilder {
    private val points = mutableListOf<Point>()

    infix fun Int.`_`(y: Int) {
        points.add(Point(this, y))
    }

    fun build() = Polygon(points)
}

fun polygon(callback: PolygonBuilder.() -> Unit) =
    PolygonBuilder().apply(callback).build()

fun List<Polygon>.draw(container: Container, color: RGBA) =
    forEach { it.draw(container, color) }