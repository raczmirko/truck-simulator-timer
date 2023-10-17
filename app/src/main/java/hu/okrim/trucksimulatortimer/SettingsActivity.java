package hu.okrim.trucksimulatortimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    RadioButton radioButtonOptimistic, radioButtonRealistic, radioButtonPessimistic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initComoponents();
        addListeners();
        loadTacticFromSharedPreferences();
    }

    private void loadTacticFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String tactic = sharedPreferences.getString("estimationTactic", "none");
        if(tactic.equals("optimistic")){
            radioButtonOptimistic.setChecked(true);
        }
        else if(tactic.equals("realistic")){
            radioButtonRealistic.setChecked(true);
        }
        else if(tactic.equals("pessimistic")){
            radioButtonPessimistic.setChecked(true);
        }
    }

    private void addListeners() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        radioButtonOptimistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationTactic", "optimistic");
                    editor.putFloat("estimationTacticValue", 0.1f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
        radioButtonRealistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationTactic", "realistic");
                    editor.putFloat("estimationTacticValue", 0.3f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
        radioButtonPessimistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationTactic", "pessimistic");
                    editor.putFloat("estimationTacticValue", 0.5f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
    }

    private void initComoponents() {
        radioButtonOptimistic = findViewById(R.id.radioButtonOptimistic);
        radioButtonRealistic = findViewById(R.id.radioButtonRealistic);
        radioButtonPessimistic = findViewById(R.id.radioButtonPessimistic);
    }
}
