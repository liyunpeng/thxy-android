package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Config {
    @SerializedName("base_url")
    private String baseUrl;

    @SerializedName("mp3_source_router")
    private String mp3SourceRouter;

    @SerializedName("service_current_version")
    private String serviceCurrentVersion;


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getServiceCurrentVersion() {
        return serviceCurrentVersion;
    }

    public String getMp3SourceRouter() {
        return mp3SourceRouter;
    }
}
