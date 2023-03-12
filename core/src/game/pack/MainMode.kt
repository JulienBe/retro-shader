package game.pack

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import kotlin.math.abs

class MainMode {

    private val keys: Map<Int, Dir> = mapOf(Keys.UP to Dir.UP, Keys.DOWN to Dir.DOWN, Keys.LEFT to Dir.LEFT, Keys.RIGHT to Dir.RIGHT)
    private val blocks = mutableListOf<Block>()
    private val grid: List<Cell> = (0..8).map { Cell(Pos(it % 3, (it / 3f).toInt())) }
    private var state = State.GAME
    private val blockInPlace: (Block) -> Boolean = { b: Block -> abs(b.actualPos.x - b.desiredX) < 1f && abs(b.actualPos.y - b.desiredY) < 1f }

    fun act(delta: Float) {
        when (state) {
            State.GAME ->
                keys.forEach {
                    if (Gdx.input.isKeyJustPressed(it.key))
                        actOnBlock(it.value)
                }
            State.TRANSITION -> {
                blocks.forEach {
                    it.actualPos.x -= (it.actualPos.x - it.desiredX) / 8f
                    it.actualPos.y -= (it.actualPos.y - it.desiredY) / 8f
                }
                if (blocks.all { blockInPlace.invoke(it) }) {
                    state = State.GAME
                    blocks.forEach {
                        it.actualPos.x = it.desiredX
                        it.actualPos.y = it.desiredY
                    }
                }
            }
        }
    }

    private fun actOnBlock(dir: Dir) {
        if (moveBlocks(grid, blocks, dir))
            state = State.TRANSITION
        fuseBlocks(blocks)
        if (blocks.size < grid.size)
            spawnBlock(grid, blocks)
    }

    private fun fuseBlocks(blocks: MutableList<Block>) {
        blocks
            .groupBy { it.cell }
            .filterValues { it.size > 1 }
            .values
            .forEach { groupedBlocks ->
                groupedBlocks[0].lvlUp()
                blocks.remove(groupedBlocks[1])
            }
    }

    private fun moveBlocks(grid: List<Cell>, blocks: MutableList<Block>, dir: Dir): Boolean {
        var hasMoved = false
        while (blocks.any { it.move(grid, blocks, dir) }) {
            hasMoved = true
        }
        return hasMoved
    }

    private fun spawnBlock(grid: List<Cell>, blocks: MutableList<Block>) {
        val blockCells = blocks.map { it.cell }
        val spawnCell = grid.filterNot { blockCells.contains(it) }.random()
        blocks.add(
            Block(Data.blockTemplates[0], Vector2(spawnCell.x * Block.w, spawnCell.y * Block.w), spawnCell)
        )
    }

    fun draw(batch: SpriteBatch) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        batch.begin()
        blocks.forEach {
            batch.draw(it.template.texture, it.actualPos.x, it.actualPos.y, Block.w, Block.w)
        }
        batch.end()
    }

    fun dispose() {}
}

private fun Block.lvlUp() {
    template = Data.blockTemplates[template.lvl + 1]
}

private fun Block.move(grid: List<Cell>, blocks: MutableList<Block>, dir: Dir): Boolean {
    val newPos = Pos(cell.x + dir.x, cell.y + dir.y)
    val newCell = grid.firstOrNull { it.pos == newPos }
        ?: return false
    val blocksAlreadyThere = blocks.filter { it.cell == newCell }
    val sameLvlBlocks = blocksAlreadyThere.count { it.template.lvl == template.lvl }
    if (sameLvlBlocks > 1)
        return false
    if (blocksAlreadyThere.size > sameLvlBlocks)
        return false
    cell = newCell
    return true
}