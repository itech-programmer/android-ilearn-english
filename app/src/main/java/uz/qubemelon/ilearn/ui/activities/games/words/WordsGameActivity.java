package uz.qubemelon.ilearn.ui.activities.games.words;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.ui.activities.games.fives.FivesGameActivity;
import uz.qubemelon.ilearn.utilities.CircleButton;
import uz.qubemelon.ilearn.utilities.Settings;

public class WordsGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_words_home);

//        CircleButton btn_info = (CircleButton) findViewById(R.id.btn_info);
//        btn_info.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WordsGameActivity.this, WordsGameInfoActivity.class);
//                startActivity(intent);
//            }
//        });


        Button btn_play = findViewById(R.id.btn_play);
        Button btn_back = findViewById(R.id.btn_back);
        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordsGameActivity.this, GameBoardActivity.class);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(WordsGameActivity.this, HomeActivity.class));
                finish();
            }
        });


//        CircleButton btn_settings = (CircleButton) findViewById(R.id.btn_settings);
//        btn_settings.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WordsGameActivity.this, WordsGameSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
