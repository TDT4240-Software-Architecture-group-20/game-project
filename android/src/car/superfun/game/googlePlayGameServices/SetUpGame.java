package car.superfun.game.googlePlayGameServices;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.states.NewState;
import car.superfun.game.R;
import car.superfun.game.cars.OpponentCarController;


public class SetUpGame {

    private int lastTimestamp;

    private final static String TAG = "SetUpGame";

    private AndroidLauncher androidLauncher;

    private Communicator communicator;

    private RoomConfig joinedRoomConfig = null;

    private String playerId;

    public String roomId;

    private Participant localParticipant;


    public final static int RC_WAITING_ROOM = 10002;

    // The participants in the currently active game
    public ArrayList<Participant> participants = null;


    // My participant ID in the currently active game
    public String myId = null;


    // Client used to interact with the real time multiplayer system.
    public RealTimeMultiplayerClient realTimeMultiplayerClient = null;

    public SetUpGame(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
        this.communicator = new Communicator(this);
        lastTimestamp = 0;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public void startQuickGame(NewState newState, int numberOfPlayers) {
        androidLauncher.setNewState(newState);
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(numberOfPlayers, numberOfPlayers, 0);

        // build the room config:
        joinedRoomConfig =
                RoomConfig.builder(roomUpdateCallback)
                        .setOnMessageReceivedListener(communicator.messageReceivedHandler)
                        .setRoomStatusUpdateCallback(roomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();

        // create room:
        Games.getRealTimeMultiplayerClient(androidLauncher, GoogleSignIn.getLastSignedInAccount(androidLauncher))
                .create(joinedRoomConfig);
    }

    private RoomUpdateCallback roomUpdateCallback = new RoomUpdateCallback() {
        // Called when room has been created
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            roomId = room.getRoomId();

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                return;
            }
            // show the waiting room UI
            showWaitingRoom(room);
        }

        @Override
        public void onLeftRoom(int statusCode, @NonNull String roomId) {
            Log.d(TAG, "onLeftRoom, code " + statusCode);
        }
    };

    private RoomStatusUpdateCallback roomStatusCallbackHandler = new RoomStatusUpdateCallback() {
        @Override
        public void onConnectedToRoom(Room room) {
            Log.d(TAG, "onConnectedToRoom.");

            participants.clear();
            //get participants and my ID:
            participants = room.getParticipants();
            myId = room.getParticipantId(playerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (roomId == null) {
                roomId = room.getRoomId();
            }

        }

        @Override
        public void onDisconnectedFromRoom(Room room) {
            roomId = null;
            joinedRoomConfig = null;
        }

        @Override
        public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(@NonNull String participant) {
            int index = participants.indexOf(participant);
            if (index >= 0) {
                participants.remove(index);
            }
        }

        @Override
        public void onP2PConnected(@NonNull String participant) {
        }

        @Override
        public void onPeerJoined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {

            updateRoom(room);
            communicator.updateParticipants(peersWhoLeft);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }
    };

    public void leaveRoom() {
        if (roomId != null) {
            realTimeMultiplayerClient.leave(joinedRoomConfig, roomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            roomId = null;
                            joinedRoomConfig = null;
                        }
                    });

        }
    }

    void showWaitingRoom(Room room) {
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        realTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        androidLauncher.startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"));
    }

    GoogleSignInAccount signedInAccount = null;

    public void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (signedInAccount != googleSignInAccount) {

            signedInAccount = googleSignInAccount;

            // update the clients
            realTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(androidLauncher, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(androidLauncher, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            playerId = player.getPlayerId();
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }

    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }

    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = androidLauncher.getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = androidLauncher.getString(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = androidLauncher.getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = androidLauncher.getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = androidLauncher.getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = androidLauncher.getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = androidLauncher.getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = androidLauncher.getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(androidLauncher)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private void updateRoom(Room room) {
        if (room != null) {
            participants = room.getParticipants();
        } else {
            participants.clear();
            communicator.clearParticipantCarController();
        }
    }

    public void waitingRoomReady() {
        communicator.clearParticipantCarController();
        for (Participant participant : participants) {
            if (participant.getParticipantId().equals(myId)) {
                localParticipant = participant;
                continue;
            }
            OpponentCarController opponentCarController = new OpponentCarController(participant);
            communicator.putParticipantController(participant.getParticipantId(), opponentCarController);
            communicator.initiateTimestampMap();
        }
    }

    public Participant getLocalParticipant() {
        return localParticipant;
    }


}
