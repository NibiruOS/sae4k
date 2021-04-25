package io.github.nibiruos.sae4k

import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.component.docking.sortChildrenByY
import com.soywiz.korge.tween.V2
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point

class CharacterActor(
    private val standingAnimationRight: SpriteAnimation,
    private val walkingAnimationRight: SpriteAnimation,
    private val standingAnimationLeft: SpriteAnimation,
    private val walkingAnimationLeft: SpriteAnimation,
    initialPosition: Point,
    private val initialToRight: Boolean,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE
) : BaseActor(
    initialPosition,
    floorAngle
) {
    override fun initialize() {
        super.initialize()

        sprite.playAnimation(
            if (initialToRight) {
                standingAnimationRight
            } else {
                standingAnimationLeft
            }
        )
    }

    override suspend fun moveTo(path: List<Point>) {
        val pathLength = path.pathLength(feetPosition)
        val totalTime = pathLength / Parameters.WALK_SPEED

        val targetPosition = path.last()
        val toLeft = targetPosition.x < sprite.x

        sprite.playAnimationLooped(
            spriteAnimation = if (toLeft) {
                walkingAnimationLeft
            } else {
                walkingAnimationRight
            }, spriteDisplayTime = 200.milliseconds
        )

        var previous = feetPosition
        path.forEach {
            val duration = (totalTime * previous.distanceTo(it) / pathLength).seconds
            val scaleFactor = scaleFactor(it)

            sprite.tween(
                sprite::x[it.x],
                sprite::y[it.y],
                sprite::scale[scaleFactor],
                v2 {
                    sprite.parent!!.sortChildrenByY()
                },
                time = duration
            )

            previous = it
        }

        sprite.playAnimation(
            if (toLeft) {
                standingAnimationLeft
            } else {
                standingAnimationRight
            }
        )
    }
}

suspend fun simpleCharacter(
    character: String,
    initialPosition: Point,
    initialToRight: Boolean,
    frames: Int,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE
) = simpleCharacter(
    resourcesVfs["${character}_standing.png"].readBitmap(),
    resourcesVfs["${character}_walking.png"].readBitmap(),
    initialPosition,
    initialToRight,
    frames,
    floorAngle
)

fun simpleCharacter(
    standingBitmap: Bitmap,
    walkingBitmap: Bitmap,
    initialPosition: Point,
    initialToRight: Boolean,
    frames: Int,
    floorAngle: Angle = Parameters.DEFAULT_FLOOR_ANGLE
): CharacterActor {
    val characterWidth = standingBitmap.width
    val characterHeight = standingBitmap.height
    return CharacterActor(
        SpriteAnimation(
            spriteMap = standingBitmap,
            spriteWidth = characterWidth,
            spriteHeight = characterHeight,
            columns = 1,
            rows = 1
        ),
        SpriteAnimation(
            spriteMap = walkingBitmap,
            spriteWidth = characterWidth,
            spriteHeight = characterHeight,
            columns = frames,
            rows = 1
        ),
        SpriteAnimation(
            spriteMap = standingBitmap.clone().flipX(),
            spriteWidth = characterWidth,
            spriteHeight = characterHeight,
            columns = 1,
            rows = 1
        ),
        SpriteAnimation(
            spriteMap = walkingBitmap.clone().flipX(),
            spriteWidth = characterWidth,
            spriteHeight = characterHeight,
            columns = frames,
            rows = 1
        ),
        initialPosition,
        initialToRight,
        floorAngle
    )
}

private object V2Dummy {
    var dummy: Unit? = null
}

private fun v2(callback: () -> Unit) = V2(
    V2Dummy::dummy,
    null,
    null,
    { _, _, _ ->
        callback()
    },
    false
)

private fun List<Point>.pathLength(startingPoint: Point): Double {
    var pathLength = 0.0
    var previous = startingPoint
    forEach {
        pathLength += previous.distanceTo(it)
        previous = it
    }
    return pathLength
}