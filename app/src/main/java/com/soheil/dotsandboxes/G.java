package com.soheil.dotsandboxes;

import android.app.Application;
import android.content.Context;

public class G extends Application {
    public static Context context;
    public static int screenWidth;
    public static int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        screenWidth = G.context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = G.context.getResources().getDisplayMetrics().heightPixels;
    }
}
