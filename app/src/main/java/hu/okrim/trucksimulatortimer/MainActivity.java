package hu.okrim.trucksimulatortimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TimerButtonState timerButtonState = TimerButtonState.STOPPED;
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean timerIsRunning = false;
    String estimatedTimeTextBackup = null;
    final double ferryOneKmInGameMinutesShortDistance = 1.71;
    final double ferryOneKmInGameMinutesLongDistance = 1.25;
    int remainingSeconds = 0;
    int timeAddedCounter = 0;
    CountDownTimer inspectionTimer;
    Button buttonStartTimer;
    Button buttonStopTimer;
    Button buttonReset;
    Button buttonAddOneMinute;
    Button buttonAddFerryTime;
    Button buttonCalculate;
    Button buttonSubtractFerryTime;
    CardView cardViewTime;
    CardView cardViewFerry;
    EditText editTextTotalHours;
    EditText editTextTotalMinutes;
    EditText editTextTotalFerryDistance;
    EditText editTextDialogFerryHour;
    EditText editTextDialogFerryMinute;
    Thread timer;
    TextView textViewTimer;
    TextView textViewEstimatedTimeValue;
    TextView textViewETAValue;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rework);
        initComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initComponents() {
        buttonStartTimer = findViewById(R.id.buttonStartTimer);
        buttonStopTimer = findViewById(R.id.buttonStopTimer);
        buttonReset = findViewById(R.id.buttonReset);
        buttonAddOneMinute = findViewById(R.id.buttonAddOneMin);
        buttonAddFerryTime = findViewById(R.id.buttonAddFerryTime);
        buttonCalculate  = findViewById(R.id.buttonCalculate);
        buttonSubtractFerryTime = findViewById(R.id.buttonSubtractFerryTime);
        cardViewTime = findViewById(R.id.cardTotalTravelTime);
        cardViewFerry = findViewById(R.id.cardTotalFerryDistance);
        editTextTotalHours = findViewById(R.id.editTextTotalHours);
        editTextTotalMinutes = findViewById(R.id.editTextTotalMinutes);
        editTextTotalFerryDistance = findViewById(R.id.editTextTPickupHour);
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
                if(timerButtonState == TimerButtonState.STOPPED){
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
                        buttonAddOneMinute.setVisibility(View.VISIBLE);
                        buttonStopTimer.setVisibility(View.VISIBLE);
                        buttonAddFerryTime.setVisibility(View.VISIBLE);
                        editTextTotalHours.setEnabled(false);
                        editTextTotalMinutes.setEnabled(false);
                        editTextTotalFerryDistance.setEnabled(false);
                        buttonSubtractFerryTime.setEnabled(false);
                        buttonCalculate.setEnabled(false);
                        buttonCalculate.setVisibility(View.GONE);
                        buttonSubtractFerryTime.setVisibility(View.GONE);
                        cardViewTime.setVisibility(View.GONE);
                        cardViewFerry.setVisibility(View.GONE);
                        //Changing button to pause button
                        buttonStartTimer.setText(R.string.pause);
                        remainingSeconds = calculateDriveTimeInSeconds();
                        showDistanceToStartingCompanyDialog();
                    }
                }
                else if(timerButtonState == TimerButtonState.RUNNING){
                    timerIsRunning = false;
                    setButtonInPausedTimerState();
                }
                else {
                    setETAText();
                    startTimer(remainingSeconds);
                    setButtonInRunningTimerState();
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
                resetUI();
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
        buttonAddFerryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFerryPopupDialog();
            }
        });
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTotalHours.getText().length() == 0 ||
                        editTextTotalMinutes.getText().length() == 0 ||
                        editTextTotalFerryDistance.getText().length() == 0){
                    Toast.makeText(
                            getApplicationContext(),
                            "Fill in the inputs before calculating!",
                            Toast.LENGTH_LONG
                    ).show();
                }
                else {
                    buttonSubtractFerryTime.setEnabled(true);
                    remainingSeconds = calculateDriveTimeInSeconds();
                    int ferryDistance = Integer.parseInt(editTextTotalFerryDistance.getText().toString());
                    subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(ferryDistance);
                    setEstimatedTimeText(remainingSeconds);
                    setETAText();
                }
            }
        });
        buttonSubtractFerryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ferryDistance = Integer.parseInt(editTextTotalFerryDistance.getText().toString());
                subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(ferryDistance);
                setEstimatedTimeText(remainingSeconds);
                setETAText();
            }
        });
    }

    private void resetUI() {
        textViewTimer.setText(R.string.timer_default);
        editTextTotalHours.setText(null);
        editTextTotalMinutes.setText(null);
        editTextTotalFerryDistance.setText("0");
        textViewEstimatedTimeValue.setText(null);
        textViewETAValue.setText(null);
        editTextTotalHours.setEnabled(true);
        editTextTotalMinutes.setEnabled(true);
        editTextTotalFerryDistance.setEnabled(true);
        buttonSubtractFerryTime.setEnabled(false);
        buttonCalculate.setEnabled(true);
        cardViewTime.setVisibility(View.VISIBLE);
        cardViewFerry.setVisibility(View.VISIBLE);
        buttonCalculate.setEnabled(true);
        buttonSubtractFerryTime.setEnabled(true);
        buttonCalculate.setVisibility(View.VISIBLE);
        buttonSubtractFerryTime.setVisibility(View.VISIBLE);
    }

    private void subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(int ferryKilometres){
        double distanceInGameMinutes = 0;
        if(ferryKilometres < 700){
            distanceInGameMinutes = ferryKilometres * ferryOneKmInGameMinutesShortDistance;
        }
        else {
            distanceInGameMinutes = ferryKilometres * ferryOneKmInGameMinutesLongDistance;
        }
        // 1 in-game minute = 3 seconds IRL
        int distanceInRealSeconds = (int)(distanceInGameMinutes * 3);

        if(remainingSeconds - distanceInRealSeconds > 0){
            remainingSeconds -= distanceInRealSeconds;
        }
        else{
            Toast.makeText(
                    getApplicationContext(),
                    "This subtraction would result in a negative timer value!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    private void subTractFerryTimeFromRemainingSecondsCalculatedFromFerryTime(int ferryMinutes){
        if(remainingSeconds - ferryMinutes * 3 > 0){
            remainingSeconds -= ferryMinutes * 3;
        }
        else{
            Toast.makeText(
                    getApplicationContext(),
                    "This subtraction would result in a negative timer value!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void startTimer(int seconds) {
        if(!timerIsRunning){
            buttonCalculate.setEnabled(false);
            buttonSubtractFerryTime.setEnabled(false);
            startTimerThread(seconds);
            setEstimatedTimeText(seconds);
            setETAText();
            setButtonInRunningTimerState();
        }
    }

    private int calculateDriveTimeInSeconds() {
        int realDriveTimeSeconds = 0;
        int inputMinutes = Integer.parseInt(editTextTotalMinutes.getText().toString());
        int inputHours = Integer.parseInt(editTextTotalHours.getText().toString());
        int inputTotalInMinutes = inputHours * 60 + inputMinutes;
        // We first convert the in-game time into a sum of seconds
        // Then we multiply by 3 because 1 in game minute is 3 seconds IRL
        // And our method needs to return seconds
        realDriveTimeSeconds = inputTotalInMinutes * 3;
        if(!editTextTotalFerryDistance.getText().toString().equals("0")){
            int ferryDistance = Integer.parseInt(editTextTotalFerryDistance.getText().toString());
            subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(ferryDistance);
        }
        return realDriveTimeSeconds;
    }

    private void setEstimatedTimeText(int totalSeconds){
        int hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        Log.d("totalSeconds", String.valueOf(totalSeconds));
        int minutes = (int)Math.ceil(totalSeconds / 60.0);
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

    public void setButtonInStoppedTimerState(){
        buttonStartTimer.setText(R.string.timer_start);
        buttonStartTimer.setBackgroundColor(Color.parseColor("#ff669900"));
        timerButtonState = TimerButtonState.STOPPED;
    }

    public void setButtonInRunningTimerState(){
        buttonStartTimer.setText(R.string.pause);
        buttonStartTimer.setBackgroundColor(Color.parseColor("#ffffbb33"));
        timerButtonState = TimerButtonState.RUNNING;
    }

    public void setButtonInPausedTimerState(){
        buttonStartTimer.setText(R.string.resume);
        buttonStartTimer.setBackgroundColor(Color.parseColor("#ffff8800"));
        timerButtonState = TimerButtonState.PAUSED;
    }

    public void endTimer(){
        timerIsRunning = false;
        buttonAddOneMinute.setVisibility(View.GONE);
        buttonStopTimer.setVisibility(View.GONE);
        buttonAddFerryTime.setVisibility(View.GONE);
        timeAddedCounter = 0;
        setButtonInStoppedTimerState();
    }
    public void showFerryPopupDialog(){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ferry_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final EditText editTextHour = dialogView.findViewById(R.id.editTextTPickupHour);
        final EditText editTextMinute = dialogView.findViewById(R.id.editTextPickupMinute);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textHour = editTextHour.getText().toString();
                String textMinute = editTextMinute.getText().toString();
                int ferryTotalMinutes = Integer.parseInt(textHour) * 60 + Integer.parseInt(textMinute);
                subTractFerryTimeFromRemainingSecondsCalculatedFromFerryTime(ferryTotalMinutes);
            }
        });

        // Define Cancel button behavior
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel button click (if needed)
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    public void showDistanceToStartingCompanyDialog(){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.load_pickup_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final EditText editTextHour = dialogView.findViewById(R.id.editTextTPickupHour);
        final EditText editTextMinute = dialogView.findViewById(R.id.editTextPickupMinute);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textHour = editTextHour.getText().toString();
                String textMinute = editTextMinute.getText().toString();
                int pickupInMinutes = Integer.parseInt(textHour) * 60 + Integer.parseInt(textMinute);
                remainingSeconds += pickupInMinutes * 3;
                startTimer(remainingSeconds);
            }
        });

        // Define Cancel button behavior
        dialogBuilder.setNegativeButton("NONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel button click (if needed)
                dialog.dismiss();
                startTimer(calculateDriveTimeInSeconds());
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}