package uz.qubemelon.ilearn.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profile {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("ranking")
    private int ranking;

    @SerializedName("point")
    private String point;

    @SerializedName("participated_questions")
    private int participated_questions;

    @SerializedName("daily_score")
    private List<DailyScore> daily_score;


    public int getUserId() {
        return user_id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getRanking() {
        return ranking;
    }

    public String getPoint() {
        return point;
    }

    public int getParticipatedQuestions() {
        return participated_questions;
    }

    public List<DailyScore> getDailyScore(){
        return daily_score;
    }
}
