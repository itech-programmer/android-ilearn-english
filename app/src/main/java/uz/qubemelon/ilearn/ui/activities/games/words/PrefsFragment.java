package uz.qubemelon.ilearn.ui.activities.games.words;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import uz.qubemelon.ilearn.R;

public class PrefsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.prefs);

	}


}