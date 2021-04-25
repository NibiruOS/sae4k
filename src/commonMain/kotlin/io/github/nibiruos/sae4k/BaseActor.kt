package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.xy
import com.soywiz.korma.geom.*

abstract class BaseActor(
    private val initialPosition: Point,
    floorAngle: Angle
) : Actor {
    private companion object {
        const val SCREEN_DISTANCE = 100.0
    }

    override val feetPosition: Point
        get() = sprite.pos

    override val headPosition: Point
        get() = Point(
            sprite.x,
            sprite.y - sprite.scaledHeight
        )

    protected val sprite = Sprite()
    private val floorTg = floorAngle.tangent

    override fun Container.addToContainer() {
        addChild(sprite)
    }

    override fun removeFromContainer() {
        sprite.removeFromParent()
    }

    override fun initialize() {
        sprite.anchor(Anchor.BOTTOM_CENTER)
        sprite.scale = scaleFactor(initialPosition)
        sprite.xy(initialPosition.x, initialPosition.y)
    }

    protected fun scaleFactor(position: Point): Double {
        val parent = sprite.parent
        requireNotNull(parent) { "Actor sprite must be attached to parent before computing scale factor" }

        val z = (parent.height - position.y) * floorTg
        return SCREEN_DISTANCE / (SCREEN_DISTANCE + z)
    }
}