package hu.okrim.trucksimulatortimer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
    List<CardView> cards = new ArrayList<>();
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
        cards.add(cardViewDeliveryNumber);
        cards.add(cardViewDaysOfUse);
        cards.add(cardViewAveragePrecision);
        setRandomColorsForCardBackground(cards);
    }

    private void setRandomColorsForCardBackground(List<CardView> cards){
        String[] staticColorArray = {
                "#D79FC7", "#57F287", "#96AED0", "#AA875C", "#1DACD6",
                "#A4C495", "#45B5AA", "#FFCC33", "#FF9B49", "#708090",
                "#1D8489", "#F6D155", "#FDD808", "#A1A556"
        };
        List<String> colorList = new ArrayList<>(Arrays.asList(staticColorArray));
        for(CardView cv: cards){
            int randomIndex = (int)(Math.random() * (staticColorArray.length));
            cv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorList.get(randomIndex))));
            colorList.remove(randomIndex);
        }
    }
}
