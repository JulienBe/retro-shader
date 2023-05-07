package game.pack

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Array

class Cubeticle(environment: Environment) {

    companion object {
        private val modelBuilder = ModelBuilder()
        private val cubeticles = Array<Cubeticle>()
        private var row = 0f

        fun draw(modelBatch: ModelBatch) {
            cubeticles.forEach {
                modelBatch.render(it.renderable)
            }
        }
    }

    private val model = modelBuilder.createBox(
        1f, 1f, 1f,
        Material(ColorAttribute.createDiffuse(PaletteColor.next().color)),
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
    )
    private val instance = ModelInstance(model)
    private val renderable = Renderable()

    init {
        renderable.environment = environment
        renderable.meshPart.set(instance.nodes.first().parts.first().meshPart)
        renderable.material = instance.nodes.first().parts.first().material
        renderable.worldTransform.idt()
    }

    fun add() {
        cubeticles.add(this)
        renderable.worldTransform.translate(row, row, row)
        row += 2f
    }
}