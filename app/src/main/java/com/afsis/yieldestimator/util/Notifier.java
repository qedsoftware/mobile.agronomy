package com.afsis.yieldestimator.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Notifier {

    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        // toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
