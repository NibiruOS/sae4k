package io.github.nibiruos.sae4k

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size

class SpriteActor(
    private val animation: SpriteAnimation,
    initialPosition: Point,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE
) : BaseActor(initialPosition, floorAngle) {

    override fun initialize() {
        super.initialize()
        sprite.playAnimation(animation)
    }

    override suspend fun moveTo(path: List<Point>) {
        sprite.pos = path.last()
    }

    fun onClick(callback: suspend () -> Unit): SpriteActor {
        sprite.onClick { callback() }
        return this
    }
}

suspend fun Scenario.simpleActor(
    actorBitmap: Bitmap,
    frames: Int,
    position: Point,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE,
    setupCallback: (suspend SpriteActor.() -> Unit)? = null
): SpriteActor {
    val actorWidth = actorBitmap.width
    val actorHeight = actorBitmap.height
    return SpriteActor(
        SpriteAnimation(
            spriteMap = actorBitmap,
            spriteWidth = actorWidth,
            spriteHeight = actorHeight,
            columns = frames,
            rows = 1
        ),
        position,
        floorAngle
    ).apply {
        addToScenario()
        if (setupCallback != null) {
            setupCallback()
        }
    }
}

suspend fun Scenario.simpleActor(
    actor: String,
    frames: Int,
    position: Point,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE,
    setupCallback: (suspend SpriteActor.() -> Unit)? = null
): SpriteActor = simpleActor(
    resourcesVfs["$actor.png"].readBitmap(),
    frames,
    position,
    floorAngle,
    setupCallback
)
