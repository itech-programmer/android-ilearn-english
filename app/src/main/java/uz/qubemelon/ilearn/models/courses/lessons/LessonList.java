package uz.qubemelon.ilearn.models.courses.lessons;

import com.google.gson.annotations.SerializedName;

public class LessonList {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("chapter_title")
    private String chapter_title;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getChapterTitle() {
        return chapter_title;
    }
}
