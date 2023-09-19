package uz.qubemelon.ilearn.models.dictionaries;

import com.google.gson.annotations.SerializedName;

public class DictionaryList {

    @SerializedName("id")
    private int id;

    @SerializedName("en_word")
    private String en_word;

    @SerializedName("en_definition")
    private String en_definition;

    @SerializedName("uz_word")
    private String uz_word;

    @SerializedName("uz_definition")
    private String uz_definition;

    @SerializedName("qr_word")
    private String qr_word;

    @SerializedName("qr_definition")
    private String qr_definition;

    @SerializedName("ru_word")
    private String ru_word;

    @SerializedName("ru_definition")
    private String ru_definition;

    public int getId() {
        return id;
    }

    public String getEnWord() {
        return en_word;
    }

    public String getEnDefinition() {
        return en_definition;
    }

    public String getUzWord() {
        return uz_word;
    }

    public String getUzDefinition() {
        return uz_definition;
    }

    public String getQrWord() {
        return qr_word;
    }

    public String getQrDefinition() {
        return qr_definition;
    }

    public String getRuWord() {
        return ru_word;
    }

    public String getRuDefinition() {
        return ru_definition;
    }
}
