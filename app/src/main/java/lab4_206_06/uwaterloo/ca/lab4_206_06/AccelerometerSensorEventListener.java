package lab4_206_06.uwaterloo.ca.lab4_206_06;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class AccelerometerSensorEventListener implements SensorEventListener {

    GestureStateMachine.GestureState gestureState;
    GestureStateMachine.GestureStateY gestureStateY;
    GameLoopTask.GameDirection finalGesture;
    private TextView localGesture;
    //private LineGraphView localGraph;
    float[] filteredReading = new float[3];
    private float[][] localTracker;
    private float[] maxValue;
    private int counter = 0;
    private float acclerationDifference = 0;
    private float acclerationDifferenceY = 0;
    private GestureStateMachine gestureControl = new GestureStateMachine();
    private float currentValue;
    private float currentValueY;
    private float relativeLocation;
    private GameLoopTask localGameLoop;


    public AccelerometerSensorEventListener(TextView gesture, float[][] meterArray, float[] maxAccel, GameLoopTask gameLoop)
    {
        this.localGesture = gesture;
        this.localTracker = meterArray;
        this.maxValue = maxAccel;
        this.localGameLoop = gameLoop;
    }

    public void onAccuracyChanged(Sensor s, int i)
    {

    }

    public void onSensorChanged(SensorEvent se)
    {
        float[] templine = new float[3];

        //Takes the max value of each component
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            if(maxValue[0] < se.values[0]){
                maxValue[0] = se.values[0];
            }
            if(maxValue[1] < se.values[1]){
                maxValue[1] = se.values[1];
            }
            if(maxValue[2] < maxValue[2]){
                maxValue[2] = se.values[2];
            }
            filteredReading[0] += (se.values[0] - filteredReading[0]) / 50;
            filteredReading[1] += (se.values[1] - filteredReading[1]) / 50;
            filteredReading[2] += (se.values[2] - filteredReading[2]) / 50;

            String reading = String.format("(%f, %f, %f)", se.values[0], se.values[1], se.values[2]);
            String maxReading = String.format("(%f, %f, %f)", maxValue[0], maxValue[1], maxValue[2]);

            //localOutputMax.setText("Max value of ACCELEROMETER: \n" + maxReading + "\n");

            //If there is less than 100 lines keep adding
            if(counter <= 99){
                localTracker[counter][0] = filteredReading[0];
                localTracker[counter][1] = filteredReading[1];
                localTracker[counter][2] = filteredReading[2];
                /*if(counter != 0){
                    acclerationDifference = localTracker[counter][0] - localTracker[counter - 1][0];
                }*/
                currentValue = localTracker[counter][0];
            }
            //If there is 100 lines take away the last line and replace it with the line above
            if (counter > 99){
                for(int i = 1; i <= 99; i++) {
                    for (int j = 0; j < 3; j++) {
                        templine[j] = localTracker[i][j];
                    }
                    for (int j = 0; j < 3; j++){
                        localTracker[i - 1][j] = templine[j];
                    }
                    //Then add new points to the first line
                    if(i == 99){
                        localTracker[99][0] = filteredReading[0];
                        localTracker[99][1] = filteredReading[1];
                        localTracker[99][2] = filteredReading[2];
                        acclerationDifference =  localTracker[99][0] - localTracker[98][0];
                        acclerationDifferenceY = localTracker[99][1] - localTracker[98][1];
                        relativeLocation = localTracker[0][0];
                        currentValue = localTracker[99][0];
                        currentValueY = localTracker[99][1];
                    }
                }
            }
            GestureStateMachine.GestureState state = gestureControl.DetermineGesture(acclerationDifference, currentValue);
            GestureStateMachine.GestureStateY yState = gestureControl.DetermineGestureY(acclerationDifferenceY,currentValueY);
            //Storing the possible values in gameblock form the fsm
            GameLoopTask.GameDirection gestureDetermine = gestureControl.ComboLogic(state, yState);

            if(gestureDetermine != finalGesture.UNDETERMINED){
                localGesture.setText(gestureDetermine.toString());
                //Sets the direction of the gameblock
                //Only changes directions when it is up down left right
                localGameLoop.setDirection(gestureDetermine);
            }
            counter++;
        }
    }
}

