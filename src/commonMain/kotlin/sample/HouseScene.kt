package sample

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import io.github.nibiruos.sae4k.*
import sample.Entrance.HOUSE

class HouseScene : Scene() {
    override suspend fun Container.sceneInit() {
        val state = injector.get<WorldState>()

        val floor = floor(walkableArea({
            0 _ 426
            110 _ 385
            390 _ 350
            310 _ 270
            400 _ 260
            500 _ 330
            650 _ 290
            800 _ 290
            800 _ 310
            550 _ 426
        }) {
            innerPolygon {
                600 _ 340
                660 _ 360
                550 _ 360
            }
        }, Angle.fromDegrees(30))

        val entrance = injector.getOrNull() ?: HOUSE
        val mainCharacter = simpleCharacter(
            "guybrush",
            entrance.position,
            entrance.toRight,
            6,
            floor
        )

        simpleScenario(
            "house",
            mainCharacter,
            floor
        ) {
            //draw(this@sceneInit)
            if (!state.inventory.pacman) {
                simpleActor("pacman", 1, 160 _ 320) {
                    onClick {
                        actions(action("Look") {
                            mainCharacter.walkTo(220 _ 400)
                            mainCharacter.showText("It looks like a good idea for\nan arcade game character")
                        }, action("Pickup") {
                            mainCharacter.walkTo(220 _ 400)
                            sprite.removeFromParent()
                            state.inventory.pacman = true
                        })
                    }
                }
            }

            simpleActor("robot", 1, 600 _ 360) {
                onClick {
                    if (!state.duckViewed) {
                        actions(action("What is this???") {
                            mainCharacter.walkTo(500 _ 360)
                            mainCharacter.walkTo(510 _ 360)
                            mainCharacter.showText("It looks like a robot duck!!!")
                            state.duckViewed = true
                        })
                    } else {
                        mainCharacter.showText("That robot duck is really scary")
                    }
                }
            }

            standingArea({
                750 _ 290
                800 _ 290
                800 _ 310
                750 _ 350
            }) {
                sceneContainer.changeTo<DockScene>()
            }
        }
    }
}

enum class Entrance(
    val toRight: Boolean,
    val position: Point
) {
    HOUSE(true, 350 _ 280),
    RIGHT(false, 790 _ 300),
    LEFT(true, 10 _ 420)
}
