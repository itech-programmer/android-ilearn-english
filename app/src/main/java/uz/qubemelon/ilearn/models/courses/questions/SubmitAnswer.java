package uz.qubemelon.ilearn.models.courses.questions;

import com.google.gson.annotations.SerializedName;

public class SubmitAnswer {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("total_point")
    private String total_point;

    @SerializedName("right_answer")
    private RightAnswer right_answer;

    public boolean isSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }

    public String getTotalPoint(){
        return total_point;
    }

    public RightAnswer getRightAnswer(){
        return right_answer;
    }

}
