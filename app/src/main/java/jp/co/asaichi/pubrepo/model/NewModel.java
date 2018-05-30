package jp.co.asaichi.pubrepo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nguyentu on 12/21/17.
 */

public class NewModel {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("publish_date")
    @Expose
    private Long publish_date;
    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("created_timestamp")
    @Expose
    private Long created_timestamp;
    @SerializedName("deleted_flg")
    @Expose
    private Long deleted_flg;

    @SuppressWarnings("unused")
    public NewModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(Long publish_date) {
        this.publish_date = publish_date;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(Long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public Long getDeleted_flg() {
        return deleted_flg;
    }

    public void setDeleted_flg(Long deleted_flg) {
        this.deleted_flg = deleted_flg;
    }
}
