package hu.okrim.trucksimulatortimer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    static boolean timerIsRunning = false;
    static String estimatedTimeTextBackup = null;
    int remainingSeconds = 0;
    int timeAddedCounter = 0;
    CountDownTimer inspectionTimer;
    Button buttonStartTimer;
    Button buttonStopTimer;
    Button buttonReset;
    Button buttonAddOneMinute;
    EditText editTextTotalHours;
    EditText editTextTotalMinutes;
    EditText editTextTotalFerryDistance;
    Thread timer;
    TextView textViewTimer;
    TextView textViewEstimatedTimeValue;
    TextView textViewETAValue;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
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
        buttonAddOneMinute = findViewById(R.id.buttonAddOneMin);
        editTextTotalHours = findViewById(R.id.editTextTotalHours);
        editTextTotalMinutes = findViewById(R.id.editTextTotalMinutes);
        editTextTotalFerryDistance = findViewById(R.id.editTextTotalFerryDistance);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewEstimatedTimeValue = findViewById(R.id.textViewEstimatedTimeValue);
        textViewETAValue = findViewById(R.id.textViewETAValue);
        editTextTotalFerryDistance.setText("0");
        setListeners();
    }

    private void setListeners(){
        buttonStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTotalHours.getText().length() == 0 ||
                   editTextTotalMinutes.getText().length() == 0 ||
                   editTextTotalFerryDistance.getText().length() == 0){
                    Toast.makeText(
                            getApplicationContext(),
                            "Fill in the inputs before starting the timer!",
                            Toast.LENGTH_LONG
                    ).show();
                }
                else{
                    startTimer(calculateDriveTimeInSeconds());
                    buttonAddOneMinute.setVisibility(View.VISIBLE);
                    buttonStopTimer.setVisibility(View.VISIBLE);
                }
            }
        });
        buttonStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimer();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimer();
                textViewTimer.setText(R.string.timer_default);
                editTextTotalHours.setText(null);
                editTextTotalMinutes.setText(null);
                editTextTotalFerryDistance.setText("0");
                textViewEstimatedTimeValue.setText(null);
                textViewETAValue.setText(null);
            }
        });
        buttonAddOneMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remainingSeconds += 60;
                timeAddedCounter++;
                textViewEstimatedTimeValue.setText(String.format("%s (+%s m)",estimatedTimeTextBackup,timeAddedCounter));
                setETAText();
            }
        });
    }

    private void startTimer(int seconds) {
        if(!timerIsRunning){
            startTimerThread(seconds);
            setEstimatedTimeText();
            setETAText();
        }
    }

    private int calculateDriveTimeInSeconds() {
        int realDriveTimeSeconds = 0;
        if(editTextTotalFerryDistance.getText().toString().equals("0")){
            int inputMinutes = Integer.parseInt(editTextTotalMinutes.getText().toString());
            int inputHours = Integer.parseInt(editTextTotalHours.getText().toString());
            int inputTotalInMinutes = inputHours * 60 + inputMinutes;
            // We first convert the in-game time into a sum of seconds
            // Then we multiply by 3 because 1 in game minute is 3 seconds IRL
            // And our method needs to return seconds
            realDriveTimeSeconds = inputTotalInMinutes * 3;
        }
        return realDriveTimeSeconds;
    }

    private void setEstimatedTimeText(){
        int totalSeconds = calculateDriveTimeInSeconds();
        int hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        int minutes = totalSeconds / 60;
        textViewEstimatedTimeValue.setText(String.format("~ %s hours %s minutes", hours, minutes));
        estimatedTimeTextBackup = String.format("~ %s hours %s minutes", hours, minutes);
    }

    private void setETAText(){
        long currentTimeMillis = System.currentTimeMillis(); // Get current time in milliseconds
        long etaMillis = currentTimeMillis + (remainingSeconds * 1000L); // Add seconds in milliseconds
        String formattedTime = timeFormat.format(new Date(etaMillis));
        textViewETAValue.setText(formattedTime);
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
                        if(remainingSeconds == 0){endTimer();}
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

    public void endTimer(){
        timerIsRunning = false;
        buttonAddOneMinute.setVisibility(View.GONE);
        buttonStopTimer.setVisibility(View.GONE);
        timeAddedCounter = 0;
    }
}