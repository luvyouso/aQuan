package jp.co.asaichi.pubrepo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleService  implements Serializable{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("googleServiceInfoPlist")
    @Expose
    private String googleServiceInfoPlist;
    @SerializedName("googleServiceInfoJson")
    @Expose
    private String googleServiceInfoJson;
    @SerializedName("agreementUrl")
    @Expose
    private String agreementUrl;
    @SerializedName("faqUrl")
    @Expose
    private String faqUrl;
    @SerializedName("guidUrl")
    @Expose
    private String guidUrl;
    @SerializedName("privacyPolicyUrl")
    @Expose
    private String privacyPolicyUrl;
    @SerializedName("defaultLatitude")
    @Expose
    private Float defaultLatitude;
    @SerializedName("defaultLongitude")
    @Expose
    private Float defaultLongitude;

    private String applicationId;
    private String apiKey;
    private String databaseUrl;
    private String storageBucket;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleServiceInfoPlist() {
        return googleServiceInfoPlist;
    }

    public void setGoogleServiceInfoPlist(String googleServiceInfoPlist) {
        this.googleServiceInfoPlist = googleServiceInfoPlist;
    }

    public String getGoogleServiceInfoJson() {
        return googleServiceInfoJson;
    }

    public void setGoogleServiceInfoJson(String googleServiceInfoJson) {
        this.googleServiceInfoJson = googleServiceInfoJson;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }

    public String getFaqUrl() {
        return faqUrl;
    }

    public void setFaqUrl(String faqUrl) {
        this.faqUrl = faqUrl;
    }

    public String getGuidUrl() {
        return guidUrl;
    }

    public void setGuidUrl(String guidUrl) {
        this.guidUrl = guidUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public Float getDefaultLatitude() {
        return defaultLatitude;
    }

    public void setDefaultLatitude(Float defaultLatitude) {
        this.defaultLatitude = defaultLatitude;
    }

    public Float getDefaultLongitude() {
        return defaultLongitude;
    }

    public void setDefaultLongitude(Float defaultLongitude) {
        this.defaultLongitude = defaultLongitude;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }
}
