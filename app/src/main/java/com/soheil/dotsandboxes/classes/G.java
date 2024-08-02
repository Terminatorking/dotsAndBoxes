package com.soheil.dotsandboxes.classes;

import android.app.Application;
import android.content.Context;
import android.os.Handler;


public class G extends Application {
    public static Context context;
    public static int screenWidth;
    public static int screenHeight;
    public static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        screenWidth = G.context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = G.context.getResources().getDisplayMetrics().heightPixels;
        handler = new Handler();
    }
}
