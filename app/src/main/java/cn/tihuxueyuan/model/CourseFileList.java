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
        private int id;
        @SerializedName("mp3_file_name")
        private String mp3_file_name;
        @SerializedName("img_src")
        private String img_src;
        @SerializedName("number")
        private String number;
        @SerializedName("course_id")
        private int courseId;
        @SerializedName("listened_percent")
        private int listenedPercent;
        @SerializedName("duration")
        private String duration;


        public int getCourseId() { return  courseId;}
        public String getFileName() {
            return mp3_file_name;
        }

        public int getListenedPercent() {
            return listenedPercent;
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
