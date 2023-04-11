package game.pack

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport


class GameLoop : ApplicationAdapter() {

    lateinit var viewport: Viewport
    lateinit var environment: Environment
    lateinit var cam: PerspectiveCamera
    lateinit var modelBatch: ModelBatch
    lateinit var camController: CameraInputController
    lateinit var fbo: FrameBuffer
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        modelBatch = ModelBatch()

        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()
        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController
        viewport = FitViewport(160f, 144f, cam)

        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width / 10, Gdx.graphics.height / 10, false)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render() {
        if (Gdx.input.justTouched()) {
            Cubeticle(environment).add()
        }
        fbo {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.width / 10, Gdx.graphics.height / 10)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

            camController.update()

            modelBatch.begin(cam)
            Cubeticle.draw(modelBatch)
            modelBatch.end()
        }


        val texture = fbo.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        val textureRegion = TextureRegion(texture)
        textureRegion.flip(false, true)

        batch.begin()
        batch.draw(textureRegion, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        modelBatch.dispose()
//        model.dispose()
    }
}

private operator fun FrameBuffer.invoke(draw: () -> Unit) {
    begin()
    draw.invoke()
    end()
}
