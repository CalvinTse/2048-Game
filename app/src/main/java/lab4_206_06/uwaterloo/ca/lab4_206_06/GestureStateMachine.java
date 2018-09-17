package lab4_206_06.uwaterloo.ca.lab4_206_06;

public class GestureStateMachine {
    public enum GestureState {WAITING, RISERIGHT, FALLRIGHT, FALLLEFT, RISELEFT, UNKNOWN, RIGHT, LEFT}
    public enum GestureStateY {WAITING, RISEUP, FALLUP, FALLDOWN, RISEDOWN, UNKNOWN, UP, DOWN}

    GestureState gestureState;
    GestureStateY gestureStateY;
    GameLoopTask.GameDirection finalGesture;
    private int counter;
    private int counterY;


    public GestureStateMachine() {
        this.gestureState = gestureState.WAITING;
        this.gestureStateY = gestureStateY.WAITING;
    }

    //One state machine to look at x axis for cleaner code
    public GestureState DetermineGesture(float accelerationDifferenceX, float currentValue) {
        final float RIGHT_RISE_THRESHOLD = 0.29f;
        final float RIGHT_FALL_THRESHOLD = -0.1f;
        final float LEFT_FALL_THRESHOLD = -0.28f;
        final float MIN_LEFT_FALL = -0.3f;
        final float LEFT_RISE_THRESHOLD = 0.1f;
        final float MAX_RIGHT_RISE = 0.4f;

        //Sginuature analysis
        switch (gestureState) {
            //Checks if accleration thresholds are met if met change state
            case WAITING:
                if (accelerationDifferenceX > RIGHT_RISE_THRESHOLD) {
                    gestureState = gestureState.RISERIGHT;
                } else if (accelerationDifferenceX < LEFT_FALL_THRESHOLD) {
                    gestureState = gestureState.FALLLEFT;
                } else {
                    gestureState = gestureState.WAITING;
                }
                break;
            case RISERIGHT:
                //Determine if peak has been passed
                if (currentValue > MAX_RIGHT_RISE && accelerationDifferenceX <= 0) {
                    gestureState = gestureState.FALLRIGHT;
                }
                if (counter > 30) {
                    resetGesture(gestureState);
                }
                counter++;
                break;
            case FALLRIGHT:
                //If thersholds are met the gesture is determined
                if (accelerationDifferenceX >= 0 && currentValue < RIGHT_FALL_THRESHOLD) {
                    gestureState = gestureState.RIGHT;
                }
                if (counter > 30) {
                    gestureState = resetGesture(gestureState);
                }
                counter++;
                break;
            case FALLLEFT:
                if (currentValue < MIN_LEFT_FALL && accelerationDifferenceX >= 0) {
                    gestureState = gestureState.RISELEFT;
                }
                if (counter > 30) {
                    gestureState = resetGesture(gestureState);
                }
                counter++;
                break;
            case RISELEFT:
                //If thersholds are met the gesture is determined
                if (accelerationDifferenceX >= 0 && currentValue > LEFT_RISE_THRESHOLD) {
                    gestureState = gestureState.LEFT;
                }
                if (counter > 30) {
                    gestureState = resetGesture(gestureState);
                }
                counter++;
                break;
            case RIGHT:
                gestureState = resetGesture(gestureState);
                break;
            case LEFT:
                gestureState = resetGesture(gestureState);
                break;
            case UNKNOWN:
                if (counter > 30) {
                    gestureState = resetGesture(gestureState);
                }
                counter++;
                break;
        }
        return gestureState;
    }

    //One state machine to look at y axis for cleaner code
    public GestureStateY DetermineGestureY(float accelerationDifferenceY, float currentValueY) {
        final float UP_RISE_THRESHOLD = 0.25f;
        final float UP_FALL_THRESHOLD = -0.1f;
        final float DOWN_FALL_THRESHOLD = -0.25f;
        final float MIN_LEFT_FALL = -0.3f;
        final float DOWN_RISE_THRESHOLD = 0.1f;
        final float MAX_RIGHT_RISE = 0.4f;

        //Sginuature analysis
        switch (gestureStateY) {
            case WAITING:
                if (accelerationDifferenceY > UP_RISE_THRESHOLD) {
                    gestureStateY = gestureStateY.RISEUP;
                } else if (accelerationDifferenceY < DOWN_FALL_THRESHOLD) {
                    gestureStateY = gestureStateY.FALLDOWN;
                } else {
                    gestureStateY = gestureStateY.WAITING;
                }
                break;
            case RISEUP:
                if (currentValueY > MAX_RIGHT_RISE && accelerationDifferenceY <= 0) {
                    gestureStateY = gestureStateY.FALLUP;
                }
                if (counterY > 30) {
                    resetGestureY(gestureStateY);
                }
                counterY++;
                break;
            case FALLUP:
                //If thersholds are met the gesture is determined
                if (accelerationDifferenceY >= 0 && currentValueY < UP_FALL_THRESHOLD) {
                    gestureStateY = gestureStateY.UP;
                }
                if (counterY > 30) {
                    gestureStateY = resetGestureY(gestureStateY);
                }
                counterY++;
                break;
            case FALLDOWN:
                if (currentValueY < MIN_LEFT_FALL && accelerationDifferenceY >= 0) {
                    gestureStateY = gestureStateY.RISEDOWN;
                }
                if (counterY > 30) {
                    gestureStateY = resetGestureY(gestureStateY);
                }
                counterY++;
                break;
            case RISEDOWN:
                if (accelerationDifferenceY >= 0 && currentValueY > DOWN_RISE_THRESHOLD) {
                    //If thersholds are met the gesture is determined
                    gestureStateY = gestureStateY.DOWN;
                }
                if (counterY > 30) {
                    gestureStateY = resetGestureY(gestureStateY);
                }
                counterY++;
                break;
            case UP:
                gestureStateY = resetGestureY(gestureStateY);
                break;
            case DOWN:
                gestureStateY = resetGestureY(gestureStateY);
                break;
            case UNKNOWN:
                if (counterY > 30) {
                    gestureStateY = resetGestureY(gestureStateY);
                }
                counterY++;
                break;
        }
        return gestureStateY;
    }

    //Combinational logic
    public GameLoopTask.GameDirection ComboLogic (GestureState gestureState, GestureStateY gestureStateY){
        if(gestureState == gestureState.RIGHT && (gestureStateY != gestureStateY.UP || gestureStateY != gestureStateY.DOWN)){
            return finalGesture.RIGHT;
        } else if(gestureState == gestureState.LEFT && (gestureStateY != gestureStateY.UP || gestureStateY != gestureStateY.DOWN)){
            return finalGesture.LEFT;
        } else if(gestureStateY == gestureStateY.UP && (gestureState != gestureState.RIGHT || gestureState != gestureState.LEFT)){
            return finalGesture.UP;
        } else if(gestureStateY == gestureStateY.DOWN && (gestureState != gestureState.RIGHT || gestureState != gestureState.LEFT)){
            return finalGesture.DOWN;
        } else {
            return finalGesture.UNDETERMINED;
        }
    }

    //Sets the counters back to 0 and state to waiting state
    public GestureState resetGesture(GestureState gestureState){
        gestureState = gestureState.WAITING;
        counter = 0;
        return gestureState;
    }

    public GestureStateY resetGestureY(GestureStateY gestureStateY){
        gestureStateY = gestureStateY.WAITING;
        counterY = 0;
        return gestureStateY;
    }
}
