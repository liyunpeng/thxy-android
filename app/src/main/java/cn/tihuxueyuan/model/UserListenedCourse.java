package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;


public class UserListenedCourse {
    @SerializedName("code")
    public   String code;

    @SerializedName("id")
    public int id;

    @SerializedName("course_id")
    public int courseId;

    @SerializedName("listened_files")
    public  String listenedFiles;

    @SerializedName("last_listened_course_file_id")
    public int lastListenedCourseFileId;

}
