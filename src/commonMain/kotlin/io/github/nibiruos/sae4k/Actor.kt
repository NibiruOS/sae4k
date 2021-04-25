package io.github.nibiruos.sae4k

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Sprite
import com.soywiz.korma.geom.Orientation
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size

interface Actor {
    val feetPosition: Point

    val headPosition: Point

    fun initialize()

    suspend fun moveTo(path: List<Point>)

    fun Container.addToContainer()

    fun removeFromContainer()
}