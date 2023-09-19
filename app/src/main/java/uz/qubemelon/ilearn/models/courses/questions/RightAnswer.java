package uz.qubemelon.ilearn.models.courses.questions;

import com.google.gson.annotations.SerializedName;

public class RightAnswer {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

}
