package uz.qubemelon.ilearn.ui.activities.games.words;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import uz.qubemelon.ilearn.models.games.words.Word;

public class GameStatus extends View.BaseSavedState {

    public Set<Word> found_words;

    GameStatus() {
        super(Parcel.obtain());
        found_words = new HashSet<>();
    }

    private GameStatus(Parcel in) {
        super(in);
        found_words = new HashSet<>();
        Parcelable[] parcels = in.readParcelableArray(Word.class.getClassLoader());
        for (Parcelable parcel : parcels) {
            found_words.add((Word) parcel);
        }

    }

    GameStatus(Parcelable parcelable) {
        super(parcelable);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelableArray(found_words.toArray(new Word[]{}), flags);
    }

    public static final Creator<GameStatus> CREATOR =  new Creator<GameStatus>() {

        public GameStatus createFromParcel(Parcel source) {
            return new GameStatus(source);
        }

        public GameStatus[] newArray(int size) {
            return new GameStatus[size];
        }
    };

}
