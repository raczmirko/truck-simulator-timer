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
    TextView averagePrecision, deliveryNumber, daysOfUse;
    CardView cardViewAveragePrecision, cardViewDeliveryNumber, cardViewDaysOfUse;
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
    }

    private void initComponents() {
        averagePrecision = findViewById(R.id.textViewOverallPrecisionValue);
        deliveryNumber = findViewById(R.id.textViewDeliveryNumber);
        daysOfUse = findViewById(R.id.textViewDaysOfUse);
        cardViewDeliveryNumber = findViewById(R.id.cardViewDeliveryNumber);
        cardViewDaysOfUse = findViewById(R.id.cardViewDaysOfUse);
        cardViewAveragePrecision = findViewById(R.id.cardViewAveragePrecision);
        //Color all cards with a different background
        CardView[] cardViews = {cardViewDeliveryNumber, cardViewDaysOfUse, cardViewAveragePrecision};
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
