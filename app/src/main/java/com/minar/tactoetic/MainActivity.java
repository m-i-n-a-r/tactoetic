package com.minar.tactoetic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the shared preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sp.getString("theme_color", "system");
        String accent = sp.getString("accent_color", "blue");

        // Set the base theme and the accent
        if (theme.equals("system"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (theme.equals("dark"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (theme.equals("light"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (accent.equals("green")) setTheme(R.style.AppTheme_green);
        if (accent.equals("orange")) setTheme(R.style.AppTheme_orange);
        if (accent.equals("yellow")) setTheme(R.style.AppTheme_yellow);
        if (accent.equals("teal")) setTheme(R.style.AppTheme_teal);
        if (accent.equals("violet")) setTheme(R.style.AppTheme_violet);
        if (accent.equals("pink")) setTheme(R.style.AppTheme_pink);
        if (accent.equals("lightBlue")) setTheme(R.style.AppTheme_lightBlue);
        if (accent.equals("red")) setTheme(R.style.AppTheme_red);
        if (accent.equals("lime")) setTheme(R.style.AppTheme_lime);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Some utility functions, used from every fragment connected to this activity

    // Simply vibrate for a short time
    public void vibrate() {
        SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        if (sp.getBoolean("vibration", true))
            // Vibrate if the vibration in options is set to on
            // noinspection ConstantConditions
            vib.vibrate(30);
    }

    // Programmatically get the accent color to use it in color transitions
    public int getAccent(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }
}
