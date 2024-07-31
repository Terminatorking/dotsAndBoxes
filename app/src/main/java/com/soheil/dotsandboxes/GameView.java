package com.soheil.dotsandboxes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("all")
public class GameView extends View {

    private int cols = 4;
    private int rows = 4;
    private int offsetY;
    private int offsetX;
    private int space = 150;
    private int radius = 15;
    private Paint circlePaint;
    private Paint textPaint;
    private Paint linePaint;

    private static class point {
        public int x;
        public int y;

        public point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public GameView(Context context) {
        super(context);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#222222"));
        connect(canvas, 2, 2, 3, 1);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                point point = computePoint(i, j);
                canvas.drawCircle(point.x, point.y, radius, circlePaint);
            }
        }
        debugNaming(canvas);

    }

    private point computePoint(int i, int j) {
        int x = offsetX + (i * space);
        int y = offsetY + ((rows - 1 - j) * space);
        return new point(x, y);
    }

    private void initialize() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(20);
        textPaint.setTextAlign(Paint.Align.CENTER);
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(Color.parseColor("#2222ff"));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);
        int boxWidth = (cols - 1) * space;
        int boxHeight = (rows - 1) * space;
        DisplayMetrics displayMetrics = G.context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }

    private boolean connect(Canvas canvas, int i1, int j1, int i2, int j2) {
        float c = (float) Math.sqrt(Math.pow((i1 - i2), 2) + Math.pow((j1 - j2), 2));
        if (c != 1) {
            return false;
        }
        point p1 = computePoint(i1, j1);
        point p2 = computePoint(i2, j2);
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
        return true;
    }

    private void debugNaming(Canvas canvas) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                String string = i + "," + j;
                point point = computePoint(i, j);
                canvas.drawText(string, point.x, point.y + 50, textPaint);
            }
        }
    }

}
