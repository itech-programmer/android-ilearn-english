package uz.qubemelon.ilearn.ui.activities.games.words;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import uz.qubemelon.ilearn.R;

public class WordsGameInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();

        setContentView(R.layout.activity_info);
    }
}
