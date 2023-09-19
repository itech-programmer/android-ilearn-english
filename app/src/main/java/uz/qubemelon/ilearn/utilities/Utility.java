package uz.qubemelon.ilearn.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.models.courses.questions.Answers;

public class Utility {

    public static int TOTAL_POINT = 0;
    public static final String VOCABULARY_CHAPTER = "vocabulary_chapter";
    public static final String GRAMMAR_CHAPTER = "grammar_chapter";
    public static final String LESSON = "lesson";
    public static final String QUESTION = "question";
    public static final String EXERCISE = "exercise";
    public static int QUESTION_POINT = 0;
    public static String REQUEST_TYPE = "application/json";

    public static long get_milliseconds_from_minutes(int min) {
        return (min * 60) * 1000;
    }

    public static HashMap<String, Integer> get_time_from_millisecond(long millis) {

        /*make a placeholder hash map object*/
        HashMap<String, Integer> holder = new HashMap<>();

        /*get total days from the Milliseconds with TimeUnit Object */
        int days = (int) TimeUnit.MILLISECONDS.toDays(millis);
        /*minus the days from the total seconds*/
        millis -= TimeUnit.DAYS.toMillis(days);
        /*get total hours from the Milliseconds with TimeUnit Object */
        int hours = (int) TimeUnit.MILLISECONDS.toHours(millis);
        /*minus the hours from the total seconds*/
        millis -= TimeUnit.HOURS.toMillis(hours);
        /*get total minutes from the Milliseconds with TimeUnit Object */
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
        /*minus the minutes from the total seconds*/
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        /*get total seconds from the Milliseconds with TimeUnit Object */
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis);


        /*put the days, hours, minutes and seconds to the HashMap */
        holder.put("day", days);
        holder.put("hour", hours);
        holder.put("min", minutes);
        holder.put("sec", seconds);

        /*return the hashMap*/
        return holder;
    }

    public static String convert_un_capitalized(String str) {
        // Create a char array of given String
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {
                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    // Convert into Upper-case
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            }
            // If apart from first character
            // Any one is in Upper-case
            else if (ch[i] >= 'A' && ch[i] <= 'Z')
                // Convert into Lower-Case
                ch[i] = (char) (ch[i] + 'a' - 'A');
        }

        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;
    }

    public static boolean is_internet_available(Activity activity) {
        final ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            //    Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    /* show the dialog */
    public static ProgressDialog show_dialog(Activity activity) {
        assert activity != null;
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading...");
        dialog.show();
        return dialog;
    }

    /* dismiss the dialog */
    public static void dismiss_dialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static String get_formatted_date(String date) {
        String[] section = date.split(" ");
        return section[0] + " " + section[1];
    }

    /* this method disable shifting Animation of bottom navigation bar */
    @SuppressLint("RestrictedApi")
    public static void remove_shift_mode(BottomNavigationView view) {
        /* get first bottom navigation menu view from bottom navigation */
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            /*get field of bottom view from menu view */
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            /* set accessible true to Field */
            shiftingMode.setAccessible(true);
            /* set boolean to field with menu bar */
            shiftingMode.setBoolean(menuView, false);
            /* then set accessible false to field */
            shiftingMode.setAccessible(false);
            /* for every menu view in the bottom navigation set shifting mode false and set checked the item data is checked */
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                // set once again checked value, so view will be updated
                item.setChecked(Objects.requireNonNull(item.getItemData()).isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

    public static void play_wrong_music(Activity activity) {
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.music_wrong);
        mediaPlayer.start();

    }

    public static void play_right_music(Activity activity) {
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.song_correct);
        mediaPlayer.start();

    }

    public static void vibrate_phone(int time, Activity activity) {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 400 milliseconds
        if (v != null)
            v.vibrate(time);
    }

    public static List<Answers> remove_empty_fields(List<Answers> list) {
        List<Answers> keeper = new ArrayList<>();
        for (Answers answers : list) {
            if (answers.getAnswer().length() != 0) {
                keeper.add(answers);
            }
        }
        return keeper;
    }
}
