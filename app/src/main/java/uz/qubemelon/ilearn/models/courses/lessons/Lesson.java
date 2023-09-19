package uz.qubemelon.ilearn.models.courses.lessons;

import com.google.gson.annotations.SerializedName;

public class Lesson {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("teacher")
    private String teacher;

    @SerializedName("content")
    private String content;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getContent() {
        return content;
    }
}
