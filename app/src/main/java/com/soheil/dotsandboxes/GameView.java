package com.soheil.dotsandboxes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;


@SuppressWarnings("all")
public class GameView extends View {
    private boolean isSide1 = true;
    private int cols = 4;
    private int rows = 4;
    private int offsetY;
    private int offsetX;
    private int space = 150;
    private int radius = 15;
    private Paint circlePaint;
    private Paint textPaint;
    private Paint linePaint;
    private float touchX;
    private float touchY;
    private final ArrayList<Line> lines = new ArrayList<>();

    private static class Position {
        public int x;
        public int y;


        public Position(int x, int y) {
            this.x = x;
            this.y = y;

        }
    }

    private static class Diff {
        public int i;
        public int j;
        public Float diff;

        public Diff(int i, int j, Float diff) {
            this.i = i;
            this.j = j;
            this.diff = diff;
        }
    }

    private static class Line {
        public int i1;
        public int j1;
        public int i2;
        public int j2;
        public int playerIndex;


        public Line(int i1, int j1, int i2, int j2, int playerIndex) {
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.playerIndex = playerIndex;
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
        for (Line line : lines) {
            connect(canvas, line);

        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position point = computePosition(i, j);
                canvas.drawCircle(point.x, point.y, radius, circlePaint);
            }
        }

    }

    private Position computePosition(int i, int j) {
        int x = offsetX + (i * space);
        int y = offsetY + ((rows - 1 - j) * space);
        return new Position(x, y);
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
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(radius - 5);
        int boxWidth = (cols - 1) * space;
        int boxHeight = (rows - 1) * space;
        DisplayMetrics displayMetrics = G.context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }

    private void connect(Canvas canvas, Line line) {
        Position p1 = computePosition(line.i1, line.j1);
        Position p2 = computePosition(line.i2, line.j2);
        if (line.playerIndex == 1) {
            linePaint.setColor(Color.parseColor("#4444ff"));
        } else {
            linePaint.setColor(Color.parseColor("#ff4444"));
        }
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        detectConnectingLine();
        invalidate();
        isSide1 = !isSide1;
        return super.onTouchEvent(event);
    }

    private float computeDiff(float x1, float j1, float x2, float j2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2)
                + Math.pow((j1 - j2), 2));
    }

    private void detectConnectingLine() {
        ArrayList<Diff> diffs = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                float diff = computeDiff(touchX, touchY,
                        computePosition(i, j).x, computePosition(i, j).y);
                diffs.add(new Diff(i, j, diff));
            }
        }
        Collections.sort(diffs, (o1, o2) -> o1.diff.compareTo(o2.diff));
        Diff min1 = diffs.get(0);
        Diff min2 = diffs.get(1);
        if (min1.diff > (float) space / 2) {
            return;
        }
        Line line = new Line(min1.i, min1.j, min2.i, min2.j, isSide1 ? 1 : 2);
        lines.add(line);
    }
}
