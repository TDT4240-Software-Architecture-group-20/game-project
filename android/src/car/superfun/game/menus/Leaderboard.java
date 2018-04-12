package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import car.superfun.game.actor.ButtonActor;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class Leaderboard extends State {
    private Texture background;
    private Stage stage;
    private ArrayList<String> playerList;
    private Table table;

    public Leaderboard(){
        this.stage = new Stage(new ScreenViewport());
        background = new Texture("background.png");

        playerList = new ArrayList<>();

        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());

        fillTable();
        setInputProcessor();
    }

    @Override
    public void handleInput() { }

    @Override
    public void update(float dt) { }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        sb.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void updateTable(String player){
        clearTable();
        placePlayer(player);
        fillTable();
    }

    private void fillTable(){
        Skin headerSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        headerSkin.getFont("default-font").getData().setScale(6f,6f);
        skin.getFont("default-font").getData().setScale(4f,4f);

        ButtonActor backButton = new ButtonActor(new Sprite(new Texture("menu-buttons/back.png")));
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().pop();
                Gdx.input.setInputProcessor(MainMenu.stage);
                //updateTable("heheh");
                return true;
            }
        });

        table.add(backButton).expandX().left().colspan(2).padBottom(-60);
        table.row();
        table.add(new Label("Leaderboard", headerSkin)).center().colspan(2);
        if(!playerList.isEmpty()){
            int pos = 1;
            for(String player : playerList){
                table.row();
                table.add(new Label(Integer.toString(pos)+".", skin)).right().padRight(80);
                table.add(new Label(player, skin)).left().width(stage.getWidth()/6);
                pos += 1;
            }
        }

        stage.addActor(table);
    }

    private void clearTable(){
        table.clearChildren();
    }

    private void setInputProcessor(){
        Gdx.input.setInputProcessor(stage);
    }

    private void placePlayer(String player){
        playerList.add(player);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
