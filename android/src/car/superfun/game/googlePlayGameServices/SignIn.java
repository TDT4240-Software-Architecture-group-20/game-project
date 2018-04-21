package car.superfun.game.googlePlayGameServices;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.CarSuperFun;
import car.superfun.game.states.NewState;


public class SignIn  {

    private final static String TAG = "SignIn";

    private AndroidLauncher androidLauncher;

    // Request code used to invoke sign in user interactions.
    public static final int SIGN_IN_ID = 9001;

    private CarSuperFun carSuperFun;
    private SetUpGame setUpGame;
    // Client used to sign in with Google APIs
    private GoogleSignInClient googleSignInClient = null;

    public SignIn(AndroidLauncher androidLauncher, SetUpGame setUpGame, CarSuperFun carSuperFun) {
        this.setUpGame = setUpGame;
        this.androidLauncher = androidLauncher;
        this.carSuperFun = carSuperFun;
    }

    public void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(androidLauncher, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        androidLauncher.startActivityForResult(intent, SIGN_IN_ID);
    }

    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(androidLauncher,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(androidLauncher,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        carSuperFun.createNewState(NewState.LOGIN_MENU);
                    }
                });
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        googleSignInClient.silentSignIn().addOnCompleteListener(androidLauncher,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                                setUpGame.onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                        }
                    }
                });
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(androidLauncher) != null;
    }

    public GoogleSignInClient getSignInClient(){
        return googleSignInClient;
    }

    public void setSignInClient(){
        googleSignInClient = GoogleSignIn.getClient(androidLauncher, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    }
}
