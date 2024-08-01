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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("all")
public class GameView extends View {
    private final String player1Color = "#4444ff";
    private final String player2Color = "#ff4444";
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
    private Paint homePaint;
    private float touchX;
    private float touchY;
    private final ArrayList<Line> lines = new ArrayList<>();
    private final ArrayList<Home> homes = new ArrayList<>();

    private static class Home {
        public int i;
        public int j;
        public int playerIndex;

        public Home(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

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
        for (Home home : homes) {
            if (home.playerIndex == 1) {
                homePaint.setColor(Color.parseColor(player1Color));
            } else {
                homePaint.setColor(Color.parseColor(player2Color));
            }
            Position position = computePosition(home.i, home.j);
            canvas.drawCircle(position.x + (space / 2), position.y - (space / 2), radius, homePaint);
        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position point = computePosition(i, j);
                canvas.drawCircle(point.x, point.y, radius + 10, circlePaint);
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
        homePaint = new Paint();
        homePaint.setAntiAlias(true);
        homePaint.setStyle(Paint.Style.FILL_AND_STROKE);
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
            linePaint.setColor(Color.parseColor(player1Color));
        } else {
            linePaint.setColor(Color.parseColor(player2Color));
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
        Diff firstPoint;
        Diff secondPoint;
        Home home1 = null;
        Home home2 = null;
        if (min1.i == min2.i) {
            //vertical
            if (min1.j < min2.j) {
                firstPoint = min1;
                secondPoint = min2;
            } else {
                firstPoint = min2;
                secondPoint = min1;
            }
            home1 = new Home(firstPoint.i, firstPoint.j);
            if (firstPoint.i > 0) {
                home2 = new Home(firstPoint.i - 1, firstPoint.j);
            }

        } else {
            //horizontal
            if (min1.i < min2.i) {
                firstPoint = min1;
                secondPoint = min2;
            } else {
                firstPoint = min2;
                secondPoint = min1;
            }
            home1 = new Home(firstPoint.i, firstPoint.j);
            if (firstPoint.j > 0) {
                home2 = new Home(firstPoint.i, firstPoint.j - 1);
            }
        }
//        if (firstPoint.diff > (float) space / 2) {
//            return;
//        }
        Line line = new Line(firstPoint.i, firstPoint.j,
                secondPoint.i, secondPoint.j, isSide1 ? 1 : 2);
        lines.add(line);
        if (home1 != null) {
            checkHome(home1);
        }
        if (home2 != null) {
            checkHome(home2);
        }
    }

    private void checkHome(Home home) {
        int i = home.i;
        int j = home.j;
        boolean leftConnected = false;
        boolean rightConnected = false;
        boolean topConnected = false;
        boolean bottomConnected = false;
        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j &&
                    line.i2 == i && line.j2 == j + 1) {
                leftConnected = true;
            }

            if (line.i1 == i + 1 && line.j1 == j &&
                    line.i2 == i + 1 && line.j2 == j + 1) {
                rightConnected = true;
            }

            if (line.i1 == i && line.j1 == j + 1 &&
                    line.i2 == i + 1 && line.j2 == j + 1) {
                topConnected = true;
            }

            if (line.i1 == i && line.j1 == j &&
                    line.i2 == i + 1 && line.j2 == j) {
                bottomConnected = true;
            }
        }
        boolean isFullConnected = leftConnected && rightConnected
                && topConnected && bottomConnected;
        if (isFullConnected) {
            home.playerIndex = isSide1 ? 1 : 2;
            homes.add(home);
        }
    }

    private void showSnackBar(View view, String string, int lenght) {
        Snackbar.make(view, string, lenght).show();
    }
}
