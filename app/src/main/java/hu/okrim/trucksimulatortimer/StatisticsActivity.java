package hu.okrim.trucksimulatortimer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    DatabaseController databaseController = new DatabaseController(StatisticsActivity.this);
    TextView averagePrecision, deliveryNumber, daysOfUse, averageDrivePerDay, totalDriveTimeSeconds, dateMedian;
    CardView cardViewAveragePrecision, cardViewDeliveryNumber, cardViewDaysOfUse,
            cardViewAverageDrivePerDay, cardViewTotalDriveTime, cardViewDateMedian;
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
        averagePrecision.setText(decimalFormat.format(databaseController.calculateOverallEstimationPrecision() * 100));
        deliveryNumber.setText(String.valueOf(databaseController.countRecords()));
        daysOfUse.setText(String.valueOf(databaseController.countDaysOfUse()));
        averageDrivePerDay.setText(TimeFormatController.createTimeText(databaseController.getAverageDriveTimePerDayInSeconds()));
        totalDriveTimeSeconds.setText(TimeFormatController.createTimeText(databaseController.getTotalDriveTime()));
        dateMedian.setText(databaseController.getMedianOfDriveDate());
    }

    private void initComponents() {
        averagePrecision = findViewById(R.id.textViewOverallPrecisionValue);
        deliveryNumber = findViewById(R.id.textViewDeliveryNumber);
        daysOfUse = findViewById(R.id.textViewDaysOfUse);
        averageDrivePerDay = findViewById(R.id.textViewAverageDrivePerDay);
        totalDriveTimeSeconds = findViewById(R.id.textViewTotalDriveTime);
        dateMedian = findViewById(R.id.textViewDateMedian);

        cardViewDeliveryNumber = findViewById(R.id.cardViewDeliveryNumber);
        cardViewDaysOfUse = findViewById(R.id.cardViewDaysOfUse);
        cardViewAveragePrecision = findViewById(R.id.cardViewAveragePrecision);
        cardViewAverageDrivePerDay = findViewById(R.id.cardViewAverageDrivePerDay);
        cardViewTotalDriveTime  = findViewById(R.id.cardViewTotalDriveTime);
        cardViewDateMedian = findViewById(R.id.cardViewDateMedian);
        //Color all cards with a different background
        CardView[] cardViews = {cardViewDeliveryNumber, cardViewDaysOfUse,
                cardViewAveragePrecision, cardViewAverageDrivePerDay,
                cardViewTotalDriveTime, cardViewDateMedian};
        setCardViewBackgroundColorForAllCards(cardViews);
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
}
