package hu.okrim.trucksimulatortimer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorController {
    static double minimumContrastRatio = 6.5; // WCAG recommended minimum is 4.5
    public static double getContrastRatio(int color1, int color2) {
        double luminance1 = getLuminance(color1);
        double luminance2 = getLuminance(color2);

        double brighter = Math.max(luminance1, luminance2);
        double darker = Math.min(luminance1, luminance2);

        return (brighter + 0.05) / (darker + 0.05);
    }

    private static double getLuminance(int color) {
        double red = Color.red(color) / 255.0;
        double green = Color.green(color) / 255.0;
        double blue = Color.blue(color) / 255.0;

        red = red <= 0.03928 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
        green = green <= 0.03928 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
        blue = blue <= 0.03928 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    public static void setRandomColorsForCardBackground(List<List<View>> cardList){
        String[] staticColorArray = {
                "#D79FC7", "#57F287", "#96AED0", "#AA875C", "#1DACD6",
                "#A4C495", "#45B5AA", "#FFCC33", "#FF9B49", "#708090",
                "#1D8489", "#F6D155", "#FDD808", "#A1A556"
        };
        List<String> colorList = new ArrayList<>(Arrays.asList(staticColorArray));
        for(List<View> card: cardList){
            int randomIndex = (int)(Math.random() * (colorList.size()));
            Log.d("randomIndex", String.valueOf(randomIndex));
            CardView cardView = (CardView)card.get(0);
            cardView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorList.get(randomIndex))));
            int color1 = Color.parseColor(colorList.get(randomIndex));
            int color2 = Color.BLACK;
            double contrastRatio = getContrastRatio(color1,color2);
            Log.d("contrast", String.valueOf(contrastRatio));
            if (contrastRatio < minimumContrastRatio) {
                TextView textView1 = (TextView) card.get(1);
                TextView textView2 = (TextView) card.get(2);
                Log.d("textView1", String.valueOf(textView1));
                textView1.setTextColor(Color.WHITE);
                textView2.setTextColor(Color.WHITE);
            }
            colorList.remove(randomIndex);
        }
    }
}
