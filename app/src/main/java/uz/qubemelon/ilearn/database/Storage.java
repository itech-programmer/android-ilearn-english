package uz.qubemelon.ilearn.database;

import android.content.Context;
import android.content.SharedPreferences;

import uz.qubemelon.ilearn.R;

public class Storage {

    private Context context;
    private static final boolean IS_SOUND_ENABLE = true;
    private static final boolean IS_APP_FIRST_TIME = false;
    private static final boolean LOGIN_SATE = false;
    private static final int USER_ID = 1;
    private static final String FULL_NAME = null;
    private static final String LANGUAGE = null;
    private static final String LANGUAGES_RESPONSE = null;
    private static final String DICTIONARY_RESPONSE = null;
    private static final String LEADERBOARD_RESPONSE = null;
    private static final String VOCABULARY_CHAPTER_RESPONSE = null;
    private static final String GRAMMAR_CHAPTER_RESPONSE = null;
    private static final String VOCABULARY_LESSON_RESPONSE = null;
    private static final String GRAMMAR_LESSON_RESPONSE = null;

    private static final int CHAPTER_ID = 1;
    private static final int LESSON_ID = 1;

    private static final String FIREBASE_TOKEN = null;
    private static final String ACCESS_TYPE = "Bearer";
    private static final String ACCESS_TOKEN = null;

    private static final int USER_TOTAL_POINT = 0;
    private static final int FIVES_SCORE = 0;

    public Storage(Context context) {
        this.context = context;
    }

    private SharedPreferences.Editor get_editor_preferences() {
        return get_shared_preferences().edit();
    }

    private SharedPreferences get_shared_preferences() {
        return context.getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void save_access_type(String access_type) {
        get_editor_preferences().putString("access_type", access_type).commit();
    }

    public String get_access_type() {
        return get_shared_preferences().getString("access_type", ACCESS_TYPE);
    }

    public void save_access_token(String access_token) {
        get_editor_preferences().putString("access_token", access_token).commit();
    }

    public String get_access_token() {
        return get_access_type() + " " + get_shared_preferences().getString("access_token", ACCESS_TOKEN);
    }

    public void save_sign_in_sate(boolean sign_in_state) {
        get_editor_preferences().putBoolean("logged_in", sign_in_state).commit();
    }

    public boolean get_sign_in_state() {
        return get_shared_preferences().getBoolean("logged_in", LOGIN_SATE);
    }

    public void save_firebase_token(String firebase_token) {
        get_editor_preferences().putString("firebase_token", firebase_token).commit();
    }

    public String get_firebase_token() {
        return get_shared_preferences().getString("firebase_token", FIREBASE_TOKEN);
    }

    public void save_languages_response(String languages_response) {
        get_editor_preferences().putString("languages_response", languages_response).commit();
    }

    public String get_languages_response() {
        return get_shared_preferences().getString("languages_response", LANGUAGES_RESPONSE);
    }

    public void save_is_first_time(boolean first_time_auth) {
        get_editor_preferences().putBoolean("first_time", first_time_auth).commit();
    }

    public boolean get_is_first_time() {
        return get_shared_preferences().getBoolean("first_time", IS_APP_FIRST_TIME);
    }

    public void save_user_id(int i) {
        get_editor_preferences().putInt("user_id", i).commit();
    }

    public int get_user_id() {
        return get_shared_preferences().getInt("user_id", USER_ID);
    }

    public void save_full_name(String full_name) {
        get_editor_preferences().putString("full_name", full_name).commit();
    }

    public String get_full_name() {
        return get_shared_preferences().getString("full_name", FULL_NAME);
    }

    public void save_user_language(String language) {
        get_editor_preferences().putString("language", language).commit();
    }

    public String get_user_language() {
        return get_shared_preferences().getString("language", LANGUAGE);
    }

    public void save_dictionaries_response(String dictionary_response) {
        get_editor_preferences().putString("dictionary_response", dictionary_response).commit();
    }

    public String get_dictionaries_response() {
        return get_shared_preferences().getString("dictionary_response", DICTIONARY_RESPONSE);
    }

    public void save_leaderboard_response(String leaderboard_response) {
        get_editor_preferences().putString("leaderboard_response", leaderboard_response).commit();
    }

    public String get_leaderboard_response() {
        return get_shared_preferences().getString("leaderboard_response", LEADERBOARD_RESPONSE);

    }

    public void save_fives_game_score(int fives_score) {
        get_editor_preferences().putInt("fives_score", fives_score).commit();
    }

    public int get_fives_game_score() {
        return get_shared_preferences().getInt("fives_score", FIVES_SCORE);
    }

    public void save_user_total_point(int total_point) {
        get_editor_preferences().putInt("total_point", total_point).commit();
    }

    public int get_user_total_point() {
        return get_shared_preferences().getInt("total_point", USER_TOTAL_POINT);
    }

    public void save_user_full_name(String full_name) {
        get_editor_preferences().putString("full_name", full_name).commit();
    }

    public String get_user_full_name() {
        return get_shared_preferences().getString("full_name", FULL_NAME);
    }

    public void save_vocabulary_chapters_response(String vocabulary_chapter_response) {
        get_editor_preferences().putString("vocabulary_chapter_response", vocabulary_chapter_response).commit();
    }

    public String get_vocabulary_chapters_response() {
        return get_shared_preferences().getString("vocabulary_chapter_response", VOCABULARY_CHAPTER_RESPONSE);
    }

    public void save_vocabulary_lessons_response(String vocabulary_lessons_response) {
        get_editor_preferences().putString("vocabulary_lessons_response", vocabulary_lessons_response).commit();
    }

    public String get_vocabulary_lessons_response() {
        return get_shared_preferences().getString("vocabulary_lessons_response", VOCABULARY_LESSON_RESPONSE);
    }

    public void save_grammar_chapters_response(String grammar_chapter_response) {
        get_editor_preferences().putString("grammar_chapter_response", grammar_chapter_response).commit();
    }

    public String get_grammar_chapters_response() {
        return get_shared_preferences().getString("grammar_chapter_response", GRAMMAR_CHAPTER_RESPONSE);
    }

    public void save_chapter_id(int chapter_id) {
        get_editor_preferences().putInt("chapter_id", chapter_id).commit();
    }

    public int get_chapter_id() {
        return get_shared_preferences().getInt("chapter_id", CHAPTER_ID);
    }

    public void save_grammar_lessons_response(String grammar_lessons_response) {
        get_editor_preferences().putString("grammar_lessons_response", grammar_lessons_response).commit();
    }

    public String get_grammar_lessons_response() {
        return get_shared_preferences().getString("grammar_lessons_response", GRAMMAR_LESSON_RESPONSE);
    }

    public void save_lesson_id(int lesson_id) {
        get_editor_preferences().putInt("lesson_id", lesson_id).commit();
    }

    public int get_lesson_id() {
        return get_shared_preferences().getInt("lesson_id", LESSON_ID);
    }

    public void save_sound_state(boolean p) {
        get_editor_preferences().putBoolean("sound", p).commit();
    }

    public boolean get_sound_state() {
        return get_shared_preferences().getBoolean("sound", IS_SOUND_ENABLE);
    }
}
