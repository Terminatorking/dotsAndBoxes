package com.soheil.dotsandboxes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.G;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("all")
public class GameView extends View {

    private Paint paintBox;
    private Paint paintDot;
    private Paint paintTouch;
    private Paint paintText;
    private Paint paintLine;

    private int boxWidth;
    private int boxHeight;

    private int screenWidth;
    private int screenWidthHalf;
    private int screenHeight;

    private int offsetX;
    private int offsetY;

    private float touchX;
    private float touchY;

    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_TOP = 2;
    private static final int EDGE_BOTTOM = 3;


    private static ArrayList<Move> availableMoves = new ArrayList<>();

    private static class Theme {
        private static int[] playerColors = new int[]{Color.parseColor("#4444ff"), Color.parseColor("#ff4444")};
        private static int space = 150;
        private static int radius = 15;
        private static int backgroundColor = Color.parseColor("#222222");
    }


    private static class State {
        private static int[] playerScores = new int[]{0, 0};

        private static boolean isGameOver = false;
        private static boolean isSide1 = true;
        private static ArrayList<Line> lines = new ArrayList<>();
        private static ArrayList<Box> boxes = new ArrayList<>();
    }


    private static class Options {
        private static final int TYPE_CPU = 0;
        private static final int TYPE_PLAYER = 1;

        private static int cols = 5;
        private static int rows = 4;

        private static String[] playerNames = new String[]{"Player 1", "Player 2"};
        private static int[] playerTypes = new int[]{TYPE_CPU, TYPE_CPU};
        //private static int[] playerTypes = new int[]{TYPE_PLAYER, TYPE_PLAYER};
    }


    private static class Debug {
        private static boolean isDebugMode = true;
        private static boolean drawTouch = false;
        private static boolean drawDotNames = false;
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
        public Point point;
        public Float diff;

        public Diff(Point point, float diff) {
            this.point = point;
            this.diff = diff;
        }
    }


    private static class Point {
        public int i;
        public int j;

        public Point(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }


    private static class Box {
        public int i;
        public int j;
        public int playerIndex;

        public Box(int i, int j) {
            this.i = i;
            this.j = j;
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


    private static class Move {
        public int i1;
        public int j1;
        public int i2;
        public int j2;

        public Move(int i1, int j1, int i2, int j2) {
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
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


    private void initialize() {
        if (isInEditMode()) {
            return;
        }

        initializePaints();
        initializeMetrics();
    }


    private void initializePaints() {
        paintDot = new Paint();
        paintDot.setColor(Color.WHITE);
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setAntiAlias(true);

        paintBox = new Paint();
        paintBox.setColor(Color.WHITE);
        paintBox.setStyle(Paint.Style.FILL);
        paintBox.setAntiAlias(true);

        paintTouch = new Paint();
        paintTouch.setColor(Color.RED);
        paintTouch.setStyle(Paint.Style.FILL);
        paintTouch.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#4444ff"));
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setStrokeWidth(10);
        paintLine.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        paintText.setTextSize(30);
        paintText.setTextAlign(Paint.Align.CENTER);
    }


    private void initializeMetrics() {
        boxWidth = (Options.cols - 1) * Theme.space;
        boxHeight = (Options.rows - 1) * Theme.space;

        screenWidth = G.screenWidth;
        screenHeight = G.screenHeight;

        screenWidthHalf = screenWidth / 2;

        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }


    public void resetGame() {
        State.playerScores[0] = 0;
        State.playerScores[1] = 0;

        Debug.isDebugMode = false;
        State.isSide1 = true;

        State.isGameOver = false;

        State.lines.clear();
        State.boxes.clear();

        populateMoves();

        if (isCpuTurn()) {
            playNext();
        }

        refresh();
    }


    private void populateMoves() {
        availableMoves.clear();

        for (int i = 0; i < Options.cols - 1; i++) {
            for (int j = 0; j < Options.rows; j++) {
                availableMoves.add(new Move(i, j, i + 1, j));
            }
        }

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows - 1; j++) {
                availableMoves.add(new Move(i, j, i, j + 1));
            }
        }
    }


    private void refresh() {
        if (isGameFinished()) {
            State.isGameOver = true;
        }

        invalidate();
    }


    private boolean isGameFinished() {
        return State.boxes.size() == (Options.cols - 1) * (Options.rows - 1);
    }


    private float computeDiff(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    private int getPlayerColor(int playerIndex) {
        return Theme.playerColors[playerIndex - 1];
    }


    private int getPlayerIndex() {
        return State.isSide1 ? 1 : 2;
    }


    private int getPlayerScore(int playerIndex) {
        return State.playerScores[playerIndex - 1];
    }


    private int getPlayerType(int playerIndex) {
        return Options.playerTypes[playerIndex - 1];
    }


    private void increasePlayerScore(int playerIndex) {
        State.playerScores[playerIndex - 1]++;
    }


    private String getPlayerName(int playerIndex) {
        return Options.playerNames[playerIndex - 1];
    }


    private boolean isCpuTurn() {
        return getPlayerType(getPlayerIndex()) == Options.TYPE_CPU;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        drawBackground(canvas);
        drawConnectedLines(canvas);
        drawBoxes(canvas);
        drawDots(canvas);
        drawScores(canvas);
        drawDebugTouchPosition(canvas);
        drawDebugNaming(canvas);

        if (State.isGameOver) {
            drawFinishMessage(canvas);
        }
    }


    private Position getPointPoisition(int i, int j) {
        int x = offsetX + (i * Theme.space);
        int y = offsetY + ((Options.rows - 1 - j) * Theme.space);

        return new Position(x, y);
    }


    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Theme.backgroundColor);
    }


