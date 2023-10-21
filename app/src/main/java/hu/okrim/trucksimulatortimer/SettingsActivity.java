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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    RadioButton radioButtonOptimistic, radioButtonRealistic, radioButtonPessimistic,
            radioButtonPlusMinus10, radioButtonPlusMinus20, radioButtonPlusMinus30,
            radioButtonExpectedError5, radioButtonExpectedError10, radioButtonExpectedError20,
            radioButtonExpectedError30, radioButtonMedian, radioButtonAverage;
    Button buttonHelpEstimation, buttonHelpSampling, buttonHelpExpectedError, buttonWipeDB, buttonEstimationOperand;
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
        String expectedErrorPercent = sharedPreferences.getString("expectedErrorPercentText", "none");
        String estimationOperand = sharedPreferences.getString("estimationOperand", "none");
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
        switch (expectedErrorPercent){
            case "5":
                radioButtonExpectedError5.setChecked(true);
                break;
            case "10":
                radioButtonExpectedError10.setChecked(true);
                break;
            case "20":
                radioButtonExpectedError20.setChecked(true);
                break;
            case "30":
                radioButtonExpectedError30.setChecked(true);
                break;
        }
        switch (estimationOperand){
            case "Average":
                radioButtonAverage.setChecked(true);
                break;
            case "Median":
                radioButtonMedian.setChecked(true);
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
                    editor.apply();
                }
            }
        });
        radioButtonRealistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationTactic", "realistic");
                    editor.putFloat("estimationTacticValue", 0.3f);
                    editor.apply();
                }
            }
        });
        radioButtonPessimistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationTactic", "pessimistic");
                    editor.putFloat("estimationTacticValue", 0.5f);
                    editor.apply();
                }
            }
        });
        radioButtonPlusMinus10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus10");
                    editor.putFloat("samplingStrategyValue", 0.1f);
                    editor.apply();
                }
            }
        });
        radioButtonPlusMinus20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus20");
                    editor.putFloat("samplingStrategyValue", 0.2f);
                    editor.apply();
                }
            }
        });
        radioButtonPlusMinus30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("samplingStrategy", "plusMinus30");
                    editor.putFloat("samplingStrategyValue", 0.3f);
                    editor.apply();
                }
            }
        });
        radioButtonExpectedError5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("expectedErrorPercentText", "5");
                    editor.putFloat("expectedErrorPercent", 0.05f);
                    editor.apply();
                }
            }
        });
        radioButtonExpectedError10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("expectedErrorPercentText", "10");
                    editor.putFloat("expectedErrorPercent", 0.1f);
                    editor.apply();
                }
            }
        });
        radioButtonExpectedError20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("expectedErrorPercentText", "20");
                    editor.putFloat("expectedErrorPercent", 0.2f);
                    editor.apply();
                }
            }
        });
        radioButtonExpectedError30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("expectedErrorPercentText", "30");
                    editor.putFloat("expectedErrorPercent", 0.3f);
                    editor.apply();
                }
            }
        });
        radioButtonAverage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationOperand", "Average");
                    editor.apply();
                }
            }
        });
        radioButtonMedian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("estimationOperand", "Median");
                    editor.apply();
                }
            }
        });
        buttonHelpSampling.setOnClickListener(view -> showHelpDialog(R.string.help_sampling));
        buttonHelpEstimation.setOnClickListener(view -> showHelpDialog(R.string.help_estimation));
        buttonHelpExpectedError.setOnClickListener(view -> showHelpDialog(R.string.help_estimation));
        buttonWipeDB.setOnClickListener(view -> showConfirmationDialog(R.string.help_expected_error));
        buttonEstimationOperand.setOnClickListener(view -> showHelpDialog(R.string.help_estimation_operand));
    }

    private void initComoponents() {
        radioButtonOptimistic = findViewById(R.id.radioButtonOptimistic);
        radioButtonRealistic = findViewById(R.id.radioButtonRealistic);
        radioButtonPessimistic = findViewById(R.id.radioButtonPessimistic);
        radioButtonPlusMinus10 = findViewById(R.id.radioButtonSettingsSampling10);
        radioButtonPlusMinus20 = findViewById(R.id.radioButtonSettingsSampling20);
        radioButtonPlusMinus30 = findViewById(R.id.radioButtonSettingsSampling30);
        radioButtonExpectedError5 = findViewById(R.id.radioButtonSettingsExpectedError5);
        radioButtonExpectedError10 = findViewById(R.id.radioButtonSettingsExpectedError10);
        radioButtonExpectedError20 = findViewById(R.id.radioButtonSettingsExpectedError20);
        radioButtonExpectedError30 = findViewById(R.id.radioButtonSettingsExpectedError30);
        radioButtonAverage = findViewById(R.id.radioButtonSettingsAverage);
        radioButtonMedian = findViewById(R.id.radioButtonSettingsMedian);
        buttonHelpSampling = findViewById(R.id.buttonSettingsHelpSampling);
        buttonHelpEstimation = findViewById(R.id.buttonSettingsHelpEstimation);
        buttonHelpExpectedError = findViewById(R.id.buttonSettingsHelpExpectedError);
        buttonWipeDB = findViewById(R.id.buttonWipeDB);
        buttonEstimationOperand = findViewById(R.id.buttonSettingsHelpEstimationOperand);
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
            ToastController.showToastMessage(R.string.toast_delete_successful, getApplicationContext());
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
