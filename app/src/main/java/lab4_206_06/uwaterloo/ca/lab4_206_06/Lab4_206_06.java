package lab4_206_06.uwaterloo.ca.lab4_206_06;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

import lab4_206_06.uwaterloo.ca.lab4_206_06.AccelerometerSensorEventListener;

public class Lab4_206_06 extends AppCompatActivity {

    float[][] motionTracker = new float[100][3];
    float[] maxAccelValues = new float[3];
    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_206_06);


        RelativeLayout layout = (RelativeLayout) findViewById(R.id.label2);

        layout.getLayoutParams().width = Resources.getSystem().getDisplayMetrics().widthPixels;  //gameboard size
        layout.getLayoutParams().height = Resources.getSystem().getDisplayMetrics().widthPixels;

        layout.setBackgroundResource(R.drawable.gameboard);

        //Declare the game loop task
        final GameLoopTask gameLoop = new GameLoopTask(this, layout, getApplicationContext());

        Button button1 = (Button)findViewById(R.id.up);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameLoop.setDirection(GameLoopTask.GameDirection.UP);
            }
        });

        Button button2 = (Button)findViewById(R.id.down);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameLoop.setDirection(GameLoopTask.GameDirection.DOWN);
            }
        });
        Button button3 = (Button)findViewById(R.id.left);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameLoop.setDirection(GameLoopTask.GameDirection.LEFT);
            }
        });

        Button button4 = (Button)findViewById(R.id.right);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameLoop.setDirection(GameLoopTask.GameDirection.RIGHT);
            }
        });

        //Schedules the timer to run every 50ms
        timer.schedule(gameLoop, 0, 50);
        SensorManager sensorManger = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometerSensor = sensorManger.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        TextView gesture = (TextView) findViewById(R.id.Gesture);
        final SensorEventListener accel = new AccelerometerSensorEventListener(gesture, motionTracker, maxAccelValues, gameLoop);
        sensorManger.registerListener(accel, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }
}
