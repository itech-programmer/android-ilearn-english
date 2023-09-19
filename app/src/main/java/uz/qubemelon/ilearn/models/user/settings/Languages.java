package uz.qubemelon.ilearn.models.user.settings;

import com.google.gson.annotations.SerializedName;

public class Languages {

    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