    private void drawConnectedLines(Canvas canvas) {
        for (Line line : State.lines) {
            drawLine(canvas, line);
        }
    }


    private void drawLine(Canvas canvas, Line line) {
        Position p1 = getPointPoisition(line.i1, line.j1);
        Position p2 = getPointPoisition(line.i2, line.j2);
        paintLine.setColor(getPlayerColor(line.playerIndex));
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine);
    }


    private void drawBoxes(Canvas canvas) {
        for (Box box : State.boxes) {
            paintBox.setColor(getPlayerColor(box.playerIndex));
            Position boxPos = getPointPoisition(box.i, box.j);
            canvas.drawCircle(boxPos.x + Theme.space / 2, boxPos.y - Theme.space / 2, 30, paintBox);
        }
    }


    private void drawDots(Canvas canvas) {
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position point = getPointPoisition(i, j);
                canvas.drawCircle(point.x, point.y, Theme.radius, paintDot);
            }
        }
    }


    private void drawPlayerScore(Canvas canvas, int playerIndex, int x, int y) {
        if (playerIndex == getPlayerIndex()) {
            paintBox.setColor(Color.WHITE);
            canvas.drawCircle(x, y, 50, paintBox);
        } else {
            paintBox.setColor(Color.parseColor("#444444"));
            canvas.drawCircle(x, y, 50, paintBox);
        }

        paintBox.setColor(getPlayerColor(playerIndex));
        canvas.drawCircle(x, y, 40, paintBox);
        canvas.drawText("" + getPlayerScore(playerIndex), x, y + 10, paintText);
        canvas.drawText(getPlayerName(playerIndex), x, y + 80, paintText);

    }


    private void drawScores(Canvas canvas) {
        drawPlayerScore(canvas, 1, screenWidthHalf - 100, 100);
        drawPlayerScore(canvas, 2, screenWidthHalf + 100, 100);
    }


    private void drawFinishMessage(Canvas canvas) {
        canvas.drawText(getGameFinishMessage(), screenWidthHalf, getHeight() - 100, paintText);
    }


    private void drawDebugTouchPosition(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawTouch) {
            return;
        }

        canvas.drawCircle(touchX, touchY, 10, paintTouch);
    }


    private void drawDebugNaming(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawDotNames) {
            return;
        }

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                String name = "" + i + "," + j;
                Position point = getPointPoisition(i, j);
                canvas.drawText(name, point.x, point.y + 50, paintText);
            }
        }
    }


    private String getGameFinishMessage() {
        String message ;
        if (getPlayerScore(1) == getPlayerScore(2)) {
            message = G.context.getString(R.string.gameDraw);
        } else if (getPlayerScore(1) > getPlayerScore(2)) {
            message = getPlayerName(1) + G.context.getString(R.string.playerWonGame);
        } else {
            message = getPlayerName(2) + G.context.getString(R.string.playerWonGame);
        }

        return message;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (State.isGameOver) {
            return true;
        }

        if (isCpuTurn()) {
            return true;
        }

        touchX = event.getX();
        touchY = event.getY();

        ArrayList<Diff> diffs = getDiffsByOrder();

        Diff diff1 = diffs.get(0);
        Diff diff2 = diffs.get(1);

        connectLine(diff1.point, diff2.point);
        refresh();

        return super.onTouchEvent(event);
    }


    private ArrayList<Diff> getDiffsByOrder() {
        ArrayList<Diff> diffs = new ArrayList<>();

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position position = getPointPoisition(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(new Point(i, j), diff));
            }
        }

        Collections.sort(diffs, new Comparator<Diff>() {
                    @Override
                    public int compare(Diff o1, Diff o2) {
                        return o1.diff.compareTo(o2.diff);
                    }
                }
        );

        return diffs;
    }


    private boolean connectLine(Point point1, Point point2) {
        Point firstPoint;
        Point secondPoint;

        Box box1;
        Box box2 = null;

        if (point1.i == point2.i) {
            // vertical
            if (point1.j < point2.j) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }

            box1 = new Box(firstPoint.i, firstPoint.j);

            if (firstPoint.i > 0) {
                box2 = new Box(firstPoint.i - 1, firstPoint.j);
            }
        } else {
            // horizontal
            if (point1.i < point2.i) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }

            box1 = new Box(firstPoint.i, firstPoint.j);

            if (firstPoint.j > 0) {
                box2 = new Box(firstPoint.i, firstPoint.j - 1);
            }
        }

        // if this line is already connected
        for (Line line : State.lines) {
            if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j) {
                return false;
            }
        }

        // add line to list of connected lines
        Line line = new Line(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, getPlayerIndex());
        State.lines.add(line);

        for (int index = availableMoves.size() - 1; index >= 0; index--) {
            Move move = availableMoves.get(index);
            if (move.i1 == line.i1 && move.i2 == line.i2 && move.j1 == line.j1 && move.j2 == line.j2) {
                availableMoves.remove(move);
                break;
            }
        }

        // check if player get award
        boolean wonBox1 = checkBox(box1);
        boolean wonBox2 = false;

        if (box2 != null) {
            wonBox2 = checkBox(box2);
        }

        boolean mustSwitchSide = !wonBox1 && !wonBox2;

        // if switching side required
        if (mustSwitchSide) {
            switchSide();
            return true;
        }

        playNext();
        return true;
    }


    private void switchSide() {
        State.isSide1 = !State.isSide1;
        playNext();
    }


    private void playNext() {
        if (isCpuTurn()) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ai();
                    refresh();
                }
            }, 100);
        }
    }


    private int getRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }


    private void ai() {
        if (isGameFinished()) {
            return;
        }

        while (true) {
            int moveIndex = getRandom(0, availableMoves.size() - 1);
            Move move = availableMoves.get(moveIndex);

            boolean connected = connectLine(new Point(move.i1, move.j1), new Point(move.i2, move.j2));
            if (connected) {
                break;
            }
        }
    }


    private boolean connectLeft(int i, int j) {
        return connectLine(new Point(i, j), new Point(i, j + 1));
    }


    private boolean connectRight(int i, int j) {
        return connectLine(new Point(i + 1, j), new Point(i + 1, j + 1));
    }


    private boolean connectTop(int i, int j) {
        return connectLine(new Point(i, j + 1), new Point(i + 1, j + 1));
    }


    private boolean connectBottom(int i, int j) {
        return connectLine(new Point(i, j), new Point(i + 1, j));
    }


    private boolean hasLeft(int i, int j) {
        for (Line line : State.lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasRight(int i, int j) {
        for (Line line : State.lines) {
            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasTop(int i, int j) {
        for (Line line : State.lines) {
            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasBottom(int i, int j) {
        for (Line line : State.lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                return true;
            }
        }

        return false;
    }


    private boolean checkBox(Box box) {
        int i = box.i;
        int j = box.j;

        boolean hasLeft = false;
        boolean hasRight = false;
        boolean hasTop = false;
        boolean hasBottom = false;

        for (Line line : State.lines) {
            hasLeft = hasLeft(i, j);
            hasRight = hasRight(i, j);
            hasTop = hasTop(i, j);
            hasBottom = hasBottom(i, j);
        }

        boolean isFullConnected = hasLeft && hasRight && hasTop && hasBottom;
        if (isFullConnected) {
            box.playerIndex = getPlayerIndex();
            State.boxes.add(box);

            increasePlayerScore(box.playerIndex);
            return true;
        }

        return false;
    }

}
