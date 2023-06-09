/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package game.pack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MyShaderProvider implements ShaderProvider {
    protected Array<Shader> shaders = new Array<>();
    public final MyShader.Config config;

    public MyShaderProvider(final String vertexShaderPath, final String fragmentShaderPath) {
        this(new MyShader.Config(Gdx.files.internal(vertexShaderPath).readString(), Gdx.files.internal(fragmentShaderPath).readString()));
    }

    public MyShaderProvider(final MyShader.Config config) {
        this.config = (config == null) ? new MyShader.Config() : config;
    }

    @Override
    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;
        for (Shader shader : shaders) {
            if (shader.canRender(renderable)) return shader;
        }
        final Shader shader = createShader(renderable);
        if (!shader.canRender(renderable))
            throw new GdxRuntimeException("unable to provide a shader for this renderable");
        shader.init();
        shaders.add(shader);
        return shader;
    }

    protected Shader createShader(final Renderable renderable) {
        return new MyShader(renderable, config);
    }

    @Override
    public void dispose() {
        for (Shader shader : shaders) {
            shader.dispose();
        }
        shaders.clear();
    }
}