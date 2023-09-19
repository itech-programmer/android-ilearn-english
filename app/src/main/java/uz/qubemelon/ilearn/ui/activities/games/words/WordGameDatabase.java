package uz.qubemelon.ilearn.ui.activities.games.words;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.utilities.Settings;

public class WordGameDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "wordsearch.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private String selectedLangTable;
    private SQLiteDatabase database;

    public WordGameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        this.context = context;
    }

    public void open(){
        database = getReadableDatabase();
        selectedLangTable = Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), null);
    }

    public String[] getRandomWords(int count){
        Cursor cursor = database.query(selectedLangTable+" Order BY RANDOM() LIMIT " + count, new String[] { "*" }, null, null, null, null, null);
        cursor.moveToFirst();
        int len = cursor.getCount();
        String[] words = new String[len];
        int i = 0;
        while (!cursor.isAfterLast()) {
            words[i++] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return words;
    }
}
