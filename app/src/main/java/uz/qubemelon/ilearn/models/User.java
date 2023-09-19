package uz.qubemelon.ilearn.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("profile_photo")
    private String profile_photo;

    @SerializedName("email")
    private String email;

    @SerializedName("active_status")
    private int active_status;

    public int getId() {
        return id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getProfilePhoto(){
        return profile_photo;
    }

    public String getEmail(){
        return email;
    }

    public int getActiveStatus(){
        return active_status;
    }

}
