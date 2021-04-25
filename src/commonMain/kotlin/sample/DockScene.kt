package sample

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import io.github.nibiruos.sae4k.`_`
import io.github.nibiruos.sae4k.simpleCharacter
import io.github.nibiruos.sae4k.simpleScenario
import io.github.nibiruos.sae4k.walkableArea

class DockScene : Scene() {
    override suspend fun Container.sceneInit() {
        simpleScenario(
            "dock",
            simpleCharacter(
                "guybrush",
                10 _ 400,
                true,
                6
            ),
            walkableArea({
                0 _ 410
                0 _ 370
                700 _ 370
                700 _ 410
            })
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