package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class HostMenu extends State{
    private Texture background, backButton;

    public HostMenu(){
        background = new Texture("background.png");
        backButton = new Texture("menu-buttons/back.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            if (isOnBack()) {
                GameStateManager.getInstance().pop();
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(backButton, 120, 890);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        backButton.dispose();
    }

    private boolean isOnBack(){
        Circle textureBounds = new Circle(120+backButton.getWidth()/2, (Gdx.graphics.getHeight() - 890)-backButton.getHeight()/2, backButton.getWidth()/2);
        return(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY()));
    }
}
