package uz.qubemelon.ilearn.ui.activities.games.words;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.models.games.words.Word;
import uz.qubemelon.ilearn.utilities.games.Constants;
import uz.qubemelon.ilearn.utilities.Settings;

public class GameBoardLayout extends LinearLayout {

    private Integer pencilOffset;
    private Rect pencilOffsetRect;
    private Rect pencilEndRect;
    private Integer selectionCount;
    private Direction direction;
    private Paint defaultPaint, correctPaint;
    private List<View> previousWord;
    private Bitmap memory;
    private int colWidth;
    private int cols, rows;
    private float pencilRadius;
    private final float density = getResources().getDisplayMetrics().density;
    private final float delta = (int) (50.0 * density + 0.5);

    private GameStatus game_status;

    private OnWordHighlightedListener onWordHighlightedListener;

    public GameBoardLayout(Context context) {
        super(context);
        cols = 10;
        rows = 10;
        init();
    }

    public GameBoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        cols = Integer.parseInt(Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_grid_size), Constants.DEFAULT_GRID_SIZE+""));

        rows = cols;
        init();
    }

    private void clearSelection() {
        if (direction != null && selectionCount != null) {
            onWordHighlightedListener.wordHighlighted(findCoordinatesUnderPencil(direction, pencilOffset, selectionCount));
        }
        pencilOffset = null;
        selectionCount = null;
        direction = null;
        postInvalidate();
    }

    private View findChildByPosition(int index) {
        int row = (int) Math.floor((double) index / (double) cols);
        int col = index % cols;
        LinearLayout rowView = (LinearLayout) getChildAt(row);
        return rowView.getChildAt(col);
    }

    public int getNumColumns() {
        return cols;
    }

    public int getNumRows() {
        return rows;
    }

    private List<Integer> findCoordinatesUnderPencil(Direction direction, int startPosition, int steps) {
        List<Integer> positions = new ArrayList<>();
        int curRow = startPosition / cols;
        int curCol = startPosition % cols;

        for (int i = 0; i <= steps; i++) {
            positions.add((curRow * cols) + curCol);

            if (direction.isUp()) {
                curRow -= 1;
            } else if (direction.isDown()) {
                curRow += 1;
            }

            if (direction.isLeft()) {
                curCol -= 1;
            } else if (direction.isRight()) {
                curCol += 1;
            }

            if (curRow < 0 || curCol < 0 || curRow >= rows || curCol >= cols) {
                break;
            }

        }
        return positions;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof GameStatus) {
            super.onRestoreInstanceState(((GameStatus) state).getSuperState());
            game_status = ((GameStatus) state);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        GameStatus saveState = new GameStatus(state);
        saveState.found_words = game_status.found_words;
        return saveState;
    }

    public void clear() {
        if (previousWord != null) {
            previousWord.clear();
        }
        game_status = new GameStatus();
        pencilOffset = null;
        pencilOffsetRect = null;
        pencilEndRect = null;
        direction = null;
        memory = null;
        selectionCount = null;
    }

    private void isNewSelection(float xPos, float yPos) {
        if (pencilOffset == null) {
            int position = point2XAxis((int) xPos, (int) yPos);
            if (position >= 0) {
                View item = findChildByPosition(position);
                pencilOffsetRect = getLetterBounds(item);
                pencilOffset = position;
            }
            postInvalidate();
        } else {
            float xDelta = xPos - pencilOffsetRect.centerX();
            float yDelta = (yPos - pencilOffsetRect.centerY()) * -1;
            double distance = Math.hypot(xDelta, yDelta);

            if (isInTouchMode() && distance < delta) {
                return;
            }

            Direction previousDirection = direction;
            Integer previousSteps = selectionCount;
            direction = Direction.getDirection((float) Math.atan2(yDelta, xDelta));

            float stepSize = direction.isAngle() ? (float) Math.hypot(colWidth, colWidth) : colWidth;
            selectionCount = Math.round((float) distance / stepSize);

            if (selectionCount == 0) {
                selectionCount = null;
            }

            if (direction != previousDirection || selectionCount != previousSteps) {
                List<View> selectedViews = getSelectedLetters();
                if (selectedViews == null) {
                    return;
                }


                if (previousWord != null && !previousWord.isEmpty()) {
                    List<View> oldViews = new ArrayList<>(previousWord);
                    oldViews.removeAll(selectedViews);
                }

                previousWord = selectedViews;


                if (!selectedViews.isEmpty()) {
                    View endView = selectedViews.get(selectedViews.size() - 1);
                    pencilEndRect = getLetterBounds(endView);
                }
                postInvalidate();
            }
        }

    }

    public void populateBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                View v = findChildByPosition(i * cols + j);
                ((TextView) v.findViewById(R.id.letter)).setText("" + board[i][j]);
            }
        }
    }

    public void setOnWordHighlightedListener(OnWordHighlightedListener onWordSelectedListener) {
        onWordHighlightedListener = onWordSelectedListener;
    }

    public void goal(Word word) {
        if (!game_status.found_words.contains(word)) {
            game_status.found_words.add(word);
            if (memory == null) {
                memory = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas foundCanvas = new Canvas(memory);

            boolean constColor = Settings.getBooleanValue(getContext(), getContext().getResources().getString(R.string.pref_key_line_color_mode), false);

            if(!constColor){
                Random random = new Random();
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                correctPaint.setARGB(0xA0, r, g, b);
            }
            word.setColor(correctPaint.getColor());
            paintGenericSelection(word, foundCanvas, correctPaint);
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (memory == null) {

            memory = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas foundCanvas = new Canvas(memory);
            for (Word word : game_status.found_words) {
                paintGenericSelection(word, foundCanvas, correctPaint);
            }
        }

        canvas.drawBitmap(memory, 0f, 0f, correctPaint);

        if (direction != null && selectionCount != null && pencilOffset != null) {
            paintCurrentSelection(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            colWidth = (int) ((float) getMeasuredWidth() / (float) cols);
        } else {
            if (getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels) {
                super.onMeasure(heightMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            } else {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
            }
            colWidth = (int) ((float) getMeasuredWidth() / (float) cols);
        }

        boolean roundedPencil = Settings.getBooleanValue(getContext(), getContext().getResources().getString(R.string.pref_key_rounded_line), true);
        if(roundedPencil){
            pencilRadius = getMeasuredWidth() / 28.0f;
        }else{
            pencilRadius = 0;
        }
    }

    private void paintCurrentSelection(Canvas canvas) {
        float pad = colWidth / 3.2f;
        RectF superRect = new RectF(-pad, -pad, pad, pad);
        float xDelta = pencilEndRect.centerX() - pencilOffsetRect.centerX();
        float yDelta = (pencilEndRect.centerY() - pencilOffsetRect.centerY()) * -1;
        double distance = Math.hypot(xDelta, yDelta);
        superRect.right += distance;

        canvas.save();
        canvas.translate(pencilOffsetRect.centerX(), pencilOffsetRect.centerY());
        canvas.rotate(direction.getAngleDegree());
        canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, defaultPaint);
        canvas.restore();
    }

    private void paintGenericSelection(Word word, Canvas canvas, Paint paint) {
        float angleStep = (float) Math.hypot(colWidth, colWidth);
        float pad = colWidth / 3.2f;
        float distance = (word.getDirection().isAngle() ? angleStep : colWidth) * (word.getText().length() - 1);
        RectF superRect = new RectF(-pad, -pad, pad, pad);
        superRect.right += distance;

        View v = findChildByPosition((word.getY() * cols) + word.getX());
        Rect viewRect = getLetterBounds(v);

        int savedColor = word.getColor();
        if(savedColor != 0){
            paint.setARGB(0xA0, Color.red(savedColor), Color.green(savedColor), Color.blue(savedColor));
        }

        canvas.save();
        canvas.translate(viewRect.centerX(), viewRect.centerY());
        canvas.rotate(word.getDirection().getAngleDegree());
        canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, paint);
        canvas.restore();
    }

    private List<View> getSelectedLetters() {
        if (pencilOffset == null || selectionCount == null || direction == null) {
            return null;
        }

        List<View> views = new ArrayList<>();
        for (Integer position : findCoordinatesUnderPencil(direction, pencilOffset, selectionCount)) {
            views.add(findChildByPosition(position));
        }
        return views;
    }

    private Rect getLetterBounds(View v) {
        Rect viewRect = new Rect();
        v.getDrawingRect(viewRect);
        viewRect.offset(v.getLeft(), ((ViewGroup) v.getParent()).getTop());
        return viewRect;
    }

    private void init() {
        setWillNotDraw(false);
        setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        lp.weight = 1.0f;
        for (int i = 0; i < cols; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < cols; j++) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.words_grid_cell, null);
                view.setFocusable(true);
                row.addView(view, lp);
            }
            addView(row, lp);
        }

        setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent e) {

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        isNewSelection(e.getX(), e.getY());

                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        clearSelection();
                        break;
                }
                return true;
            }
        });
        game_status = new GameStatus();

        int color = Settings.getIntValue(getContext(), getContext().getResources().getString(R.string.pref_key_line_color), 0x0099cc);

        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setARGB(0xFF, Color.red(color), Color.green(color), Color.blue(color));

        correctPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        correctPaint.setARGB(0xA0, Color.red(color), Color.green(color), Color.blue(color));

    }

    private boolean containsPoint(float x, float y, View view) {
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        rect.offset(view.getLeft(), ((ViewGroup) view.getParent()).getTop());
        return rect.contains((int) x, (int) y);
    }

    private int point2XAxis(float x, float y) {
        for (int i = 0; i < cols * cols; i++) {
            if (containsPoint(x, y, findChildByPosition(i))) {
                return i;
            }
        }
        return -1;
    }

    public interface OnWordHighlightedListener {
        void wordHighlighted(List<Integer> positions);
    }
}
