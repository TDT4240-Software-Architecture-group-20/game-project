package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.googlePlayGameServices.GoogleGameServices;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class LoginMenu extends State{

    private Texture background;
    private Sprite loginButton;
    private GoogleGameServices googleGameServices;


    public LoginMenu(GoogleGameServices googleGameServices){
        this.googleGameServices = googleGameServices;
        background = new Texture("background.png");
        loginButton = new Sprite(new Texture("menu-buttons/google.png"));
        loginButton.setPosition(Gdx.graphics.getWidth()/2 - loginButton.getWidth()/2, Gdx.graphics.getHeight()/2 - loginButton.getHeight()/2);
    }


    public void handleInput() {
        if(Gdx.input.justTouched()) {
            if (loginButton.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                googleGameServices.startSignInIntent();
            }
        }
    }

    private void tryNextWindow() {
        if(googleGameServices.isSignedIn()) {
            GameStateManager.getInstance().push(new MainMenu(googleGameServices));
        }
    }

    @Override
    public void update(float dt) {
        tryNextWindow();
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(loginButton, loginButton.getX(), loginButton.getY());
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        loginButton.getTexture().dispose();
    }
}
