package game.pack

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import ktx.assets.load

class Palette {

    val palettes = arrayListOf(
        getTexture("img/palette_improved"),
        getTexture("img/palette"),
        getTexture("img/palette_c64"),
        getTexture("img/palette_gbc"),
        getTexture("img/palette_gameboy"),
        getTexture("img/palette_pico8")
    )

    private fun getTexture(path: String): Texture {
        manager.load<Texture>("$path.png")
        manager.finishLoading()
        val t = manager.get<Texture>("$path.png")
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        return t
    }

    fun music(s: String): Music {
        manager.load<Music>("music/${s}.wav")
        manager.finishLoading()
        return manager.get("music/${s}.wav")
    }

    companion object {
        val manager = AssetManager()
    }
}