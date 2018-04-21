package car.superfun.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.util.ArrayList;

import car.superfun.game.cars.OpponentCarController;
import car.superfun.game.googleGamePlayServices.Communicator;
import car.superfun.game.googleGamePlayServices.GoogleGameServices;
import car.superfun.game.googleGamePlayServices.SetUpGame;
import car.superfun.game.googleGamePlayServices.SignIn;
import car.superfun.game.states.NewState;

public class AndroidLauncher extends AndroidApplication {

    final static String TAG = "CarSuperFun";

    public SetUpGame setUpGame;
    public SignIn signIn;
    public Communicator communicator;

    private CarSuperFun carSuperFun;
    private NewState newState = NewState.RACE_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.carSuperFun = new CarSuperFun(googleGameServices, this);
        initialize(carSuperFun, config);

        this.setUpGame = new SetUpGame(this);
        this.signIn = new SignIn(this, setUpGame, carSuperFun);
        this.communicator = setUpGame.getCommunicator();

        signIn.setSignInClient();

        ClockSynchronizer clockSync = new ClockSynchronizer();
        clockSync.start();
        Gdx.graphics.setContinuousRendering(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        signIn.signInSilently();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == signIn.SIGN_IN_ID) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                Log.d(TAG, "Login success");
                carSuperFun.createNewState(NewState.LOGIN_MENU);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        } else if (requestCode == SetUpGame.RC_WAITING_ROOM) {
            if (resultCode == Activity.RESULT_OK) {
                setUpGame.waitingRoomReady();
                carSuperFun.createNewState(newState);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                setUpGame.leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setUpGame.leaveRoom();
            }
        }
    }

    private class ClockSynchronizer extends Thread { // TODO make this more reliable
        public void run() {
            if (!TrueTime.isInitialized()) {
                Gdx.app.log("TrueTime", "start of run");
                try {
                    TrueTime.build()
                            .withNtpHost("time.google.com")
                            .withLoggingEnabled(false)
                            .withConnectionTimeout(3_1428)
                            .initialize();
                } catch (IOException ex) {
                    Gdx.app.log("IOException from TrueTime:", ex.getMessage());
                    ex.printStackTrace();
                }
                if (!TrueTime.isInitialized()) {
                    Gdx.app.error("TrueTime", "True time is not initialized");
                } else {
                    Gdx.app.log("TrueTime", "True time now initialized! :D");
                }
            } else {
                Gdx.app.log("TrueTime", "True time all ready initialized! :D");
            }
        }
    }

    public void setNewState(NewState newState) {
        this.newState = newState;
    }

    public GoogleGameServices googleGameServices = new GoogleGameServices() {

        @Override
        public void broadcastState(Vector2 velocity, Vector2 position, float angle, float forward, float rotation) {
            communicator.broadcastState(velocity, position, angle, forward, rotation);
        }

        @Override
        public void broadcastScore(int score) {
            communicator.broadcastScore(score);
        }

        @Override
        public Array<OpponentCarController> getOpponentCarControllers() {
            return communicator.getOpponentCarControllers();
        }

        @Override
        public boolean isSignedIn() {
            return signIn.isSignedIn();
        }

        @Override
        public void signOut() {
            signIn.signOut();
        }

        @Override
        public void startSignInIntent() {
            signIn.startSignInIntent();
        }

        @Override
        public void startQuickGame(NewState newState, int numberOfPlayers) {
            setUpGame.startQuickGame(newState, numberOfPlayers);
        }

        @Override
        public void leaveRoom() {
            setUpGame.leaveRoom();
        }

        @Override
        public void readyToStart(boolean isSinglePlayer) {
            communicator.readyToStart(isSinglePlayer);
        }

        @Override
        public boolean gameStarted() {
            return communicator.gameStarted;
        }

        @Override
        public long getStartTime() {
            return communicator.startTime;
        }

        public ArrayList<Participant> getParticipants () {
            return setUpGame.participants;
        }

        @Override
        public String getMyID () {
            return setUpGame.myId;
        }

        @Override
        public Participant getLocalParticipant() {
            return setUpGame.getLocalParticipant();
        }
    };
}