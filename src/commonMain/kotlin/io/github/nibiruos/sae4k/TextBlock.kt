package io.github.nibiruos.sae4k

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*

class TextBlock(
    content: String,
    action: (suspend () -> Unit)? = null
) {
    companion object {
        private const val HORIZONTAL_MARGIN = 5
        private const val VERTICAL_MARGIN = 2
    }

    val view: View

    init {
        view = Container().apply {
            val textView = Text(content, color = Parameters.TEXT_COLOR)
            textView.xy(HORIZONTAL_MARGIN, VERTICAL_MARGIN)
            val box = solidRect(
                textView.width + HORIZONTAL_MARGIN * 2,
                textView.height + VERTICAL_MARGIN * 2,
                color = Parameters.TEXT_BACKGROUND
            )
            addChild(textView)
            size(box.width, box.height)
            if (action != null) {
                onClick {
                    dispose()
                    action()
                }
            }
        }
    }

    fun dispose() {
        view.removeFromParent()
    }
}

fun Container.textBlock(
    text: String,
    action: (suspend () -> Unit)? = null
) = TextBlock(text, action).also {
    addChild(it.view)
}