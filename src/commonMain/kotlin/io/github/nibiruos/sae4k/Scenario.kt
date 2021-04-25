package io.github.nibiruos.sae4k

import com.soywiz.korge.component.docking.sortChildrenByY
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.delay

class Scenario(
    background: Bitmap,
    character: CharacterActor,
    private val walkableArea: WalkableArea
) {
    companion object {
        private const val ACTOR_TEXT_SPACING = 10
    }

    private val container = Container()
    private val standingAreas = mutableListOf<StandingArea>()
    private var currentText: TextBlock? = null
    private var currentActions: Actions? = null

    init {
        container.sortChildrenByY()
        val backgroundImage = container.image(background)
        container.size(backgroundImage.width, backgroundImage.height)
        character.addToScenario()
        backgroundImage.onClick {
            val pathFound = character.walkTo(it.currentPosLocal)
            if (pathFound) {
                standingAreas.forEach { area -> area.standOn(character.feetPosition) }
            }
        }
    }

    suspend fun Actor.walkTo(destination: Point): Boolean {
        disposeTexts()
        val path = walkableArea.findPath(feetPosition, destination)
        val pathFound = path.isNotEmpty()
        if (pathFound) {
            moveTo(path)
        }
        return pathFound
    }

    suspend fun Actor.showText(text: String) {
        disposeTexts()
        val textBlock = container.textBlock(text)
        showAtHead(textBlock.view)
        currentText = textBlock
        delay(Parameters.TEXT_DISPLAY_TIME.toLong())
        textBlock.dispose()
        currentText = null
    }

    fun Actor.actions(vararg actions: Action) {
        disposeTexts()
        val actionBlock = container.actions(*actions)
        showAtHead(actionBlock.view)
        currentActions = actionBlock
    }

    private fun Actor.showAtHead(view: View) {
        var newX = headPosition.x
        var newY = headPosition.y - view.height - ACTOR_TEXT_SPACING
        if (newX + view.width > container.width) {
            newX = container.width - view.width
        }
        if (newY < 0) {
            newY = 0.0
        }
        view.xy(newX, newY)
    }

    private fun disposeTexts() {
        currentText?.dispose()
        currentActions?.dispose()
        currentText = null
        currentActions = null
    }

    internal fun addTo(container: Container) {
        container.addChild(this.container)
    }

    fun standingArea(
        area: PolygonBuilder.() -> Unit,
        onStanding: suspend () -> Unit
    ) {
        standingAreas.add(StandingArea(polygon(area), onStanding))
    }

    fun Actor.addToScenario() {
        container.addToContainer()
        initialize()
    }

    fun draw(container: Container) {
        walkableArea.draw(container)
        standingAreas.forEach { it.draw(container) }
    }
}

suspend fun buildSimpleScenario(
    background: String,
    character: CharacterActor,
    walkableArea: WalkableArea
): Scenario = Scenario(
    resourcesVfs["$background.png"].readBitmap(),
    character,
    walkableArea
)

suspend fun Container.simpleScenario(
    background: String,
    character: CharacterActor,
    walkableArea: WalkableArea,
    setupCallback: suspend Scenario.() -> Unit = {}
): Scenario = buildSimpleScenario(background, character, walkableArea)
    .apply {
        addTo(this@simpleScenario)
        setupCallback()
    }
