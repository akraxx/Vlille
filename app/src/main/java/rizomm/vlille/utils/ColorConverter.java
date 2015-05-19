package rizomm.vlille.utils;

import android.content.SharedPreferences;
import android.graphics.Color;

import rizomm.vlille.ui.activities.SettingsActivity;

/**
 * Created by Maximilien on 17/05/2015.
 */
public class ColorConverter {

    public static int getColorByNumber(Integer number, SharedPreferences sharedPreferences) {

        int lowLimit = sharedPreferences.getInt(SettingsActivity.SETTINGS_LOW_LIMIT, SettingsActivity.DEFAULT_LOW_LIMIT);
        int middleLimit = sharedPreferences.getInt(SettingsActivity.SETTINGS_MIDDLE_LIMIT, SettingsActivity.DEFAULT_MIDDLE_LIMIT);

        int color;

        if (number <= lowLimit) {
            color = Color.parseColor("#cc0000");
        } else if (number <= middleLimit) {
            color = Color.parseColor("#ff8800");
        } else {
            color = Color.parseColor("#669900");
        }

        return color;
    }
}
