package uz.qubemelon.ilearn.network;

import android.graphics.Bitmap;

import java.io.File;

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
    @POST("register")
    Call<String> do_registration(
            @Field("full_name") String full_name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("password_confirmation") String confirm_password
    );

    @FormUrlEncoded
    @POST("login")
    Call<String> do_login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("send-reset-password-code")
    Call<String> forgot_password(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("reset-password")
    Call<String> reset_password(
            @Field("access_code") String access_code,
            @Field("password") String password,
            @Field("password_confirmation") String confirm_password
    );

    @GET("user-settings")
    Call<String> get_user_setting(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("save-user-settings")
    Call<String> post_user_language(
            @Header("Authorization") String token,
            @Field("language") String language
    );

    @GET("profile")
    Call<String> get_profile(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("update-profile")
    Call<String> update_profile(
            @Header("Authorization") String token,
            @Field("full_name") String full_name,
            @Field("username") String username
    );

    @Multipart
    @POST("update-avatar")
    Call<String> update_avatar(
            @Header("Authorization") String token,
            @Part MultipartBody.Part avatar
    );

    @GET
    Call<String> add_user_to_firebase(@Url String url, @Header("Authorization") String token);


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
    Call<String> submit_question_answer(@Url String url, @Header("Authorization") String token, @Field("answer") int answer);

    @FormUrlEncoded
    @POST("submit-exercise-answer")
    Call<String> submit_exercise_answer(
            @Header("Authorization") String token,
            @Field("id") int answer_id,
            @Field("answer") String answer
    );

    @Multipart
    @POST("games/scores")
    Call<String> store_game_score(@Header("Authorization") String token, @Part("name") RequestBody name, @Part("score") RequestBody score);



}
