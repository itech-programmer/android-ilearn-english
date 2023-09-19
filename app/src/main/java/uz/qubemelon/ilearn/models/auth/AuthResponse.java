package uz.qubemelon.ilearn.models.auth;

import com.google.gson.annotations.SerializedName;

import uz.qubemelon.ilearn.models.User;

public class AuthResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("access_type")
    private String access_type;

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("total_point")
    private String total_point;

    @SerializedName("user")
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessType() {
        return access_type;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getTotalPoint() {
        return total_point;
    }

    public User getUser() {
        return user;
    }
}
