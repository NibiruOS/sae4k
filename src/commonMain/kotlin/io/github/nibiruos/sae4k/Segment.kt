package io.github.nibiruos.sae4k

import com.soywiz.korma.geom.Point
import kotlin.math.max
import kotlin.math.min

data class Segment(
    val endpoint1: Point,
    val endpoint2: Point
) {
    fun intersects(other: Segment): Boolean {
        val slope = slope()
        val oSlope = other.slope()

        return if (slope == oSlope) {
            false
        } else {
            //A1*x + Y1-A1*X1 = A2*x + Y2-A2*X2
            // A1*x -A2*x  =   Y2-A2*X2 -Y1+A1*X1
            // (A1 -A2)*x  =   Y2-A2*X2 -Y1+A1*X1
            // x  =   (Y2-A2*X2 -Y1+A1*X1) /(A1 -A2)
            val xIntersection =
                (other.endpoint1.y - oSlope * other.endpoint1.x - endpoint1.y + slope * endpoint1.x) / (slope - oSlope)
            xRange().contains(xIntersection) && other.xRange().contains(xIntersection)
        }
    }

    private fun xRange() = min(endpoint1.x, endpoint2.x)..max(endpoint1.x, endpoint2.x)
    private fun slope() = (endpoint2.y - endpoint1.y) / (endpoint2.x - endpoint1.x)
}
