package car.superfun.game;

import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import car.superfun.game.gameModes.gladiatorMode.GladiatorMode;
import car.superfun.game.gameModes.raceMode.RaceMode;
import car.superfun.game.googlePlayGameServices.GoogleGameServices;
import car.superfun.game.menus.LoadingScreen;
import car.superfun.game.menus.LoginMenu;
import car.superfun.game.menus.MainMenu;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.NewState;


public class CarSuperFun extends ApplicationAdapter {

    private GameStateManager gsm;
    private SpriteBatch batch;
    private GoogleGameServices googleGameServices;
    private AndroidLauncher androidLauncher;

    private boolean justPressedBack;

    ArrayList<NewState> statesToBeCreated = new ArrayList<>();

    /**
     * Sets up the app
     */

    public CarSuperFun(GoogleGameServices googleGameServices, AndroidLauncher androidLauncher) {
        this.googleGameServices = googleGameServices;
        this.androidLauncher = androidLauncher;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        gsm = GameStateManager.getInstance();

        //sets the background color (not usually seen) to black
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // Starts the game with logging in

        gsm.push(new LoginMenu(googleGameServices));

        // Take control of the back button
        Gdx.input.setCatchBackKey(true);
        justPressedBack = false;
    }


    @Override
    public void resume() {
        super.resume();

        // Initiate all states in the enum list
        for (NewState state : statesToBeCreated) {
            switch (state) {
                case RACE_MODE:
                    Log.d("CarSuperFun", "Pushed RaceMode");
                    GameStateManager.getInstance().set(new RaceMode(googleGameServices, GlobalVariables.SINGLE_PLAYER));
                    Gdx.input.setInputProcessor(null);
                    break;
                case MAIN_MENU:
                    Log.d("CarSuperFun", "Pushed MainMenu");
                    GameStateManager.getInstance().push(new MainMenu(googleGameServices));
                    break;
                case LOGIN_MENU:
                    Log.d("CarSuperFun", "Pushed LoginMenu");
                    GameStateManager.getInstance().push(new LoginMenu(googleGameServices));
                    break;
                case GLADIATOR_MODE:
                    Log.d("CarSuperFun", "Pushed GladiatorMode");
                    GameStateManager.getInstance().set(new GladiatorMode(googleGameServices, GlobalVariables.SINGLE_PLAYER));
                    Gdx.input.setInputProcessor(null);
                    break;
            }
        }
        statesToBeCreated.clear();
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
            if (gsm.isInMainMenu()) {
                androidLauncher.finish();
                System.exit(0);
            } else if(gsm.peek() instanceof LoadingScreen) {
                return;
            } else {
                justPressedBack = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        justPressedBack = false;
                    }
                }, 800);
                if (gsm.isInGameMode()) {
                    googleGameServices.leaveRoom();
                }
                gsm.pop();
                Gdx.input.setInputProcessor(MainMenu.stage);
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


    /**
     * Adds the enum corresponding to the class that will be initiated on next resume
     *
     * @param newState
     */
    public void createNewState(NewState newState) {
        statesToBeCreated.add(newState);
    }
}
