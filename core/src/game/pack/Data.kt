package game.pack

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

object Data {
    val blockTemplates = (0..8).map {
        BlockTemplate(it, Texture("block_img$it.png"))
    }
}
enum class Dir(val x: Int, val y: Int) { UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);
}
enum class GameState {
    GAME, TRANSITION
}
data class Pos(val x: Int, val y: Int)
data class BlockTemplate(val lvl: Int, val texture: Texture)
data class Block(var template: BlockTemplate, val actualPos: Vector2, var cell: Cell, var actualW: Float = 0f) {
    val desiredX: Float
        get() = cell.x * w
    val desiredY: Float
        get() = cell.y * w

    companion object {
        const val w = 250f
    }
}

data class Cell(val pos: Pos) {
    val x: Int
        get() = pos.x
    val y: Int
        get() = pos.y
}