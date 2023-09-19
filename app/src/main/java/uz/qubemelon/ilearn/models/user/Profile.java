package uz.qubemelon.ilearn.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profile {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("username")
    private String username;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("total_point")
    private String total_point;

    @SerializedName("ranking")
    private int ranking;

    @SerializedName("participated_questions")
    private int participated_questions;

    @SerializedName("daily_score")
    private List<DailyScore> daily_score;

    @SerializedName("language")
    private String language;

    public int getUserId() {
        return user_id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTotalPoint() {
        return total_point;
    }

    public int getRanking() {
        return ranking;
    }

    public int getParticipatedQuestions() {
        return participated_questions;
    }

    public List<DailyScore> getDailyScore(){
        return daily_score;
    }

    public String getLanguage(){
        return language;
    }
}
