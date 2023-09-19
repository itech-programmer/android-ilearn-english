package uz.qubemelon.ilearn.ui.activities.games.fives;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.utilities.games.GameConfigs;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    /* all global field instances are here */
    private TextView text_possible_word, text_game_score, text_word_count;
    private Button btn_go_back, btn_play_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fives_over);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Hide Status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Init Views
        init_views();

        // Show score
        text_game_score.setText(String.valueOf(GameConfigs.score));

        // Get the word you could have made
        text_possible_word.setText(GameConfigs.string_array.get(0));

        // Get the other words you could have made
        if (GameConfigs.string_array.size() > 1) {
            text_word_count.setText( String.valueOf(GameConfigs.string_array.size()-1) );
        } else {
            text_word_count.setText("0");
        }

        // MARK: - PLAY AGAIN BUTTON ------------------------------------
        btn_play_again.setTypeface(GameConfigs.june_gull);

    }// end onCreate()

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_play_again:
                finish();
                break;
            case R.id.btn_go_back:
                startActivity(new Intent(GameOverActivity.this, FivesGameActivity.class));
                break;
        }
    }

    private void init_views() {

        text_possible_word = findViewById(R.id.text_possible_word);
        text_game_score = findViewById(R.id.text_game_score);
        text_word_count = findViewById(R.id.text_word_count);
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(this);
        btn_play_again = findViewById(R.id.btn_play_again);
        btn_play_again.setOnClickListener(this);
    }

}// @end
