package com.soheil.dotsandboxes.classes;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    public static SharedPreferences.Editor getSharedPreferenceEditor() {
        return G.context.getSharedPreferences("options", MODE_PRIVATE).edit();
    }

    public static SharedPreferences getSharedPreference() {
        return G.context.getSharedPreferences("options", MODE_PRIVATE);
    }

    public static boolean isEnableMusic() {
        return getSharedPreference().getBoolean("enable_music", false);
    }

    public static boolean isEnableSfx() {
        return getSharedPreference().getBoolean("enable_sfx", false);
    }

    public static boolean isEnableHighPerformance() {
        return getSharedPreference().getBoolean("enable_high_performance", false);
    }
}
