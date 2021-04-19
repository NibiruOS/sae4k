package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.tangent

// TODO: Both Scenario and CharacterActor require floor in its constructor,
// but Scenario also requires CharacterActor, so floor needs to be initialized
// before those 2. Think about a DSL to solve this.
class Floor(
    private val walkableArea: WalkableArea,
    angle: Angle
) {
    private companion object {
        const val SCREEN_DISTANCE = 100.0
    }

    private val floorTg = angle.tangent
    private val indexes = mutableMapOf<View, Double>()

    suspend fun walkTo(
        actor: Actor,
        destination: Point
    ): Boolean {
        val path = walkableArea.findPath(actor.feetPosition, destination)
        val pathFound = path.isNotEmpty()
        if (pathFound) {
            actor.moveTo(path)
        }
        return pathFound
    }

    fun getZIndex(view: View): Double =
        indexes[view] ?: 0.0

    fun setZIndex(
        view: View,
        z: Double
    ) {
        // TODO: This is a hack for z-index
        indexes[view] = z
        indexes.entries
            .sortedByDescending { it.value }
            .forEach {
                it.key.parent?.addChild(it.key)
            }
    }

    fun draw(container: Container) {
        walkableArea.draw(container)
    }

    fun computePositionFromClick(
        actor: Actor,
        clickPosition: Point
    ): Triple<Double, Point, Double> {
        val parent = actor.sprite.parent
        requireNotNull(parent) { "Actor sprite must be attached to parent before computing position" }

        val screenDistance = SCREEN_DISTANCE
        val z = (parent.height - clickPosition.y) * floorTg
        val scaleFactor = screenDistance / (screenDistance + z)

        val newX = clickPosition.x - actor.size.width * scaleFactor / 2
        val newY = clickPosition.y - actor.size.height * scaleFactor

        return Triple(scaleFactor, Point(newX, newY), z)
    }
}

fun floor(
    walkableArea: WalkableArea,
    angle: Angle
) = Floor(walkableArea, angle)