package game.pack

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

enum class Dir(val pos: Pos) { UP(Pos(0, 1)), DOWN(Pos(0, -1)), LEFT(Pos(-1, 0)), RIGHT(Pos(1, 0));
    val x: Int
        get() = pos.x
    val y: Int
        get() = pos.y
}
object Data {
    val blockTemplates = (0..8).map {
        BlockTemplate(it, Texture("block_img$it.png"))
    }
}
data class Pos(val x: Int, val y: Int)
data class BlockTemplate(val lvl: Int, val texture: Texture)
data class Block(var template: BlockTemplate, val actualPos: Vector2, var cell: Cell) {
    companion object {
        const val w = 100f
    }
}

data class Cell(val pos: Pos) {
    val x: Int
        get() = pos.x
    val y: Int
        get() = pos.y
}