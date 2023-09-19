package uz.qubemelon.ilearn.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    /* those are constants urls we used in this project */
//    private static final String BASE_URL = "http://qubemelonacademy.uz/api/";
    private static final String BASE_URL = "http://ilearnenglish.uz/api/";
    public static final String USERS = BASE_URL + "users";
    public static final String GAMES = BASE_URL + "games";
    public static final String LEADERBOARD_URL = "leader-board";
    public static final String LESSONS_URL = BASE_URL + "chapter";
    public static final String LESSON_VIEW_URL = BASE_URL + "lesson";
    public static final String QUESTIONS_URL = "questions";
    public static final String EXERCISES_URL = "exercises";
    public static final String SUBMIT_QUESTION_ANSWER_ULR = BASE_URL + "submit-question-answer";
    public static final String SUBMIT_EXERCISE_ANSWER_ULR = BASE_URL + "submit-exercise-answer";
    public static final String FIVES = "fives";
    public static final String FIREBASE_ENDPOINT = BASE_URL + "set-user-device-id";
    private static Retrofit retrofit = null;

    /* here we made a global */
    public static Retrofit get_retrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
