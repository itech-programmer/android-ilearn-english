package uz.qubemelon.ilearn.models.courses.questions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Answers implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("answer")
    private String answer;

    @SerializedName("answer_type")
    private int answer_type;

    public int getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public int getAnswerType() {
        return answer_type;
    }
}
