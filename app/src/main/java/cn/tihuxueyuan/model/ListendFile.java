package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

public class ListendFile  {
    /*
    type ListenedFile struct {
	CourseFileId    int `json:"cfi"` // 为了节约数据库存储空间
	ListenedPercent int `json:"pc"`
	Position        int `json:"pos"`
}
     */

    @SerializedName("cfi")
    public   int courseFileId;

    @SerializedName("pc")
    public   int listenedPercent;

    @SerializedName("pos")
    public   int position;

}
