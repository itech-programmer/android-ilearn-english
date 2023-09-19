package uz.qubemelon.ilearn.models.courses.chapters;

import com.google.gson.annotations.SerializedName;

public class GrammarChapterList {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
}
