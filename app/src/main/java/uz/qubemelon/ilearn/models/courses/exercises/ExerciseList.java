package uz.qubemelon.ilearn.models.courses.exercises;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExerciseList implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("point")
    private int point;

    @SerializedName("time_limit")
    private int time_limit;

    @SerializedName("answer")
    private String answer;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPoint() {
        return point;
    }

    public int getTimeLimit() {
        return time_limit;
    }

    public String getAnswer() {
        return answer;
    }
}
