package uz.qubemelon.ilearn.utilities.games;

import android.app.Application;
import android.graphics.Typeface;

import java.util.List;

import uz.qubemelon.ilearn.R;

public class GameConfigs extends Application {

    // SET THE MAXIMUM TIME OF A ROUND
    public static float round_time = 15;

    // SET THE PROGRESS THAT WILL BE TAKEN OFF FROM THE CURRENT PROGRESS EACH TIME YOU GUESS A WORD (NOTE: it must never be equal or more than 100, otherwise it'll reset the progressBar)
    public static int bonus_progress = 10;

    // Set fonts
    public static Typeface june_gull;

    // Array of circle shapes from the drawable folder
    public static  int[] circles_array = new int[] {
            R.drawable.circle_corner_orange,
            R.drawable.circle_corner_blue,
            R.drawable.circle_corner_dark_purple,
            R.drawable.circle_corner_green,
            R.drawable.circle_corner_purple,
    };

    @Override
    public void onCreate() {
        super.onCreate();

        june_gull = Typeface.createFromAsset(getAssets(),"font/junegull.ttf");


    }
    // end onCreate()

    /*********** DO NOT EDIT THE CODE BELOW! *************/
    public static int score;
    public static List<String> string_array;
}
