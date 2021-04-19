package sample

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korma.geom.Angle
import io.github.nibiruos.sae4k.*

class DockScene : Scene() {
    override suspend fun Container.sceneInit() {
        val floor = floor(walkableArea({
            0 _ 410
            0 _ 370
            700 _ 370
            700 _ 410
        }), Angle.fromDegrees(30))

        simpleScenario(
            "dock",
            simpleCharacter(
                "guybrush",
                10 _ 400,
                true,
                6,
                floor
            ),
            floor
        ) {
            standingArea({
                0 _ 410
                0 _ 370
                50 _ 370
                50 _ 410
            }) {
                sceneContainer.changeTo<HouseScene>(Entrance.RIGHT)
            }
            //draw(this@sceneInit)
        }
    }
}