package uz.qubemelon.ilearn.models.courses.questions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QuestionList  implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("has_image")
    private int has_image;

    @SerializedName("image")
    private String image;

    @SerializedName("point")
    private int point;

    @SerializedName("time_limit")
    private int time_limit;

    @SerializedName("active_status")
    private int active_status;

    @SerializedName("answers")
    private List<Answers> answers;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getHasImage() {
        return has_image;
    }

    public String getImage() {
        return image;
    }

    public int getPoint() {
        return point;
    }

    public int getTimeLimit() {
        return time_limit;
    }

    public int isActiveStatus() {
        return active_status;
    }

    public List<Answers> getAnswers() {
        return answers;
    }
}
