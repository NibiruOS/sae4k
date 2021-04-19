package io.github.nibiruos.sae4k

import com.soywiz.korge.view.xy
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size

abstract class BaseActor(
    override val size: Size,
    private val initialPosition: Point,
    private val floor: Floor
) : Actor {
    override val feetPosition: Point
        get() = Point(
            sprite.x + sprite.scaledWidth / 2,
            sprite.y + sprite.scaledHeight
        )

    override val headPosition: Point
        get() = Point(
            sprite.x + sprite.scaledWidth / 2,
            sprite.y
        )

    override fun initialize() {
        val (scaleFactor, newPosition, z) = floor.computePositionFromClick(this, initialPosition)
        sprite.scale = scaleFactor
        sprite.xy(newPosition.x, newPosition.y)
        floor.setZIndex(sprite, z)
    }
}