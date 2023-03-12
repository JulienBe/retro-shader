package game.pack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	MainMode mode;

	@Override
	public void create () {
		batch = new SpriteBatch();
		mode = new MainMode();
	}

	@Override
	public void render () {
		mode.act(Gdx.graphics.getDeltaTime());
		mode.draw(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		mode.dispose();
	}
}
