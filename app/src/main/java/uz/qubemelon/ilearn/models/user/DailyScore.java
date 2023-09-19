package uz.qubemelon.ilearn.models.user;

import com.google.gson.annotations.SerializedName;

public class DailyScore {

    @SerializedName("date")
    private String date;

    @SerializedName("score")
    private String score;

    @SerializedName("total_score")
    private String total_score;

    @SerializedName("score_percentage")
    private double score_percentage;

    public String getDate() {
        return date;
    }

    public String getScore() {
        return score;
    }

    public String getTotalScore() {
        return total_score;
    }

    public double getScorePercentage() {
        return score_percentage;
    }
}
