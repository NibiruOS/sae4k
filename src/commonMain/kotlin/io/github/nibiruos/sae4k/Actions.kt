package io.github.nibiruos.sae4k

import com.soywiz.korge.view.*

class Actions(vararg actions: Action) {
    val view: View

    init {
        view = Container().apply {
            var totalHeight = 0.0;
            var maxWidth = 0.0;

            val blocks = actions.map { action ->
                val textBlock = textBlock(action.text) {
                    dispose()
                    action.callback()
                }
                textBlock.view.positionY(totalHeight)
                totalHeight += textBlock.view.height
                if (textBlock.view.width > maxWidth) {
                    maxWidth = textBlock.view.width
                }
                textBlock
            }
            size(maxWidth, totalHeight)
            solidRect(
                maxWidth,
                totalHeight,
                color = Parameters.TEXT_BACKGROUND
            )
            blocks.forEach { addChild(it.view) }
        }
    }

    fun dispose() {
        view.removeFromParent()
    }
}

class Action(val text: String, val callback: suspend () -> Unit)

fun Container.actions(
    vararg actions: Action
) = Actions(*actions).also {
    addChild(it.view)
}

fun action(
    text: String,
    callback: suspend () -> Unit
) = Action(text, callback)