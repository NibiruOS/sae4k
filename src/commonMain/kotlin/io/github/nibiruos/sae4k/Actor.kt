package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Sprite
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size

interface Actor {
    val sprite: Sprite // TODO: try avoid exposing the sprite?

    val size: Size

    val feetPosition: Point

    val headPosition: Point

    fun initialize()

    suspend fun moveTo(path: List<Point>)
}