package uz.qubemelon.ilearn.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST("login")
    Call<String> doLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("dictionaries")
    Call<String> get_dictionaries_list(@Header("Authorization") String token, @Header("Accept") String type);

    @GET
    Call<String> get_leaderboard_list(@Url String url, @Header("Authorization") String token);

    @GET()
    Call<String> get_fives_best_score(@Url String url, @Header("Authorization") String token, @Header("Accept") String type);

    @GET("vocabulary/chapters")
    Call<String> get_vocabulary_chapters_list(@Header("Authorization") String token, @Header("Accept") String type);

    @GET("grammar/chapters")
    Call<String> get_grammar_chapters_list(@Header("Authorization") String token, @Header("Accept") String type);

    @GET
    Call<String> get_lessons_list(@Url String url, @Header("Authorization") String token);

    @GET
    Call<String> get_lesson(@Url String url, @Header("Authorization") String token);

    @GET
    Call<String> get_questions_list(@Url String url, @Header("Authorization") String token);

    @FormUrlEncoded
    @POST
    Call<String> submit_answer(@Url String url, @Header("Authorization") String token, @Field("answer") int answer);

    @Multipart
    @POST("games/scores")
    Call<String> store_game_score(@Header("Authorization") String token, @Part("name") RequestBody name, @Part("score") RequestBody score);

    @GET("profile")
    Call<String> get_profile(@Header("Authorization") String token);


}
