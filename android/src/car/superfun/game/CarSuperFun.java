package car.superfun.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Timer;
import java.util.TimerTask;

import car.superfun.game.gameModes.raceMode.RaceMode;
import car.superfun.game.menus.LoginMenu;
import car.superfun.game.menus.MainMenu;
import car.superfun.game.states.GameStateManager;


public class CarSuperFun extends ApplicationAdapter {

    private GameStateManager gsm;
    private SpriteBatch batch;
    private AndroidLauncher androidLauncher;

    private boolean justPressedBack;

    /**
     * Sets up the app
     */

    public CarSuperFun(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        gsm = GameStateManager.getInstance();

        //sets the color to black
        Gdx.gl.glClearColor(0, 0, 1, 1);

        // Starts the game in MainMenu

        gsm.push(new LoginMenu(androidLauncher));
        //gsm.push(new MainMenu(androidLauncher));

        // Take control of the back button
        Gdx.input.setCatchBackKey(true);
        justPressedBack = false;
    }

    /**
     * Renders the scene
     * by updating and rendering the current state given by the gsm
     */
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());

        // render the batch
        gsm.render(batch);


        // TODO: implement this in a less hacky way
        // Pop the game state when pressing back.
        // Unless the current state is the bottom state (should be MainMenu),
        // in such case the app is closed
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) && !justPressedBack) {
            if (gsm.isOnlyOneLeft()) {
                Gdx.app.exit(); // TODO this can no longer be reached
            } else {
                justPressedBack = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        justPressedBack = false;
                    }
                }, 1500);
                gsm.pop();
            }
        }
    }
    /**
     * get gsm to dispose
     */
    @Override
    public void dispose() {
        gsm.dispose();
        batch.dispose();
    }

    public void startGame(AndroidLauncher androidLauncher) {
        gsm.push(new RaceMode(androidLauncher, false));
    }
}
