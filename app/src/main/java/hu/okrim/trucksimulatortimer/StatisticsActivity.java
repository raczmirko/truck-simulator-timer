package hu.okrim.trucksimulatortimer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    Button buttonAveragePrecisionHelp;
    DatabaseController databaseController = new DatabaseController(StatisticsActivity.this);
    TextView averageError, deliveryNumber, daysOfUse, averageDrivePerDay, totalDriveTimeSeconds, dateMedian,
            precisionMedian;
    CardView cardViewAverageError, cardViewDeliveryNumber, cardViewDaysOfUse,
            cardViewAverageDrivePerDay, cardViewTotalDriveTime, cardViewDateMedian, cardViewPrecisionMedian;
    List<List<View>> cardsAndText = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initComponents();
        setStatistics();
    }

    private void setStatistics() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        averageError.setText(decimalFormat.format(databaseController.calculateOverallEstimationPrecision() * 100).concat("%"));
        deliveryNumber.setText(String.valueOf(databaseController.countRecords()));
        daysOfUse.setText(String.valueOf(databaseController.countDaysOfUse()));
        averageDrivePerDay.setText(TimeFormatController.createTimeText(databaseController.getAverageDriveTimePerDayInSeconds()));
        totalDriveTimeSeconds.setText(TimeFormatController.createTimeText(databaseController.getTotalDriveTime()));
        dateMedian.setText(databaseController.getMedianOfDriveDate());
        precisionMedian.setText(decimalFormat.format(databaseController.calculateOverallEstimationPrecisionByMedian() * 100).concat("%"));
    }

    private void initComponents() {
        averageError = findViewById(R.id.textViewOverallPrecisionValue);
        deliveryNumber = findViewById(R.id.textViewDeliveryNumber);
        daysOfUse = findViewById(R.id.textViewDaysOfUse);
        averageDrivePerDay = findViewById(R.id.textViewAverageDrivePerDay);
        totalDriveTimeSeconds = findViewById(R.id.textViewTotalDriveTime);
        dateMedian = findViewById(R.id.textViewDateMedian);
        precisionMedian = findViewById(R.id.textViewPrecisionMedian);
        buttonAveragePrecisionHelp = findViewById(R.id.buttonAveragePrecisionHelp);
        setListeners();

        cardViewDeliveryNumber = findViewById(R.id.cardViewDeliveryNumber);
        cardViewDaysOfUse = findViewById(R.id.cardViewDaysOfUse);
        cardViewAverageError = findViewById(R.id.cardViewAveragePrecision);
        cardViewAverageDrivePerDay = findViewById(R.id.cardViewAverageDrivePerDay);
        cardViewTotalDriveTime  = findViewById(R.id.cardViewTotalDriveTime);
        cardViewDateMedian = findViewById(R.id.cardViewDateMedian);
        cardViewPrecisionMedian = findViewById(R.id.cardViewPrecisionMedian);
        //Color all cards with a different background
        CardView[] cardViews = {cardViewDeliveryNumber, cardViewDaysOfUse,
                cardViewAverageError, cardViewAverageDrivePerDay,
                cardViewTotalDriveTime, cardViewDateMedian, cardViewPrecisionMedian};
        setCardViewBackgroundColorForAllCards(cardViews);
    }

    private void setListeners() {
        buttonAveragePrecisionHelp.setOnClickListener(view -> showHelpDialog(R.string.help_statistics_precision));
    }

    public void setCardViewBackgroundColorForAllCards(CardView[] list){
        //For each card we extract the Card and the two TextView elements
        //Then we pass it as a list to the color formatter method
        //To set the background color and text color if contrast is too low
        for (CardView cardView : list) {
            List<View> cardAndContentList = new ArrayList<>();
            cardAndContentList.add(cardView);
            LinearLayout cardLayout = (LinearLayout)cardView.getChildAt(0);
            cardAndContentList.add(cardLayout.getChildAt(0));
            cardAndContentList.add(cardLayout.getChildAt(1));
            cardsAndText.add(cardAndContentList);
        }
        ColorController.setRandomColorsForCardBackground(cardsAndText);
    }

    public void showHelpDialog(int textReference){
        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StatisticsActivity.this);
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
}
