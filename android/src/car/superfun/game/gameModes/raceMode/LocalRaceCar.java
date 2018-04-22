package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Arrays;

import car.superfun.game.GlobalVariables;
import car.superfun.game.cars.LocalCarController;
import car.superfun.game.cars.LocalCar;

public class LocalRaceCar extends LocalCar {
    boolean[] passedCheckpoints;
    int completedRounds;

    private boolean doRotate;
    private float rotateBy;
    private RaceMode raceMode;

    public LocalRaceCar(Vector2 position, LocalCarController localCarController, World world,  RaceMode raceMode, int amountOfCheckpoints, String texturePath) {
        super(position, localCarController, world, texturePath);
        this.raceMode = raceMode;
        passedCheckpoints = new boolean[amountOfCheckpoints];
        Arrays.fill(passedCheckpoints, Boolean.FALSE);
        completedRounds = 0;
        doRotate = false;
        rotateBy = 0f;
    }

    public void passCheckpoint(int checkpointId) {
        passedCheckpoints[checkpointId] = true;
    }

    public void passGoal() {
        if (allCheckpointsPassed()) {
            completedRounds++;
            Gdx.app.log("GOAL PASSED!", "" + completedRounds + " rounds passed");
            Arrays.fill(passedCheckpoints, Boolean.FALSE);
            if (completedRounds == 3) {
                raceMode.endGame();
            }
        }
    }

    public void update(float dt) {
        super.update(dt);
        if (GlobalVariables.TESTING_MODE && doRotate) {
            body.setTransform(body.getPosition(), body.getTransform().getRotation() + rotateBy);
            doRotate = false;
            rotateBy = 0f;
        }
    }

    private boolean allCheckpointsPassed() {
        for (boolean checkpointPassed : passedCheckpoints) {
            if (!checkpointPassed) {
                return false;
            }
        }
        return true;
    }

    public void rotateBy(float angle) {
        if (!GlobalVariables.TESTING_MODE) {
            return;
        }
        doRotate = true;
        rotateBy = angle;
    }
}
