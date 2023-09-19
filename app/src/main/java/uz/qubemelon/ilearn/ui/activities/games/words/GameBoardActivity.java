package uz.qubemelon.ilearn.ui.activities.games.words;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.RelativeLayout;
import com.nineoldandroids.animation.Animator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.games.WordTableAdapter;
import uz.qubemelon.ilearn.models.games.words.Word;
import uz.qubemelon.ilearn.utilities.Settings;

public class GameBoardActivity extends AppCompatActivity implements GameBoardLayout.OnWordHighlightedListener {

    private int rows = 10;
    private int cols = 10;
    private ProgressDialog dialog;
    private String[] wordList;
    private RelativeLayout word_list_label_group;
    private WordTableAdapter word_list_adapter;
    private GameBoardLayout grid;
    private char[][] board;
    private Set<Word> solution = new HashSet<>();
    private final Direction[] directions = Direction.values();
    private boolean[][] lock;
    private int[] randIndices;
    private Set<Word> found_words = new HashSet<>();
    private GridView grid_word_list;
    private View congrats_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_words_board);

        word_list_label_group = (RelativeLayout)findViewById(R.id.word_list_label_group);
        grid_word_list = (GridView)findViewById(R.id.grd_word_list);
        congrats_view = findViewById(R.id.checkmark);

        grid = (GameBoardLayout) findViewById(R.id.game_board);

        cols = grid.getNumColumns();
        rows = grid.getNumRows();
        board = new char[rows][cols];
        lock = new boolean[rows][cols];

        if(savedInstanceState != null && savedInstanceState.get("words") == null)
            savedInstanceState = null;


        if (savedInstanceState == null) {

            showDialog();
            Runnable r = new Runnable() {

                @Override
                public void run() {


                    selectWords();


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            clearBoard();
                            randomizeWords();

                            grid.setOnWordHighlightedListener(GameBoardActivity.this);
                            prepareBoard();
                            if (dialog != null)
                                dialog.dismiss();
                            if (word_list_label_group != null)
                                word_list_label_group.setVisibility(View.VISIBLE);
                            grid.setVisibility(View.VISIBLE);
                            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                            anim.setDuration(500);
                            grid.startAnimation(anim);

                        }
                    });
                }
            };

            new Thread(r).start();

        } else {

            wordList = savedInstanceState.getStringArray("words");
            clearBoard();
            List<Word> words = savedInstanceState.getParcelableArrayList("solution");
            for (Word word : words) {
                embedWord(word);
            }
            words = savedInstanceState.getParcelableArrayList("found");
            found_words = new HashSet<>(words);
            grid_word_list.setLayoutAnimation(null);

            grid.setOnWordHighlightedListener(this);
            prepareBoard();
            grid.setVisibility(View.VISIBLE);
            if(word_list_label_group!=null)
                word_list_label_group.setVisibility(View.VISIBLE);

            if (dialog != null)
                dialog.dismiss();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean keepAwake = Settings.getBooleanValue(this, getResources().getString(R.string.pref_disable_screen_lock), false);

        if(keepAwake){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void wordHighlighted(List<Integer> positions) {
        int firstPos = positions.get(0);
        int lastPos = positions.get(positions.size() - 1);
        StringBuilder forwardWord = new StringBuilder();
        StringBuilder reverseWord = new StringBuilder();
        for (Integer position : positions) {
            int row = position / cols;
            int col = position % cols;
            char c = board[row][col];
            forwardWord.append(c);
            reverseWord.insert(0, c);
        }

        for (Word word : solution) {
            int wordStart = (word.getY() * cols) + word.getX();
            if (wordStart != firstPos && wordStart != lastPos) {
                continue;
            }

            Word found = (word.getText().equals(forwardWord.toString())) ? word : null;
            if (found == null) {
                found = (word.getText().equals(reverseWord.toString())) ? word : null;
            }

            if (found != null) {
                boolean ok = found_words.add(found);
                grid.goal(found);
                word_list_adapter.setWordFound(found);
                if(ok) {
                    MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.word_found);
                    mPlayer.start();
                }
                break;
            }
        }

        if (found_words.size() == solution.size()) {
            puzzleFinished();
        }
    }

    private void startNewGame() {
        clearBoard();
        randomizeWords();
        prepareBoard();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        grid.startAnimation(anim);
        if(word_list_label_group!=null)
            word_list_label_group.startAnimation(anim);
        grid_word_list.startAnimation(anim);

        grid.setEnabled(true);
        grid_word_list.setEnabled(true);
    }

    private void gameFinishAnimation(){
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setFillAfter(true);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

                congrats_view.setVisibility(View.VISIBLE);
                MediaPlayer mPlayer = MediaPlayer.create(GameBoardActivity.this, R.raw.board_finished);
                mPlayer.start();
                YoYo.with(Techniques.DropOut)
                        .duration(700)
                        .withListener(new Animator.AnimatorListener(){
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        YoYo.with(Techniques.TakingOff)
                                                .duration(700)
                                                .withListener(new Animator.AnimatorListener(){
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                            startNewGame();
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                }).playOn(congrats_view);
                                    }
                                }, 2000);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .playOn(congrats_view);
            }
        });
        grid.startAnimation(fadeOut);
        if(word_list_label_group!=null)
            word_list_label_group.startAnimation(fadeOut);
        grid_word_list.startAnimation(fadeOut);

        fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setFillAfter(true);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                grid_word_list.startLayoutAnimation();
            }
        });
    }

    private void lockOrientation() {
        Display display = this.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            height = display.getHeight();
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default :
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void puzzleFinished() {
        lockOrientation();
        grid.setEnabled(false);
        grid_word_list.setEnabled(false);
        gameFinishAnimation();

    }

    private void selectWords(){
        String count = Settings.getStringValue(this, getResources().getString(R.string.pref_key_num_words_to_select), "1000");
        WordGameDatabase word_game_database = new WordGameDatabase(this);
        word_game_database.open();
        wordList = word_game_database.getRandomWords(Integer.parseInt(count));
        word_game_database.close();
    }

    private void showDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage(this.getResources().getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void prepareBoard() {
        grid.populateBoard(board);
        List<Word> sortedWords = new ArrayList<>(solution);
        Collections.sort(sortedWords);
        word_list_adapter = new WordTableAdapter(this, sortedWords);
        word_list_adapter.setWordsFound(found_words);
        grid_word_list.setAdapter(word_list_adapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("solution", new ArrayList<>(solution));
        outState.putParcelableArrayList("found", new ArrayList<>(found_words));
        outState.putStringArray("words", wordList);

    }

    private void randomizeWords() {
        randIndices = new int[rows * cols];

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < randIndices.length; i++) {
            randIndices[i] = i;
        }

        for (int i = randIndices.length - 1; i >= 1; i--) {
            int randIndex = rand.nextInt(i);
            int realIndex = randIndices[i];
            randIndices[i] = randIndices[randIndex];
            randIndices[randIndex] = realIndex;
        }

        for (int i = wordList.length - 1; i >= 1; i--) {
            int randIndex = rand.nextInt(i);
            String word = wordList[i];
            wordList[i] = wordList[randIndex];
            wordList[randIndex] = word;
        }

        for (String word : wordList) {
            addWord(word);
        }
    }

    private void addWord(String word) {
        if (word.length() > cols && word.length() > rows) {
            return;
        }



        Random rand = new Random();
        for (int i = directions.length - 1; i >= 1; i--) {
            int randIndex = rand.nextInt(i);
            Direction direction = directions[i];
            directions[i] = directions[randIndex];
            directions[randIndex] = direction;
        }

        Direction bestDirection = null;
        int bestRow = -1;
        int bestCol = -1;
        int bestScore = -1;
        for (int index : randIndices) {
            int row = index / cols;
            int col = index % cols;
            for (Direction direction : directions) {
                int score = isEmbeddable(word, direction, row, col);
                if (score > bestScore) {
                    bestRow = row;
                    bestCol = col;
                    bestDirection = direction;
                    bestScore = score;
                }
            }
        }
        if (bestScore >= 0) {
            Word result = new Word(word, bestRow, bestCol, bestDirection, this);
            embedWord(result);

        }
    }

    private void embedWord(Word word) {
        int curRow = word.getY();
        int curCol = word.getX();
        final String wordStr = word.getText();
        final Direction direction = word.getDirection();
        for (int i = 0; i < wordStr.length(); i++) {
            char c = wordStr.charAt(i);

            board[curRow][curCol] = c;
            lock[curRow][curCol] = true;

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
        }

        solution.add(word);
    }

    private int isEmbeddable(String word, Direction direction, int row, int col) {
        if (getEmptySpace(direction, row, col) < word.length()) {
            return -1;
        }

        int score = 0;
        int curRow = row;
        int curCol = col;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (lock[curRow][curCol] && board[curRow][curCol] != c) {
                return -1;
            } else if (lock[curRow][curCol]) {
                score++;
            }

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

        }

        return score;
    }

    private int getEmptySpace(Direction direction, int row, int col) {
        switch (direction) {
            case SOUTH:
                return rows - row;
            case SOUTH_WEST:
                return Math.min(rows - row, col);
            case SOUTH_EAST:
                return Math.min(rows - row, cols - col);
            case WEST:
                return col;
            case EAST:
                return cols - col;
            case NORTH:
                return row;
            case NORTH_WEST:
                return Math.min(row, col);
            case NORTH_EAST:
                return Math.min(row, cols - col);
        }

        return 0;
    }

    private void clearBoard() {
        found_words.clear();
        solution.clear();
        grid.clear();

        for (int i = 0; i < lock.length; i++) {
            for (int j = 0; j < lock[i].length; j++) {
                lock[i][j] = false;
            }
        }

        char c;
        if(wordList != null && wordList.length > 0)
            c = wordList[0].charAt(0);
        else
            c = 'A';

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = c;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
