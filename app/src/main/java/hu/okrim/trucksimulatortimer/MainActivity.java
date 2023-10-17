package hu.okrim.trucksimulatortimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button buttonTimer, buttonSettings, buttonStatistics, buttonDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initComponents() {
        buttonTimer = findViewById(R.id.buttonMenuTimer);
        buttonSettings = findViewById(R.id.buttonMenuSettings);
        buttonStatistics = findViewById(R.id.buttonMenuStatistics);
        buttonDatabase = findViewById(R.id.buttonMenuDatabase);
        buttonTimer.setOnClickListener(this::loadSelectedPage);
        buttonSettings.setOnClickListener(this::loadSelectedPage);
        buttonStatistics.setOnClickListener(this::loadSelectedPage);
        buttonDatabase.setOnClickListener(this::loadSelectedPage);
    }

    public void loadSelectedPage(android.view.View source) {
        Class classToLoad = null;
        Intent intent;

        if (source == buttonTimer) {
            classToLoad = TimerActivity.class;
        } else if (source == buttonSettings) {
            classToLoad = TimerActivity.class;
        } else if (source == buttonStatistics) {
            classToLoad = TimerActivity.class;
        } else if (source == buttonDatabase) {
            classToLoad = TimerActivity.class;
        }

        //Defining which class to load in the new intent
        intent = new Intent(this, classToLoad);
        try {
            startActivity(intent);
        } catch (NullPointerException NPE) {
            Log.d("ActivityError", NPE.getMessage());
        }
    }

}