package uz.qubemelon.ilearn.adapters.games;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.models.games.words.Word;
import uz.qubemelon.ilearn.utilities.Settings;
import uz.qubemelon.ilearn.utilities.games.Constants;

public class WordTableAdapter extends BaseAdapter implements ListAdapter {

    private Locale locale;
    private Set<Word> correct_words;
    private Context context;
    private List<Word> words;

    public WordTableAdapter(Context context, List<Word> word_list) {
        context = context;
        words = word_list;
        correct_words = new HashSet<>();
        String localeCode = Settings.getStringValue(context, context.getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE);
        locale = new Locale(localeCode);
    }

    public int getCount() {
        return words.size();

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void setWordsFound(Set<Word> words) {
        correct_words.addAll(words);
    }

    public void setWordFound(Word word) {
        correct_words.add(word);
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return words.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.word_item, null);
        }

        Word word = (Word) getItem(position);
        TextView text_word_game = (TextView) view.findViewById(R.id.text_word_game);
        view.setFocusable(false);
        text_word_game.setFocusable(false);
        String wordStr = word.getText().toLowerCase(locale);
        wordStr = wordStr.substring(0, 1).toUpperCase(locale) + wordStr.substring(1);
        text_word_game.setText(wordStr);
        if (!correct_words.contains(word)) {
            text_word_game.setTypeface(Typeface.DEFAULT_BOLD);
            text_word_game.setPaintFlags(text_word_game.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            text_word_game.setTypeface(Typeface.DEFAULT);
            text_word_game.setPaintFlags(text_word_game.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return view;
    }
}
