package uz.qubemelon.ilearn.ui.activities.games.words;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import uz.qubemelon.ilearn.models.games.words.Word;

public class WordGameStatus extends View.BaseSavedState {

    public Set<Word> found_words;

    WordGameStatus() {
        super(Parcel.obtain());
        found_words = new HashSet<>();
    }

    private WordGameStatus(Parcel in) {
        super(in);
        found_words = new HashSet<>();
        Parcelable[] parcels = in.readParcelableArray(Word.class.getClassLoader());
        for (Parcelable parcel : parcels) {
            found_words.add((Word) parcel);
        }
    }

    WordGameStatus(Parcelable parcelable) {
        super(parcelable);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelableArray(found_words.toArray(new Word[]{}), flags);
    }

    public static final Creator<WordGameStatus> CREATOR =  new Creator<WordGameStatus>() {

        public WordGameStatus createFromParcel(Parcel source) {
            return new WordGameStatus(source);
        }

        public WordGameStatus[] newArray(int size) {
            return new WordGameStatus[size];
        }
    };

}
