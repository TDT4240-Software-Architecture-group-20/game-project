package car.superfun.game.googlePlayGameServices;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;

import car.superfun.game.cars.OpponentCarController;
import car.superfun.game.states.NewState;

public interface GoogleGameServices {

    void broadcastState(Vector2 velocity, Vector2 position, float angle, float forward, float rotation);
    void broadcastScore(int score);

    Array<OpponentCarController> getOpponentCarControllers();

    boolean isSignedIn();
    void signOut();
    void startSignInIntent();

    void startQuickGame(NewState newState, int numberOfPlayers);
    void leaveRoom();

    void readyToStart(boolean isSinglePlayer);

    boolean gameStarted();
    ArrayList<Participant> getParticipants();
    String getMyID();
    long getStartTime();

    Participant getLocalParticipant();
}
