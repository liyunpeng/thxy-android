package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseFileList {
    @SerializedName("courseFileList")
    private List<CourseFile> courseFileList;

    @SerializedName("last_listened_course_file_id")
    private int LastListenedCourseFileId;
    public int getLastListenedCourseFileId() { return  LastListenedCourseFileId;}

    public List<CourseFile> getCourseFileList() {
        return courseFileList;
    }
    public void setCourseList(List<CourseFile> courseList) {
        this.courseFileList = courseList;
    }

    public static class CourseFile {
        @SerializedName("id")
        public int id;
        @SerializedName("mp3_file_name")
        public String mp3_file_name;
        @SerializedName("img_src")
        public String img_src;
        @SerializedName("number")
        public String number;
        @SerializedName("course_id")
        public int courseId;
        @SerializedName("listened_percent")
        public int listenedPercent;
        @SerializedName("duration")
        public String duration;
        @SerializedName("listened_position")
        public int listenedPosition;


        public int getCourseId() { return  courseId;}
        public String getFileName() {
            return mp3_file_name;
        }

        public int getListenedPercent() {
            return listenedPercent;
        }
        public int getListenedPosition() {
            return listenedPosition;
        }
        public String getDuration() {
            return duration;
        }

        public String getNumber(){
            return  number;
        }

        public String getMp3FileName() {
            return mp3_file_name;
        }

        public int getId() {
            return id;
        }

        public void setId(String Id) {
            this.id = id;
        }
    }
}
