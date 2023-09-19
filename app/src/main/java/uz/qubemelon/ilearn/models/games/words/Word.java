package uz.qubemelon.ilearn.models.games.words;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import java.text.Collator;
import java.util.Locale;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.ui.activities.games.words.Direction;
import uz.qubemelon.ilearn.utilities.games.Constants;
import uz.qubemelon.ilearn.utilities.Settings;

public class Word implements Parcelable, Comparable<Word> {

    private int color;
    private String text;
    private int y, x;
    private Direction direction;
    private Context context;

    public Word(String word, int row, int col, Direction dir, Context context) {
        super();
        text = word;
        y = row;
        x = col;
        direction = dir;
        this.context = context;
    }

    public Word(Parcel in) {
        text = in.readString();
        y = in.readInt();
        x = in.readInt();
        direction = Direction.valueOf(in.readString());
        color = in.readInt();
    }

    public int compareTo(Word another) {
        Locale locale;
        try {
            locale = new Locale(Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE));
        }catch (Exception e){
            locale = Locale.ENGLISH;
        }
        Collator collator = Collator.getInstance(locale);
        return collator.compare(text.toLowerCase(locale), another.getText().toLowerCase(locale));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(y);
        dest.writeInt(x);
        dest.writeString(direction.name());
        dest.writeInt(color);
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public int getColor() {
        return color;
    }

    public void setColor(int col) {
        color = col;
    }

    public String getText() {
        return text;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;

        String otherAsReverse = new StringBuilder(other.text).reverse().toString();

        if (this.text.equals(otherAsReverse))
            return true;

        boolean substringAllowed = Settings.getBooleanValue(context, context.getResources().getString(R.string.pref_allow_substring), false);

        if(!substringAllowed){
            if(this.text.indexOf(other.text) > -1)
                return true;

            if(other.text.indexOf(this.text) > -1)
                return true;

            if(this.text.indexOf(otherAsReverse) > -1)
                return true;

            if(other.text.indexOf(new StringBuilder(this.text).reverse().toString()) > -1)
                return true;
        }

        if (x != other.x)
            return false;
        if (direction != other.direction)
            return false;
        if (y != other.y)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }
}
