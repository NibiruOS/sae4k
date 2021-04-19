package io.github.nibiruos.sae4k

import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size

class CharacterActor(
    private val standingAnimationRight: SpriteAnimation,
    private val walkingAnimationRight: SpriteAnimation,
    private val standingAnimationLeft: SpriteAnimation,
    private val walkingAnimationLeft: SpriteAnimation,
    size: Size,
    initialPosition: Point,
    private val initialToRight: Boolean,
    private val floor: Floor
) : BaseActor(size, initialPosition, floor) {
    override val sprite = Sprite()
    private var zIndex: Double
        get() = floor.getZIndex(sprite)
        set(z) = floor.setZIndex(sprite, z)

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
        var pathLength = 0.0
        var previous = feetPosition
        path.forEach {
            pathLength += previous.distanceTo(it)
            previous = it
        }
        val totalTime = pathLength / Parameters.WALK_SPEED

        val (_, targetPosition, _) = floor.computePositionFromClick(this, path.last())
        val toLeft = targetPosition.x < sprite.x

        sprite.playAnimationLooped(
            spriteAnimation = if (toLeft) {
                walkingAnimationLeft
            } else {
                walkingAnimationRight
            }, spriteDisplayTime = 200.milliseconds
        )

        previous = feetPosition
        path.forEach {
            val duration = (totalTime * previous.distanceTo(it) / pathLength).seconds
            val (scaleFactor, newPosition, z) = floor.computePositionFromClick(this, it)

            sprite.tween(
                sprite::x[newPosition.x],
                sprite::y[newPosition.y],
                sprite::scale[scaleFactor],
                this::zIndex[z],
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
    floor: Floor
) = simpleCharacter(
    resourcesVfs["${character}_standing.png"].readBitmap(),
    resourcesVfs["${character}_walking.png"].readBitmap(),
    initialPosition,
    initialToRight,
    frames,
    floor
)

fun simpleCharacter(
    standingBitmap: Bitmap,
    walkingBitmap: Bitmap,
    initialPosition: Point,
    initialToRight: Boolean,
    frames: Int,
    floor: Floor
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
        Size(characterWidth, characterHeight),
        initialPosition,
        initialToRight,
        floor
    )
}