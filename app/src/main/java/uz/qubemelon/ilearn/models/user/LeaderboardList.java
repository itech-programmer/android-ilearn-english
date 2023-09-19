package uz.qubemelon.ilearn.models.user;

import com.google.gson.annotations.SerializedName;

public class LeaderboardList {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("user_rank")
    private int user_rank;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("user_score")
    private String user_score;

    public int getUserId() {
        return user_id;
    }

    public int getUserRank() {
        return user_rank;
    }

    public String getFullName() {
        return full_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserScore() {
        return user_score;
    }
}
