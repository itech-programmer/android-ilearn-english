package uz.qubemelon.ilearn.ui.activities.games.words;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import uz.qubemelon.ilearn.utilities.Settings;
import uz.qubemelon.ilearn.utilities.games.Constants;

public class WordsGameSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefsFragment fragment = new PrefsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        Settings.saveBooleanValue(WordsGameSettingsActivity.this, Constants.MADE_SETTINGS, true);
        super.onBackPressed();
    }
}
