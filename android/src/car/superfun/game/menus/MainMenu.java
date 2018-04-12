package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import car.superfun.game.actor.ButtonActor;
import car.superfun.game.AndroidLauncher;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.gameModes.gladiatorMode.GladiatorMode;
import car.superfun.game.gameModes.raceMode.RaceMode;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class MainMenu extends State {
    private Texture background;
    public static Stage stage = new Stage(new ScreenViewport());
    private AndroidLauncher androidLauncher;

    public MainMenu(AndroidLauncher aLauncher){
        this.androidLauncher = aLauncher;

        background = new Texture("background.png");

        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);

        table.setPosition(0,Gdx.graphics.getHeight());

        ButtonActor settingsButton = new ButtonActor(new Sprite(new Texture("menu-buttons/settings.png")));
        settingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //androidLauncher.signOut();
                //GameStateManager.getInstance().push(new SettingsMenu());
                GameStateManager.getInstance().push(new Leaderboard());
                return true;
            }
        });

        ButtonActor extraSettingsButton = new ButtonActor(new Sprite(new Texture("menu-buttons/settings.png")));
        extraSettingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().push(new GladiatorMode());
                return true;
            }
        });

        ButtonActor joinButton = new ButtonActor(new Sprite(new Texture("menu-buttons/join.png")));
        joinButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                androidLauncher.startQuickGame();
                GameStateManager.getInstance().push(new GameBrowser());
                return true;
            }
        });

        ButtonActor hostButton = new ButtonActor(new Sprite(new Texture("menu-buttons/host.png")));
        hostButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().push(new HostMenu());
                // starting PlayState instead, so that we can test the game
                RaceMode race = new RaceMode(androidLauncher, false);

//                race.setLocalRaceCar(new Vector2(1600, 11000));
//                Array<Vector2> opponentCars = new Array<Vector2>();
//                opponentCars.add(new Vector2(1600, 11050));
//                race.setOpponentCars(opponentCars);

                GameStateManager.getInstance().push(race);
                return true;
            }
        });

        table.add(extraSettingsButton).expandX().top().left();
        table.add(settingsButton).expandX().top().right().padBottom(120);
        table.row();
        table.add(joinButton).padBottom(120).colspan(2);
        table.row();
        table.add(hostButton).colspan(2);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() { }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        sb.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }
}
