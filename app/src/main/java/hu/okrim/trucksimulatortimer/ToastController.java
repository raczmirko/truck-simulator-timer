package hu.okrim.trucksimulatortimer;

import android.content.Context;
import android.widget.Toast;

public class ToastController {
    public static void showToastMessage(int textResource, Context context) {
        Toast.makeText(
                context,
                textResource,
                Toast.LENGTH_LONG
        ).show();
    }
}
