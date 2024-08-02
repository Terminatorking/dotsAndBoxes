package com.soheil.dotsandboxes.classes;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.File;


public class G extends Application {
    public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String HOME_DIR = SDCARD + "/uncox";
    public static final String APP_DIR = HOME_DIR + "/dot_n_boxes";
    public static boolean hasWriteAccess;
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

    public static void createDirectory() {
        if (hasWriteAccess) {
            File file = new File(APP_DIR);
            file.mkdirs();
        }

    }
}
