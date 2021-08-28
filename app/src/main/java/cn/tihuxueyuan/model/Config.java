package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Config {
    @SerializedName("base_url")
    private String baseUrl;

    @SerializedName("mp3_source_router")
    private String mp3SourceRouter;


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getMp3SourceRouter() {
        return mp3SourceRouter;
    }
}
