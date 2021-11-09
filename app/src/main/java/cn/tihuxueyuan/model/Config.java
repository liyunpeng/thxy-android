package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Config {
//    @SerializedName("base_url")
//    private String baseUrl;

//    @SerializedName("mp3_source_router")
//    private String mp3SourceRouter;

    @SerializedName("service_current_version")
    private String serviceCurrentVersion;

    @SerializedName("course_type_update_version")
    private int courseTypeUpdateVersion;


    @SerializedName("id")
    private int id;


//    public String getBaseUrl() {
//        return baseUrl;
//    }

//    public String setBaseUrl( String baseUrlParam) {
//        return  this.baseUrl = baseUrlParam;
//    }

    public String getServiceCurrentVersion() {
        return serviceCurrentVersion;
    }

    public int getCourseTypeUpdateVersion() {
        return courseTypeUpdateVersion;
    }


    public int setCourseTypeUpdateVersion(int v) {
        return courseTypeUpdateVersion = v;
    }


    public int getId() {
        return id;
    }

    public int setId(  int idParam) {
        return this.id = idParam;
    }
}
