package uz.qubemelon.ilearn.ui.activities.games.fives;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.utilities.Utility;
import uz.qubemelon.ilearn.utilities.games.GameConfigs;

public class GameBoardActivity extends AppCompatActivity implements View.OnClickListener {

    /* all global field instances are here */
    private AppCompatActivity activity;
    private TextView text_game_points, text_first_letter, text_second_letter, text_third_letter, text_fourth_letter, text_fives_letter, text_letter;
    private ProgressBar game_progressbar;
    private Button btn_game_back, btn_game_reset, btn_first_letter, btn_second_letter, btn_third_letter, btn_fourth_letter, btn_fives_letter, btn_letter;
    private Timer game_timer;
    private float progress;
    private List<String> words_array;
    private List<String> char_array;
    private String word_string = "";
    private int taps_count = 0;
    private String first_word = "";
    private String second_word = "";
    private String third_word = "";
    private String word_by_characters = "";
    private int random_circle = 0;
    private Button [] btn_letters;
    private TextView [] text_view_letters;
    private MediaPlayer media_player;
    private Storage game_storage = new Storage(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fives_board);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Hide Status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get a List array of words
        String [] words_arr = getResources().getStringArray(R.array.words);
        words_array = new ArrayList<String>(Arrays.asList(words_arr));

        init_views();

        // Make an array of letter buttons
        btn_letters = new Button[5];
        btn_letters[0] = btn_first_letter;
        btn_letters[1] = btn_second_letter;
        btn_letters[2] = btn_third_letter;
        btn_letters[3] = btn_fourth_letter;
        btn_letters[4] = btn_fives_letter;


        // Make an array of letters on the top
        text_view_letters = new TextView[5];
        text_view_letters[0] = text_first_letter;
        text_view_letters[1] = text_second_letter;
        text_view_letters[2] = text_third_letter;
        text_view_letters[3] = text_fourth_letter;
        text_view_letters[4] = text_fives_letter;

