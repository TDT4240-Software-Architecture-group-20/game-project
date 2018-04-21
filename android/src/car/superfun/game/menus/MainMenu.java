package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import car.superfun.game.googlePlayGameServices.GoogleGameServices;
import car.superfun.game.states.NewState;
import car.superfun.game.actors.ButtonActor;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class MainMenu extends State {
    public static Stage stage = new Stage(new ScreenViewport());
  
    private Texture background;

    public MainMenu(final GoogleGameServices googleGameServices) {
        background = new Texture("background.png");

        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, Gdx.graphics.getHeight());

        ButtonActor settingsButton = new ButtonActor("menu-buttons/settings.png");
        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().push(new SettingsMenu());
                return true;
            }
        });

        ButtonActor raceButton = new ButtonActor("menu-buttons/race.png");
        raceButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().push(new GameSettings(NewState.RACE_MODE, googleGameServices));
                return true;
            }
        });

        ButtonActor gladiatorButton = new ButtonActor("menu-buttons/gladiator.png");
        gladiatorButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().push(new GameSettings(NewState.GLADIATOR_MODE, googleGameServices));
                return true;
            }
        });

        table.add(settingsButton).expandX().top().right().padBottom(120).padRight(stage.getWidth()/50).padTop(stage.getHeight()/30);
        table.row();
        table.add(raceButton).padBottom(120).center();
        table.row();
        table.add(gladiatorButton).center();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
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
