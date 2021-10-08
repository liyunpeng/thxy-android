package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

public class ListenedFile {
    @SerializedName("cfi")
    public   int courseFileId;

    @SerializedName("pc")
    public   int listenedPercent;

    @SerializedName("pos")
    public   int position;

}