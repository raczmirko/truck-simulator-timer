package hu.okrim.trucksimulatortimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    static boolean timerIsRunning = false;
    int remainingSeconds = 0;
    CountDownTimer inspectionTimer;
    Button buttonStartTimer;
    Button buttonStopTimer;
    Button buttonReset;
    EditText editTextTotalDistance;
    EditText editTextTotalFerryDistance;
    Thread timer;
    TextView textViewTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        buttonStartTimer = findViewById(R.id.buttonStartTimer);
        buttonStopTimer = findViewById(R.id.buttonStopTimer);
        buttonReset = findViewById(R.id.buttonReset);
        editTextTotalDistance = findViewById(R.id.editTextTotalDistance);
        editTextTotalFerryDistance = findViewById(R.id.editTextTotalFerryDistance);
        textViewTimer = findViewById(R.id.textViewTimer);
        setListeners();
    }

    private void setListeners(){
        buttonStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTotalDistance.getText().length() == 0 ||
                   editTextTotalFerryDistance.getText().length() == 0){
                    Toast.makeText(
                            getApplicationContext(),
                            "Fill in the distances before starting the timer!",
                            Toast.LENGTH_LONG
                    ).show();
                }
                else{
                    startTimer(calculateDriveTime());
                }
            }
        });
        buttonStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void startTimer(int seconds) {
        if(!timerIsRunning){
            startTimerThread(seconds);
        }
    }

    private int calculateDriveTime() {
        return Integer.parseInt(editTextTotalDistance.getText().toString());
    }

    public void startTimerThread(int seconds){
        timerIsRunning = true;
        remainingSeconds = seconds;
        textViewTimer.setText(TimeFormatController.createTimeText(remainingSeconds));
        timer = new Thread(){
            @Override
            public void run() {
                while(timerIsRunning){
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Another if to only adjust time on screen if during the 1000ms sleep time
                    //the timer was not stopped. Otherwise stop incrementing the time.
                    if(timerIsRunning){
                        //Casting to int as the maximum time the app can track is 5_999_000 millis
                        //Which is 99 hours 59 minutes 59 seconds
                        //Since the time format is 00:00:00 (it's not really useful to track more...)
                        //An int can store values up to 2 billion so maximum 356 million fits
                        remainingSeconds--;
                        if(remainingSeconds == 0){timerIsRunning = false;}
                        runOnUiThread(() -> {
                            // Updating timer text on UI thread
                            textViewTimer.setText(TimeFormatController.createTimeText(remainingSeconds));
                        });
                    }
                }
            }
        };
        if(!timer.isAlive()){
            timer.start();
        }
    }
}