        // MARK: - RESET BUTTON ------------------------------------
        btn_game_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_word();
                // Play a sound
                play_sound("reset_word.mp3");
            }
        });

        // MARK: - BACK BUTTON ------------------------------------
        btn_game_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game_timer.cancel();
                finish();
            }
        });
    }

    // ON START() ------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        // Reset score
        GameConfigs.score = 0;
        text_game_points.setText(String.valueOf(GameConfigs.score));

        // Set progressBar and start the gameTimer
        progress = 0;
        game_progressbar.setProgress((int) progress);
        start_game_timer();


        // Get a random circle for letters
        Random random = new Random();
        random_circle = random.nextInt(GameConfigs.circles_array.length);

        // Reset taps count
        taps_count = -1;

        // Get a random word from words string-array
        get_random_word();
    }

    // MARK: - RESET LETTER BUTTONS ------------------------------------------------------
    void reset_letter_buttons() {

        for (int i = 0; i<5; i++) {
            btn_letters[i].setEnabled(true);
            btn_letters[i].setBackgroundResource(GameConfigs.circles_array[random_circle]);
            btn_letters[i].setTextColor(Color.parseColor("#ffffff"));
        }

        // Reset letters textViews on the top
        reset_letters_text_view();
    }

    // MARK: - RESET LETTERS ON THE TOP ------------------------------------------------------
    void reset_letters_text_view() {
        for (int i = 0; i<5; i++) {
            text_view_letters[i].setText("");
            text_view_letters[i].setBackgroundResource(R.drawable.circle_corner_white);
        }
    }

    // MARK: - GET A RANDOM WORD ------------------------------------------------------------
    void get_random_word() {

        // Get a random circle for letters
        Random random = new Random();
        random_circle = random.nextInt(GameConfigs.circles_array.length);

        // Get a random word from the string-arrays
        String random_word = words_array.get(new Random().nextInt(words_array.size()));
        word_string = random_word;

        // Get an array of words (if there are multiple words
        GameConfigs.string_array = new ArrayList<String>();

        if (word_string.contains(".")) {
            String[] one = word_string.split(Pattern.quote("."));
            for (String word : one) { GameConfigs.string_array.add(word); }
        } else {
            GameConfigs.string_array.add(word_string);
        }

        // Get the complete word as a List of characters
        char_array = new ArrayList<String>();
        String[] chArr = word_string.split("");
        for(int i=0; i<6; i++) {
            String c = chArr[i];
            char_array.add(c);
        }
        char_array.remove(0);

        // Get Random characters function
        get_random_char();
    }

    // MARK: - GET RANDOM CHARACTERS --------------------------------------------------------
    void get_random_char() {

        // Get a random combination that displays characters on the Game Board
        Random random = new Random();
        int randomCombination = random.nextInt(3);
        // Log.i("log-", "COMBINATION: " + randomCombination);


        switch (randomCombination) {
            case 0:
                btn_letters[1].setText(char_array.get(0));
                btn_letters[0].setText(char_array.get(1));
                btn_letters[4].setText(char_array.get(2));
                btn_letters[2].setText(char_array.get(3));
                btn_letters[3].setText(char_array.get(4));
                break;

            case 1:
                btn_letters[3].setText(char_array.get(0));
                btn_letters[0].setText(char_array.get(1));
                btn_letters[4].setText(char_array.get(2));
                btn_letters[1].setText(char_array.get(3));
                btn_letters[2].setText(char_array.get(4));
                break;

            case 2:
                btn_letters[4].setText(char_array.get(0));
                btn_letters[1].setText(char_array.get(1));
                btn_letters[0].setText(char_array.get(2));
                btn_letters[3].setText(char_array.get(3));
                btn_letters[2].setText(char_array.get(4));
                break;
        }

        // Call reset Word function
        reset_word();

    }

    // MARK: - RESET WORDS BUTTONS --------------------------------------------------------
    void reset_word() {
        // Reset tap Counts
        taps_count = -1;

        // reset wordByCharacters
        word_by_characters = "";

        // Reset letter Buttons
        reset_letter_buttons();

        // Reset top Letters
        reset_letters_text_view();
    }

    // MARK: - START GAME TIMER ---------------------------------------------------------------
    void start_game_timer() {
        float delay = 10 * GameConfigs.round_time;

        game_timer =  new Timer();
        game_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress = progress + 10 / GameConfigs.round_time;
                        game_progressbar.setProgress((int) progress);
                        // Log.i("log-", "PROGRESS: " + progress);

                        // TIME ENDED, GAME OVER!
                        if (progress >= 100) {
                            game_timer.cancel();

                            game_over();
                        }
                    }
                });
            }
        }, (int)delay, (int)delay);

    }

    // UPDATE GAME TIMER ------------------------------------------------
    void update_timer() {
        game_timer.cancel();
        game_progressbar.setProgress((int) progress);
        start_game_timer();
    }

    @Override
    public void onClick(View view) {
        btn_letter = findViewById(view.getId());
        taps_count = taps_count + 1;

        // Set letter
        text_view_letters[taps_count].setText(btn_letter.getText().toString() );
        text_view_letters[taps_count].setBackgroundResource(GameConfigs.circles_array[random_circle]);

        // Change buttons status
        btn_letter.setBackgroundResource(R.drawable.circle_corner_white);
        btn_letter.setTextColor(Color.parseColor("#999999"));
        btn_letter.setEnabled(false);

        // Create a string by shown characters
        word_by_characters = word_by_characters + text_view_letters[taps_count].getText().toString();

        // You've tapped all buttons, so check your result
        if (taps_count == 4) { check_result(); }

        // Play a sound
        play_sound("btn_tapped.mp3");
    }

    // MARK: - CHECK RESULT ------------------------------------------------------------
    void check_result() {

        // YOU'VE GUESSED THE WORD!
        first_word = GameConfigs.string_array.get(0);

        if (GameConfigs.string_array.size() == 2) {
            second_word = GameConfigs.string_array.get(1);
        }
        if (GameConfigs.string_array.size() == 3) {
            second_word = GameConfigs.string_array.get(1);
            third_word = GameConfigs.string_array.get(2);
        }

        if (word_by_characters.matches(first_word) ||
                word_by_characters.matches(second_word) ||
                word_by_characters.matches(third_word) ) {

            // Play a sound
            play_sound("right_word.mp3");

            // Update game timer
            progress = progress - GameConfigs.bonus_progress;
            update_timer();

            // Update Score
            GameConfigs.score = GameConfigs.score + 10;

            text_game_points.setText(String.valueOf(GameConfigs.score));

            // Get a new random word
            get_random_word();

            // WORD IS WRONG
        } else {
            word_by_characters = "";
            get_random_char();

            // Play a sound
            play_sound("reset_word.mp3");
        }
    }

    // MARK: - GAME OVER ------------------------------------------------------------
    void game_over() {

        String game_name = "fives_game_score";
        int best_score = game_storage.get_fives_game_score();
        int text_point = Integer.parseInt(String.valueOf(text_game_points.getText()));
        // Save Best Score
        if (best_score <= text_point) {
            store_game_score(game_name, text_point);
        }


        // Play a sound
        play_sound("game_over.mp3");

        // Go to Game Over Activity
        startActivity(new Intent(GameBoardActivity.this, GameOverActivity.class));
    }

    private void store_game_score(String game_name, int game_score){
        ProgressDialog dialog = Utility.show_dialog(this);
        Storage storage = new Storage(this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        RequestBody name = RequestBody.create(game_name, MultipartBody.FORM);
        RequestBody score = RequestBody.create(String.valueOf(game_score), MultipartBody.FORM);
        Call<String> store_score = retrofit_interface.store_game_score(storage.get_access_token(), name, score);
        store_score.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);
                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {

                            /* serialize the String response */
                            String success_message = jsonObject.getString("message");

                            Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_SHORT).show();

                            Utility.dismiss_dialog(dialog);

                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /*get all the error messages and show to the user*/
                            JSONArray messageArray = jsonObject.getJSONArray("message");
                            Toast.makeText(activity, messageArray.getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    /* dismiss the dialog */
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(getApplicationContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /* handle network error and notify the user */
                if (throwable instanceof IOException) {
                    Toast.makeText(getApplicationContext(), R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // MARK: - PLAY SOUND --------------------------------------------------------
    void play_sound(String sound_name) {
        try {

            MediaPlayer media_player = new MediaPlayer();
            AssetFileDescriptor asset_file_descriptor = getAssets().openFd("sounds/" + sound_name);
            media_player.setDataSource(asset_file_descriptor.getFileDescriptor(), asset_file_descriptor.getStartOffset(), asset_file_descriptor.getLength());

            media_player.prepare();
            media_player.setVolume(1f, 1f);
            media_player.setLooping(false);
            media_player.start();

            media_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer media_player) {
                    media_player.release();
                }});

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game_timer.cancel();
    }

    private void init_views() {
        btn_game_back = findViewById(R.id.btn_game_back);
        btn_game_reset = findViewById(R.id.btn_game_reset);
        game_progressbar = findViewById(R.id.game_progressbar);
        text_game_points = findViewById(R.id.text_game_points);
        text_game_points.setTypeface(GameConfigs.june_gull);

        text_first_letter = (TextView)findViewById(R.id.text_first_letter);
        text_first_letter.setTypeface(GameConfigs.june_gull);
        text_second_letter = (TextView)findViewById(R.id.text_second_letter);
        text_second_letter.setTypeface(GameConfigs.june_gull);
        text_third_letter = (TextView)findViewById(R.id.text_third_letter);
        text_third_letter.setTypeface(GameConfigs.june_gull);
        text_fourth_letter = (TextView)findViewById(R.id.text_fourth_letter);
        text_fourth_letter.setTypeface(GameConfigs.june_gull);
        text_fives_letter = (TextView)findViewById(R.id.text_fives_letter);
        text_fives_letter.setTypeface(GameConfigs.june_gull);

        btn_first_letter = (Button)findViewById(R.id.btn_first_letter);
        btn_first_letter.setTypeface(GameConfigs.june_gull);
        btn_first_letter.setOnClickListener(this);
        btn_second_letter = (Button)findViewById(R.id.btn_second_letter);
        btn_second_letter.setTypeface(GameConfigs.june_gull);
        btn_second_letter.setOnClickListener(this);
        btn_third_letter = (Button)findViewById(R.id.btn_third_letter);
        btn_third_letter.setTypeface(GameConfigs.june_gull);
        btn_third_letter.setOnClickListener(this);
        btn_fourth_letter = (Button)findViewById(R.id.btn_fourth_letter);
        btn_fourth_letter.setTypeface(GameConfigs.june_gull);
        btn_fourth_letter.setOnClickListener(this);
        btn_fives_letter = (Button)findViewById(R.id.btn_fives_letter);
        btn_fives_letter.setTypeface(GameConfigs.june_gull);
        btn_fives_letter.setOnClickListener(this);
    }
}
