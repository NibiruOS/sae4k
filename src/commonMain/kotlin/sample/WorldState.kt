package sample

class WorldState(
    val inventory: Inventory = Inventory(),
    var duckViewed: Boolean = false
)

class Inventory(var pacman: Boolean = false)