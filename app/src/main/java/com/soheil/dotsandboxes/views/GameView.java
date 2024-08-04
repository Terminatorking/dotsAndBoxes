package com.soheil.dotsandboxes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.G;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class GameView extends View {

    private Paint paintBox;
    private Paint paintDot;
    private Paint scoreBorderPaint;
    private Paint scoreEffectPaint;
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

    private static float[] playerIndexEffectRadius = new float[]{50, 25};
    private static boolean isRenderingLock = false;

    private static float lastLineAlpha = 0;

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
        private static ArrayList<Action> actions = new ArrayList<>();
        private static ArrayList<Box> boxes = new ArrayList<>();
    }


    private static class Options {
        private static final int TYPE_CPU = 0;
        private static final int TYPE_PLAYER = 1;

        private static int cols = 4;
        private static int rows = 4;

        private static String[] playerNames = new String[]{"Player 1", "Player 2"};
        private static int[] playerTypes = new int[]{TYPE_PLAYER, TYPE_CPU};
        //private static int[] playerTypes = new int[]{TYPE_PLAYER, TYPE_PLAYER};

        private static boolean highGraphic = true;
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


    private static class Action {
        public int i1;
        public int j1;
        public int i2;
        public int j2;
        public int playerIndex;

        public Action(int i1, int j1, int i2, int j2, int playerIndex) {
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

        if (Options.highGraphic) {
            mainLoop();
        }

        initializePaints();
        initializeMetrics();
    }


    private void initializePaints() {
        paintDot = new Paint();
        paintDot.setColor(Color.WHITE);
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setAntiAlias(true);

        scoreBorderPaint = new Paint();
        scoreBorderPaint.setColor(Color.WHITE);
        scoreBorderPaint.setStyle(Paint.Style.STROKE);
        scoreBorderPaint.setStrokeWidth(10);
        scoreBorderPaint.setAntiAlias(true);

        scoreEffectPaint = new Paint();
        scoreEffectPaint.setColor(Color.WHITE);
        scoreEffectPaint.setStyle(Paint.Style.STROKE);
        scoreEffectPaint.setStrokeWidth(4);
        scoreEffectPaint.setAntiAlias(true);

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

        State.actions.clear();
        State.boxes.clear();

        populateMoves();

        if (isCpuTurn()) {
            playNext();
        }

        refresh();
    }


    private void mainLoop() {
        Thread main = new Thread(new Runnable() {
            @Override
            public void run() {
                long physicLastTime = System.currentTimeMillis();
                long renderLastTime = System.currentTimeMillis();

                while (true) {
                    long physicElapsedTime = System.currentTimeMillis() - physicLastTime;
                    if (physicElapsedTime > 20) {
                        updatePhysic(physicElapsedTime);
                        physicLastTime = System.currentTimeMillis();
                    }


                    long renderElapsedTime = System.currentTimeMillis() - renderLastTime;
                    if (renderElapsedTime > 15) {
                        renderGame();
                        renderLastTime = System.currentTimeMillis();
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        main.start();
    }


    private void updatePhysic(long elapsedTime) {
        for (int i = 0; i < playerIndexEffectRadius.length; i++) {
            playerIndexEffectRadius[i] += elapsedTime * 0.08f;
            if (playerIndexEffectRadius[i] > 90) {
                playerIndexEffectRadius[i] = 50;
            }
        }

        lastLineAlpha += elapsedTime * 0.005f;
        if (lastLineAlpha >= 1) {
            isRenderingLock = false;
            lastLineAlpha = 0;
        }
    }


    private void renderGame() {
        postInvalidate();
    }


    private void addToAvailableMoves(int i1, int j1, int i2, int j2) {
        boolean isInAction = false;
        for (Action action : State.actions) {
            if (action.i1 == i1 && action.j1 == j1 && action.i2 == i2 && action.j2 == j2) {
                isInAction = true;
                break;
            }
        }

        if (!isInAction) {
            availableMoves.add(new Move(i1, j1, i2, j2));
        }
    }

    private void populateMoves() {
        availableMoves.clear();

        for (int i = 0; i < Options.cols - 1; i++) {
            for (int j = 0; j < Options.rows; j++) {
                int i1 = i;
                int j1 = j;
                int i2 = i + 1;
                int j2 = j;
                addToAvailableMoves(i1, j1, i2, j2);
            }
        }

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows - 1; j++) {
                int i1 = i;
                int j1 = j;
                int i2 = i;
                int j2 = j + 1;

                addToAvailableMoves(i1, j1, i2, j2);
            }
        }
    }


    private void refresh() {
        if (isGameFinished()) {
            State.isGameOver = true;
        }

        invalidate();
    }


    public void saveGame() {
        JSONObject save = new JSONObject();
        JSONObject options = new JSONObject();
        JSONObject state = new JSONObject();
        JSONArray actions = new JSONArray();
        try {
            options.put("cols", Options.cols);
            options.put("rows", Options.rows);
            options.put("opponentType", getPlayerType(2));
            save.put("options", options);

            state.put("playerIndex", getPlayerIndex());
            state.put("actions", actions);

            for (Action action : State.actions) {
                JSONObject jsonAction = new JSONObject();
                jsonAction.put("i1", action.i1);
                jsonAction.put("j1", action.j1);
                jsonAction.put("i2", action.i2);
                jsonAction.put("j2", action.j2);
                jsonAction.put("playerIndex", action.playerIndex);

                actions.put(jsonAction);
            }

            save.put("state", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String savedGame = save.toString();

        File file = new File(G.APP_DIR + "/save.dat");

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(savedGame);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public void loadGame() {
        File file = new File(G.APP_DIR + "/save.dat");
        StringBuilder savedGameBuilder = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                savedGameBuilder.append(line);
                savedGameBuilder.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String savedGame = savedGameBuilder.toString();

        try {
            JSONObject save = new JSONObject(savedGame);
            JSONObject options = save.getJSONObject("options");

            Options.cols = options.getInt("cols");
            Options.rows = options.getInt("rows");
            int opponentType = options.getInt("opponentType");
            //Options.playerTypes[1] = opponentType;

            resetGame();

            JSONObject state = save.getJSONObject("state");
            int playerIndex = state.getInt("playerIndex");
            State.isSide1 = playerIndex == 1;

            JSONArray actions = state.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++) {
                JSONObject jsonAction = actions.getJSONObject(i);
                State.actions.add(new Action(
                        jsonAction.getInt("i1"),
                        jsonAction.getInt("j1"),
                        jsonAction.getInt("i2"),
                        jsonAction.getInt("j2"),
                        jsonAction.getInt("playerIndex")
                ));
            }

            populateMoves();

            invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        if (Options.highGraphic && isRenderingLock) {
            for (int i = 0; i < State.actions.size() - 1; i++) {
                Action action = State.actions.get(i);
                drawLine(canvas, action);
            }

            drawAnimateLastAction(canvas);
        } else {
            for (Action line : State.actions) {
                drawLine(canvas, line);
            }
        }
    }


    private void drawAnimateLastAction(Canvas canvas) {
        Action lastAction = State.actions.get(State.actions.size()-1);
        Position p1 = getPointPoisition(lastAction.i1, lastAction.j1);
        Position p2 = getPointPoisition(lastAction.i2, lastAction.j2);
        paintLine.setColor(getPlayerColor(lastAction.playerIndex));
        if (p1.x == p2.x) {
            //vertical
            canvas.drawLine(p1.x, p1.y, p2.x, p1.y - Theme.space * lastLineAlpha, paintLine);
        } else {
            //horizontal
            canvas.drawLine(p1.x, p1.y, p1.x + Theme.space * lastLineAlpha, p2.y, paintLine);
        }
    }


    private void drawPlayerIndexEffect(Canvas canvas, int x, int y) {
        for (int i = 0; i < playerIndexEffectRadius.length; i++) {
            float alpha = 2.5f * (100 - playerIndexEffectRadius[i] - 10);
            scoreEffectPaint.setAlpha((int) alpha);
            canvas.drawCircle(x, y, playerIndexEffectRadius[i], scoreEffectPaint);
        }
    }


    private void drawLine(Canvas canvas, Action line) {
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
        paintBox.setColor(getPlayerColor(playerIndex));
        canvas.drawCircle(x, y, 50, paintBox);
        canvas.drawText("" + getPlayerScore(playerIndex), x, y + 10, paintText);
        canvas.drawText(getPlayerName(playerIndex), x, y + 100, paintText);

        if (playerIndex == getPlayerIndex()) {
            if (Options.highGraphic) {
                drawPlayerIndexEffect(canvas, x, y);
            } else {
                scoreBorderPaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, 60, scoreBorderPaint);
            }
        } else {
            scoreBorderPaint.setColor(Color.parseColor("#444444"));
            canvas.drawCircle(x, y, 60, scoreBorderPaint);
        }
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
        String message = "";
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

        if (isRenderingLock) {
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
        for (Action line : State.actions) {
            if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j) {
                return false;
            }
        }

        // add line to list of connected actions
        Action line = new Action(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, getPlayerIndex());
        State.actions.add(line);

        if (Options.highGraphic) {
            isRenderingLock = true;
            lastLineAlpha = 0;
        }

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

        if (fill3sidesBoxes()) {
            return;
        }

        ArrayList<Move> unsafeMoves = detectUnsafeMoves();

        if (makeRandomSafeMove(unsafeMoves)) {
            return;
        }

        makeRandomMove();
    }


    private ArrayList<Move> detectUnsafeMoves() {
        ArrayList<Move> unsafeMoves = new ArrayList<>();

        for (int i = 0; i <= Options.cols - 2; i++) {
            for (int j = 0; j <= Options.rows - 2; j++) {
                ArrayList<Integer> freeSides = new ArrayList<>();

                if (hasLeft(i, j)) {
                    freeSides.add(EDGE_LEFT);
                }

                if (hasRight(i, j)) {
                    freeSides.add(EDGE_RIGHT);
                }

                if (hasTop(i, j)) {
                    freeSides.add(EDGE_TOP);
                }

                if (hasBottom(i, j)) {
                    freeSides.add(EDGE_BOTTOM);
                }

                if (freeSides.size() == 2) {
                    Log.i("LOG", "Sides = 2");
                    if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_RIGHT)) {
                        //top, bottom
                        unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
                        unsafeMoves.add(new Move(i, j, i + 1, j));
                    }

                    if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_TOP)) {
                        //right, bottom
                        unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
                        unsafeMoves.add(new Move(i, j, i + 1, j));
                    }

                    if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_BOTTOM)) {
                        //right, top
                        unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
                        unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
                    }

                    if (freeSides.contains(EDGE_RIGHT) && freeSides.contains(EDGE_TOP)) {
                        //left, bottom.
                        unsafeMoves.add(new Move(i, j, i, j + 1));
                        unsafeMoves.add(new Move(i, j, i + 1, j));
                    }

                    if (freeSides.contains(EDGE_RIGHT) && freeSides.contains(EDGE_BOTTOM)) {
                        //left, top
                        unsafeMoves.add(new Move(i, j, i, j + 1));
                        unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
                    }

                    if (freeSides.contains(EDGE_TOP) && freeSides.contains(EDGE_BOTTOM)) {
                        //left, right
                        unsafeMoves.add(new Move(i, j, i, j + 1));
                        unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
                    }
                }
            }
        }

        return unsafeMoves;
    }


    private boolean makeRandomSafeMove(ArrayList<Move> unsafeMoves) {
        ArrayList<Move> safeMoves = new ArrayList<>();

        for (Move move : availableMoves) {
            boolean isSafeMove = true;
            for (Move testMove : unsafeMoves) {
                if (testMove.i1 == move.i1 && testMove.i2 == move.i2 && testMove.j1 == move.j1 && testMove.j2 == move.j2) {
                    isSafeMove = false;
                    break;
                }
            }

            if (isSafeMove) {
                safeMoves.add(move);
            }
        }

        if (safeMoves.size() == 0) {
            return false;
        }

        int moveIndex = getRandom(0, safeMoves.size() - 1);
        Move move = safeMoves.get(moveIndex);

        connectLine(new Point(move.i1, move.j1), new Point(move.i2, move.j2));
        return true;
    }


    private boolean makeRandomMove() {
        int moveIndex = getRandom(0, availableMoves.size() - 1);
        Move move = availableMoves.get(moveIndex);
        connectLine(new Point(move.i1, move.j1), new Point(move.i2, move.j2));

        return true;
    }


    private boolean fill3sidesBoxes() {
        for (int i = 0; i <= Options.cols - 2; i++) {
            for (int j = 0; j <= Options.rows - 2; j++) {
                int sides = 0;
                int freeSide = -1;

                if (hasBottom(i, j)) {
                    sides++;
                } else {
                    freeSide = EDGE_BOTTOM;
                }

                if (hasRight(i, j)) {
                    sides++;
                } else {
                    freeSide = EDGE_RIGHT;
                }

                if (hasLeft(i, j)) {
                    sides++;
                } else {
                    freeSide = EDGE_LEFT;
                }

                if (hasTop(i, j)) {
                    sides++;
                } else {
                    freeSide = EDGE_TOP;
                }

                if (sides == 3) {
                    Log.i("LOG", "Found Sided = 3");
                    switch (freeSide) {
                        case EDGE_BOTTOM:
                            connectBottom(i, j);
                            return true;
                        case EDGE_RIGHT:
                            connectRight(i, j);
                            return true;
                        case EDGE_LEFT:
                            connectLeft(i, j);
                            return true;
                        case EDGE_TOP:
                            connectTop(i, j);
                            return true;
                    }
                }
            }
        }

        return false;
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
        for (Action line : State.actions) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasRight(int i, int j) {
        for (Action line : State.actions) {
            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasTop(int i, int j) {
        for (Action line : State.actions) {
            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasBottom(int i, int j) {
        for (Action line : State.actions) {
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

        for (Action line : State.actions) {
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
