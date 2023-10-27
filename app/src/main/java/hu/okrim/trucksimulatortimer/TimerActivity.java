package hu.okrim.trucksimulatortimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {
    private static final String DEFAULT_ESTIMATION_OPERAND = "Average";
    private static final float DEFAULT_TACTIC_MULTIPLIER = 0.3f;
    private static final float DEFAULT_EXPECTED_ERROR_PERCENTAGE = 0.1f;
    private final double DEFAULT_GAME_MINUTE_IN_REAL_SECONDS = 3;
    TimerButtonState timerButtonState = TimerButtonState.STOPPED;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean timerIsRunning = false;
    String estimatedTimeTextBackup = null;
    final double FERRY_ONE_KM_IN_GAME_MINUTES_SHORT_DISTANCE = 1.71;
    final double FERRY_ONE_KM_IN_GAME_MINUTES_LONG_DISTANCE = 1.248;
    double gameMinuteInRealSeconds;
    int remainingSeconds, passedSeconds, ferrySeconds, totalEstimatedSeconds, timeAddedCounter = 0;
    int defaultTimerTextColor;
    Button buttonStartTimer, buttonStopTimer, buttonReset, buttonAddOneMinute, buttonAddFerryTime,
            buttonCalculate, buttonSubtractFerryTime;
    CardView cardViewTime, cardViewFerry;
    DatabaseController databaseController = new DatabaseController(TimerActivity.this);
    EditText editTextTotalHours, editTextTotalMinutes, editTextTotalFerryDistance;
    SharedPreferences sharedPreferences;
    TextView textViewTimer, textViewEstimatedTimeValue, textViewETAValue;
    Thread timer;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonStartTimer.setOnClickListener(null);
        buttonCalculate.setOnClickListener(null);
        buttonReset.setOnClickListener(null);
        buttonStopTimer.setOnClickListener(null);
        buttonAddFerryTime.setOnClickListener(null);
        buttonSubtractFerryTime.setOnClickListener(null);
        buttonAddOneMinute.setOnClickListener(null);
    }

    private void initComponents() {
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
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
        editTextTotalFerryDistance = findViewById(R.id.editTextPickupHour);
        editTextTotalFerryDistance.setText("0");
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewEstimatedTimeValue = findViewById(R.id.textViewEstimatedTimeValue);
        textViewETAValue = findViewById(R.id.textViewETAValue);
        defaultTimerTextColor = textViewTimer.getCurrentTextColor();
        setListeners();
        loadTimeCompressionFromSharedPreferences();
    }

    private void loadTimeCompressionFromSharedPreferences() {
        gameMinuteInRealSeconds = sharedPreferences.getFloat("timeCompressionOneGameMinuteInSeconds", (float) DEFAULT_GAME_MINUTE_IN_REAL_SECONDS);
    }

    private void setListeners(){
        buttonStartTimer.setOnClickListener(view -> {
            if(timerButtonState == TimerButtonState.STOPPED){
                if(checkIfSomeInputsAreNotFilled()) {
                    ToastController.showToastMessage(R.string.toast_missing_input, getApplicationContext());
                }
                else {
                    if(areAllInputsAreZero()) {
                        ToastController.showToastMessage(R.string.toast_all_inputs_zero, getApplicationContext());
                    }
                    else {
                        changeUIWhenTimerRuns();
                        //Changing button to pause button
                        buttonStartTimer.setText(R.string.pause);
                        remainingSeconds = calculateDriveTimeInSeconds();
                        showDistanceToStartingCompanyDialog();
                    }
                }
            }
            else if(timerButtonState == TimerButtonState.RUNNING){
                timerIsRunning = false;
                setButtonInPausedTimerState();
            }
            else {
                startTimer(remainingSeconds);
                setButtonInRunningTimerState();
            }

        });
        buttonStopTimer.setOnClickListener(view -> {
            endTimer();
            buttonReset.setVisibility(View.VISIBLE);
        });
        buttonReset.setOnClickListener(view -> resetUIWhenTimerStops());
        buttonAddOneMinute.setOnClickListener(view -> {
            remainingSeconds += 60;
            totalEstimatedSeconds += 60;
            timeAddedCounter++;
            textViewEstimatedTimeValue.setText(String.format("%s (+%s m)",estimatedTimeTextBackup,timeAddedCounter));
            setETAText();
        });
        buttonAddFerryTime.setOnClickListener(v -> showFerryPopupDialog());
        buttonCalculate.setOnClickListener(view -> {
            if(checkIfSomeInputsAreNotFilled()){
                ToastController.showToastMessage(R.string.toast_missing_input_calculate, getApplicationContext());
            }
            else {
                if(areAllInputsAreZero()) {
                    ToastController.showToastMessage(R.string.toast_all_inputs_zero, getApplicationContext());
                }
                else{
                    buttonSubtractFerryTime.setEnabled(true);
                    remainingSeconds = calculateDriveTimeInSeconds();
                    totalEstimatedSeconds = remainingSeconds;
                    setEstimatedTimeText(remainingSeconds);
                    setETAText();
                }
            }
        });
        buttonSubtractFerryTime.setOnClickListener(view -> {
            int ferryDistance = Integer.parseInt(editTextTotalFerryDistance.getText().toString());
            remainingSeconds = subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(remainingSeconds,ferryDistance);
            totalEstimatedSeconds = subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(totalEstimatedSeconds,ferryDistance);
            setEstimatedTimeText(remainingSeconds);
            setETAText();
        });
    }

    private boolean areAllInputsAreZero() {
        return editTextTotalHours.getText().toString().equals("0") && 
                editTextTotalMinutes.getText().toString().equals("0");
    }

    private boolean checkIfSomeInputsAreNotFilled() {
         return editTextTotalHours.getText().length() == 0 ||
                editTextTotalMinutes.getText().length() == 0 ||
                editTextTotalFerryDistance.getText().length() == 0;
    }

    private void changeUIWhenTimerRuns() {
        buttonReset.setVisibility(View.GONE);
        buttonAddOneMinute.setVisibility(View.VISIBLE);
        buttonStopTimer.setVisibility(View.VISIBLE);
        buttonAddFerryTime.setVisibility(View.VISIBLE);
        buttonSubtractFerryTime.setEnabled(false);
        buttonCalculate.setEnabled(false);
        buttonCalculate.setVisibility(View.GONE);
        buttonSubtractFerryTime.setVisibility(View.GONE);
        cardViewTime.setVisibility(View.GONE);
        cardViewFerry.setVisibility(View.GONE);
        editTextTotalHours.setEnabled(false);
        editTextTotalMinutes.setEnabled(false);
        editTextTotalFerryDistance.setEnabled(false);
    }

    private void resetUIWhenTimerStops() {
        buttonCalculate.setEnabled(true);
        buttonSubtractFerryTime.setEnabled(true);
        buttonCalculate.setVisibility(View.VISIBLE);
        buttonStartTimer.setVisibility(View.VISIBLE);
        buttonSubtractFerryTime.setEnabled(false);
        buttonCalculate.setEnabled(true);
        buttonSubtractFerryTime.setVisibility(View.VISIBLE);
        cardViewTime.setVisibility(View.VISIBLE);
        cardViewFerry.setVisibility(View.VISIBLE);
        editTextTotalHours.setText(null);
        editTextTotalMinutes.setText(null);
        editTextTotalFerryDistance.setText("0");
        editTextTotalHours.setEnabled(true);
        editTextTotalMinutes.setEnabled(true);
        editTextTotalFerryDistance.setEnabled(true);
        textViewTimer.setText(R.string.timer_default);
        textViewEstimatedTimeValue.setText(null);
        textViewETAValue.setText(null);
        textViewTimer.setTextColor(defaultTimerTextColor);
    }

    private int subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(int subtractFrom, int ferryKilometres){
        double distanceInGameMinutes;
        if(ferryKilometres < 700){
            distanceInGameMinutes = ferryKilometres * FERRY_ONE_KM_IN_GAME_MINUTES_SHORT_DISTANCE;
        }
        else {
            distanceInGameMinutes = ferryKilometres * FERRY_ONE_KM_IN_GAME_MINUTES_LONG_DISTANCE;
        }
        // 1 in-game minute = 3 seconds IRL
        int distanceInRealSeconds = (int)(distanceInGameMinutes * gameMinuteInRealSeconds);
        ferrySeconds = distanceInRealSeconds;
        if(subtractFrom - distanceInRealSeconds > 0){
            subtractFrom -= distanceInRealSeconds;
        }
        else{
            ToastController.showToastMessage(R.string.toast_negativ_value, getApplicationContext());
        }
        return subtractFrom;
    }
    private void subTractFerryTimeFromRemainingSecondsCalculatedFromFerryTime(int ferryMinutes){
        if(remainingSeconds - ferryMinutes * gameMinuteInRealSeconds > 0){
            remainingSeconds -= ferryMinutes * gameMinuteInRealSeconds;
            ferrySeconds += ferryMinutes * gameMinuteInRealSeconds;
        }
        else{
            ToastController.showToastMessage(R.string.toast_negativ_value, getApplicationContext());
        }
    }

    private void startTimer(int seconds) {
        if(!timerIsRunning){
            totalEstimatedSeconds = seconds;
            buttonCalculate.setEnabled(false);
            buttonSubtractFerryTime.setEnabled(false);
            startTimerThread(seconds);
            setEstimatedTimeText(seconds);
            setETAText();
            setButtonInRunningTimerState();
        }
    }

    private int calculateDriveTimeInSeconds() {
        int realDriveTimeSeconds;
        int inputMinutes = Integer.parseInt(editTextTotalMinutes.getText().toString());
        int inputHours = Integer.parseInt(editTextTotalHours.getText().toString());
        int inputTotalInMinutes = inputHours * 60 + inputMinutes;
        // We first convert the in-game time into a sum of seconds
        // Then we multiply by 3 because 1 in game minute is 3 seconds IRL
        // And our method needs to return seconds
        realDriveTimeSeconds = (int)Math.ceil((inputTotalInMinutes * gameMinuteInRealSeconds));
        // Then, if ferry distance was given we subtract the ferry time from the seconds remaining
        if(!editTextTotalFerryDistance.getText().toString().equals("0") &&
                editTextTotalFerryDistance.getText() != null){
            int ferryDistance = Integer.parseInt(editTextTotalFerryDistance.getText().toString());
            realDriveTimeSeconds = subtractFerryTimeFromRemainingSecondsCalculatedFromKilometres(realDriveTimeSeconds,ferryDistance);
        }
        // Multiplying the calculated seconds by the expectedErrorRate due to traffic
        realDriveTimeSeconds = multiplyRealDriveTimeSecondsWithExpectedErrorPercentage(realDriveTimeSeconds);
        // Multiplying by the average error rate calculated from previous drives of similar distance
        realDriveTimeSeconds = multiplyRealDriveTimeSecondsWithAverageDifferencePercentage(realDriveTimeSeconds);
        return realDriveTimeSeconds;
    }

    private int multiplyRealDriveTimeSecondsWithExpectedErrorPercentage(int realDriveTimeSeconds) {
        double errorMultiplier = loadExpectedErrorPercentageFromSharedPreferences();
        return realDriveTimeSeconds + (int)(realDriveTimeSeconds * errorMultiplier);
    }

    double loadExpectedErrorPercentageFromSharedPreferences(){

        return sharedPreferences.getFloat("expectedErrorPercent", DEFAULT_EXPECTED_ERROR_PERCENTAGE);
    }

    double loadTacticMultiplierFromSharedPreferences(){
        return sharedPreferences.getFloat("estimationTacticValue", DEFAULT_TACTIC_MULTIPLIER);
    }

    String loadEstimationOperandFromSharedPreferences(){
        return sharedPreferences.getString("estimationOperand", DEFAULT_ESTIMATION_OPERAND);
    }

    private int multiplyRealDriveTimeSecondsWithAverageDifferencePercentage(int realDriveTimeSeconds) {
        double differencePercentage = databaseController.calculateExtraSecondsByPastDeliveryTimes(realDriveTimeSeconds, ferrySeconds);
        double differencePercentageFromMedian = databaseController.calculateExtraSecondsByPastDeliveryTimesMedian(realDriveTimeSeconds, ferrySeconds);
        double threshold = loadTacticMultiplierFromSharedPreferences();
        String estimationOperand = loadEstimationOperandFromSharedPreferences();
        Log.d("differencePercentageFromMedian", String.valueOf(differencePercentageFromMedian));
        switch(estimationOperand){
            case "Average":
                if(differencePercentage < 0){
                    //If the percentage is smaller than -30 then change it to -30
                    if(differencePercentage < (threshold * -1)){differencePercentage = threshold * -1;}
                    realDriveTimeSeconds += (int)Math.ceil((realDriveTimeSeconds * differencePercentage)*(-1));
                }
                else {
                    //If the percentage is more than 30 then change it to 30
                    if(differencePercentage > threshold){differencePercentage = threshold;}
                    realDriveTimeSeconds -= (int)Math.ceil(realDriveTimeSeconds * differencePercentage);
                }
                break;

            case "Median":
                if(differencePercentageFromMedian < 0){
                    //If the percentage is smaller than -30 then change it to -30
                    if(differencePercentageFromMedian < (threshold * -1)){differencePercentageFromMedian = threshold * -1;}
                    realDriveTimeSeconds += (int)Math.ceil((realDriveTimeSeconds * differencePercentageFromMedian)*(-1));
                }
                else {
                    //If the percentage is more than 30 then change it to 30
                    if(differencePercentageFromMedian > threshold){differencePercentageFromMedian = threshold;}
                    realDriveTimeSeconds -= (int)Math.ceil(realDriveTimeSeconds * differencePercentageFromMedian);
                }
                break;
        }

        return realDriveTimeSeconds;
    }

    private void setEstimatedTimeText(int totalSeconds){
        int hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        int minutes = (int)Math.ceil(totalSeconds / 60.0);
        textViewEstimatedTimeValue.setText(String.format("~ %s h %s min", hours, minutes));
        estimatedTimeTextBackup = String.format("~ %s h %s min", hours, minutes);
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
                        passedSeconds++;
                        runOnUiThread(() -> {
                            // Updating timer text on UI thread
                            if(remainingSeconds < 0){textViewTimer.setTextColor(Color.RED);}
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
        buttonStartTimer.setVisibility(View.GONE);
        setButtonInStoppedTimerState();
        saveDeliveryToDatabase();
        ferrySeconds = 0;
        passedSeconds = 0;
        timeAddedCounter = 0;
    }
    public void showFerryPopupDialog(){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TimerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ferry_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final EditText editTextHour = dialogView.findViewById(R.id.editTextDialogFerryHour);
        final EditText editTextMinute = dialogView.findViewById(R.id.editTextDialogFerryMinute);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            //Initialize the numbers to 0 if they are not filled out
            String textHour = editTextHour.getText().length() != 0 ? editTextHour.getText().toString() : "0";
            String textMinute = editTextMinute.getText().length() != 0 ? editTextMinute.getText().toString() : "0";
            int ferryTotalMinutes = Integer.parseInt(textHour) * 60 + Integer.parseInt(textMinute);
            subTractFerryTimeFromRemainingSecondsCalculatedFromFerryTime(ferryTotalMinutes);
        });

        // Define Cancel button behavior
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle the Cancel button click (if needed)
            dialog.dismiss();
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    public void showDistanceToStartingCompanyDialog(){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TimerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.load_pickup_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final EditText editTextHour = dialogView.findViewById(R.id.editTextPickupHour);
        final EditText editTextMinute = dialogView.findViewById(R.id.editTextPickupMinute);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            //Initialize the numbers to 0 if they are not filled out
            String textHour = editTextHour.getText().length() != 0 ? editTextHour.getText().toString() : "0";
            String textMinute = editTextMinute.getText().length() != 0 ? editTextMinute.getText().toString() : "0";
            int pickupInMinutes = Integer.parseInt(textHour) * 60 + Integer.parseInt(textMinute);
            remainingSeconds += pickupInMinutes * gameMinuteInRealSeconds;
            startTimer(remainingSeconds);
        });
        dialogBuilder.setNegativeButton("NONE", (dialog, which) -> {
            dialog.dismiss();
            startTimer(remainingSeconds);
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void saveDeliveryToDatabase(){
        String date = SDF.format(new Date());
        DataEntryModel dataEntryModel = new DataEntryModel(totalEstimatedSeconds, passedSeconds, date, ferrySeconds);
        databaseController.addDelivery(dataEntryModel);
    }
}