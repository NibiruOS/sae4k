package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point

infix fun Int.`_`(y: Int) =
    Point(this, y)

fun Point.draw(container: Container, color: RGBA) =
    container.circle(6.0, color)
        .xy(x - 3, y - 3)

fun List<Point>.draw(container: Container, color: RGBA) =
    forEach { it.draw(container, color) }
