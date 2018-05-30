package jp.co.asaichi.pubrepo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GoogleServiceModel implements Serializable {

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("data")
    @Expose
    private List<GoogleService> data = null;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<GoogleService> getData() {
        return data;
    }

    public void setData(List<GoogleService> data) {
        this.data = data;
    }

}
