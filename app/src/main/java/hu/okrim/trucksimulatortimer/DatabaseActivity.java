package hu.okrim.trucksimulatortimer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class DatabaseActivity extends AppCompatActivity {

    DatabaseController databaseController = new DatabaseController(DatabaseActivity.this);
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        linearLayout = findViewById(R.id.linearLayoutDatabase);
        loadRecordsFromDatabaseToLayout();
    }

    private void loadRecordsFromDatabaseToLayout() {
        List<DataFetchModel> records = databaseController.getAllRecords();

        for (DataFetchModel record : records) {
            CardView cardView = new CardView(this);
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 20);
            cardView.setLayoutParams(layoutParams);
            cardView.setCardBackgroundColor(Color.argb(150, 43, 212, 184));

            // Create a LinearLayout to hold TextView and Button
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(20, 20, 20, 20);


            // Create a TextView to display record data
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(createFormattedTextFromDataFetchModel(record));
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);

            // Create a textView title
            TextView textViewTitle = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textViewTitle.setText("ID: " + record.getId());
            textViewTitle.setTypeface(Typeface.MONOSPACE);
            textViewTitle.setTextColor(Color.WHITE);
            textViewTitle.setTextSize(20);
            textViewTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Create a red divider
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    10
            ));
            divider.setBackgroundColor(Color.parseColor("#D42B47"));

            // Create a Button
            Button deleteButton = new Button(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            deleteButton.setBackgroundColor(Color.parseColor("#D42B47"));
            deleteButton.setText(R.string.delete);
            deleteButton.setTextColor(Color.WHITE);

            // Define a click listener for the delete button
            deleteButton.setOnClickListener(view -> {
                // Implement the logic to delete the record from the database
                showConfirmationDialog(record, R.string.confirm_delete, cardView);
            });

            // Add the TextView and Delete Button to the CardView's layout
            cardLayout.addView(textViewTitle);
            cardLayout.addView(divider);
            cardLayout.addView(textView);
            cardLayout.addView(deleteButton);

            // Add the cardLayout to the CardView
            cardView.addView(cardLayout);

            // Add the CardView to the main LinearLayout
            linearLayout.addView(cardView);
        }
    }

    private String createFormattedTextFromDataFetchModel(DataFetchModel model) {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedErrorRate = decimalFormat.format((1 - model.getDifferencePercentageOfTotalTime()) * 100);
        stringBuilder.append(String.format(Locale.US, "Estimated time: %s \n", TimeFormatController.createTimeText(model.getEstimatedTimeSeconds())));
        stringBuilder.append(String.format(Locale.US, "Actual time: %s \n", TimeFormatController.createTimeText(model.getActualTimeSeconds())));
        stringBuilder.append(String.format(Locale.US, "Error rate: %s%% \n", formattedErrorRate));
        stringBuilder.append(String.format(Locale.US, "Date: %s \n", model.getDateString()));
        return stringBuilder.toString();
    }

    public void showConfirmationDialog(DataFetchModel record, int textReference, CardView cardView){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DatabaseActivity.this);
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
            databaseController.deleteDelivery(record.getId());
            dialog.dismiss();
            ToastController.showToastMessage(R.string.toast_delete_successful, getApplicationContext());
            //When deleting we hide the deleted card since it is not worth to load everything
            //From the DB again just to make the card disappear
            cardView.setVisibility(View.GONE);
        });
        // Define OK button behavior
        dialogBuilder.setNegativeButton("No, cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
