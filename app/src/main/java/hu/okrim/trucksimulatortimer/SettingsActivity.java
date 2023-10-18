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
    RadioButton radioButtonOptimistic, radioButtonRealistic, radioButtonPessimistic,
            radioButtonPlusMinus10, radioButtonPlusMinus20, radioButtonPlusMinus30;
    Button buttonHelpEstimation, buttonHelpSampling, buttonWipeDB;
    DatabaseController databaseController = new DatabaseController(SettingsActivity.this);
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
        String estimationStrategy = sharedPreferences.getString("estimationTactic", "none");
        String samplingStrategy = sharedPreferences.getString("samplingStrategy", "none");
        switch (estimationStrategy) {
            case "optimistic":
                radioButtonOptimistic.setChecked(true);
                break;
            case "realistic":
                radioButtonRealistic.setChecked(true);
                break;
            case "pessimistic":
                radioButtonPessimistic.setChecked(true);
                break;
        }
        switch (samplingStrategy) {
            case "plusMinus10":
                radioButtonPlusMinus10.setChecked(true);
                break;
            case "plusMinus20":
                radioButtonPlusMinus20.setChecked(true);
                break;
            case "plusMinus30":
                radioButtonPlusMinus30.setChecked(true);
                break;
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
        radioButtonPlusMinus10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus10");
                    editor.putFloat("samplingStrategyValue", 0.1f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
        radioButtonPlusMinus20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus20");
                    editor.putFloat("samplingStrategyValue", 0.2f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
        radioButtonPlusMinus30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus30");
                    editor.putFloat("samplingStrategyValue", 0.3f);
                    editor.apply(); // Save the value in SharedPreferences
                }
            }
        });
        buttonHelpSampling.setOnClickListener(view -> showHelpDialog(R.string.help_sampling));
        buttonHelpEstimation.setOnClickListener(view -> showHelpDialog(R.string.help_estimation));
        buttonWipeDB.setOnClickListener(view -> showConfirmationDialog(R.string.confirm_wipe));
    }

    private void initComoponents() {
        radioButtonOptimistic = findViewById(R.id.radioButtonOptimistic);
        radioButtonRealistic = findViewById(R.id.radioButtonRealistic);
        radioButtonPessimistic = findViewById(R.id.radioButtonPessimistic);
        radioButtonPlusMinus10 = findViewById(R.id.radioButtonSettingsSampling10);
        radioButtonPlusMinus20 = findViewById(R.id.radioButtonSettingsSampling20);
        radioButtonPlusMinus30 = findViewById(R.id.radioButtonSettingsSampling30);
        buttonHelpSampling = findViewById(R.id.buttonSettingsHelpSampling);
        buttonHelpEstimation = findViewById(R.id.buttonSettingsHelpEstimation);
        buttonWipeDB = findViewById(R.id.buttonWipeDB);
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
        helpText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        helpText.setText(textReference);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void showConfirmationDialog(int textReference){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.settings_help_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        // References to the EditText views in the dialog
        final TextView helpText = dialogView.findViewById(R.id.textViewSettingsHelpDialog);
        helpText.setTextColor(Color.BLACK);
        helpText.setTextSize(20);
        helpText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        helpText.setText(textReference);

        // Define OK button behavior
        dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            databaseController.wipeDatabase();
            dialog.dismiss();
        });
        // Define OK button behavior
        dialogBuilder.setNegativeButton("No, cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
