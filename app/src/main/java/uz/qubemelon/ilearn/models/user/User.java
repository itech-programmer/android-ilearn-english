package uz.qubemelon.ilearn.models.user;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("profile_photo")
    private String profile_photo;

    @SerializedName("active_status")
    private int active_status;

    @SerializedName("language")
    private String language;

    public int getId() {
        return id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getProfilePhoto(){
        return profile_photo;
    }

    public int getActiveStatus(){
        return active_status;
    }

    public String getLanguage(){
        return language;
    }
}
