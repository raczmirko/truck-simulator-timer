package hu.okrim.trucksimulatortimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    RadioButton radioButtonOptimistic, radioButtonRealistic, radioButtonPessimistic;
    Button buttonHelpEstimation, buttonHelpSampling;
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
        buttonHelpSampling.setOnClickListener(view -> showHelpDialog(R.string.help_sampling));
        buttonHelpEstimation.setOnClickListener(view -> showHelpDialog(R.string.help_estimation));
    }

    private void initComoponents() {
        radioButtonOptimistic = findViewById(R.id.radioButtonOptimistic);
        radioButtonRealistic = findViewById(R.id.radioButtonRealistic);
        radioButtonPessimistic = findViewById(R.id.radioButtonPessimistic);
        buttonHelpSampling = findViewById(R.id.buttonSettingsHelpSampling);
        buttonHelpEstimation = findViewById(R.id.buttonSettingsHelpEstimation);
    }

    public void showHelpDialog(int textReference){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.settings_help_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final TextView helpText = dialogView.findViewById(R.id.textViewSettingsHelpDialog);
        helpText.setTextColor(Color.BLACK);
        helpText.setTextSize(20);
        helpText.setText(textReference);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
