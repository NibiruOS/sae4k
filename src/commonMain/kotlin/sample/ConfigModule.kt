package sample

import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlin.reflect.KClass

object ConfigModule : Module() {
    override val bgcolor = Colors["#2b2b2b"]
    override val size = SizeInt(800, 429)
    override val mainScene: KClass<out Scene> = HouseScene::class

    override suspend fun AsyncInjector.configure() {
        mapSingleton { WorldState() }
        mapPrototype { HouseScene() }
        mapPrototype { DockScene() }
    }
}