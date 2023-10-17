package hu.okrim.trucksimulatortimer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button buttonTimer, buttonSettings, buttonStatistics, buttonDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonTimer.setOnClickListener(null);
        buttonSettings.setOnClickListener(null);
        buttonStatistics.setOnClickListener(null);
        buttonDatabase.setOnClickListener(null);
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

    public void loadSelectedPage(View source) {
        Class<?> classToLoad = null;
        Intent intent = null;

        if (source == buttonTimer) {
            classToLoad = TimerActivity.class;
        } else if (source == buttonSettings) {
            classToLoad = SettingsActivity.class;
        } else if (source == buttonStatistics) {
            classToLoad = StatisticsActivity.class;
        } else if (source == buttonDatabase) {
            classToLoad = DatabaseActivity.class;
        }

        if (classToLoad != null) {
            intent = new Intent(this, classToLoad);
        } else {
            Log.e("ActivityError", "Invalid source for new intent");
            // Optionally, you can show a Toast or perform some other error handling here.
        }

        if (intent != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("ActivityError", "Activity not found: " + e.getMessage());
                // Optionally, you can show a Toast or perform some other error handling here.
            }
        }
    }


}