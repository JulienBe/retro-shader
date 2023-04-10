package game.pack

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport


class GameLoop : ApplicationAdapter() {

    lateinit var viewport: Viewport
    lateinit var environment: Environment
    lateinit var cam: PerspectiveCamera
    lateinit var model: Model
    lateinit var modelBatch: ModelBatch
    lateinit var instance: ModelInstance
    lateinit var camController: CameraInputController
    lateinit var fbo: FrameBuffer
    lateinit var batch: SpriteBatch
    lateinit var renderable: Renderable
    lateinit var shader: Shader
    lateinit var renderContext: RenderContext //Here we removed the ModelBatch and added a RenderContext and Shader. A RenderContext keeps tracks of the OpenGL state to eliminate state switching between shader switches. For example, if a Texture is already bound, it doesn’t need to be bound again

    override fun create() {
        batch = SpriteBatch()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
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


        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(
            5f, 5f, 5f,
            Material(ColorAttribute.createDiffuse(Color.GREEN)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )

        instance = ModelInstance(model)

        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10, false)

        renderable = Renderable()
        renderable.environment = environment
        renderable.meshPart.set(instance.nodes.first().parts.first().meshPart)
        renderable.material = instance.nodes.first().parts.first().material
        renderable.worldTransform.idt()

        renderContext = RenderContext(DefaultTextureBinder(1))
        shader = DefaultShader(renderable, DefaultShader.Config(Gdx.files.internal("shader/my_vert.glsl").readString(), Gdx.files.internal("shader/my_frag.glsl").readString()))
        shader.init()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render() {
        fbo.begin()
            Gdx.gl.glViewport(0, 0, Gdx.graphics.width / 10, Gdx.graphics.height / 10)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

            camController.update()

            renderContext.begin()
            shader.begin(cam, renderContext)
            shader.render(renderable)
            shader.end()
            renderContext.end()
//            modelBatch.begin(cam)
//            modelBatch.render(instance, environment, shader)
//            modelBatch.render(renderable)
//            modelBatch.end()
        fbo.end()

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
        model.dispose()
    }
}