package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point

class StandingArea(
    private val polygon: Polygon,
    private val onStanding: suspend () -> Unit
) {
    suspend fun standOn(position: Point) {
        if (polygon.contains(position)) {
            onStanding()
        }
    }

    fun draw(container: Container) {
        polygon.draw(container, Colors.BLUE)
    }
}