package com.example.sudokumaster.SettingElement;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationHelper {
    Context context;
    public VibrationHelper(Context context){
        this.context = context;
    }

    public void vibrate(long milliseconds) {
        Vibrator vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE);

            vibrator.vibrate(vibrationEffect);
        } else if (vibrator != null) {
            vibrator.vibrate(milliseconds);
        }
    }
}
