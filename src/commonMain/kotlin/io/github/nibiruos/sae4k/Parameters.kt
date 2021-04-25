package io.github.nibiruos.sae4k

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle

object Parameters {
    const val TEXT_DISPLAY_TIME = 3000
    val TEXT_COLOR = Colors.BLACK
    val TEXT_BACKGROUND = Colors.WHITE
    const val WALK_SPEED = 200 // px / second
    val DEFAULT_FLOOR_ANGLE = Angle.fromDegrees(30)
}
