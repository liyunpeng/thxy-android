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

        @SerializedName("img_file_name")
        public String img_file_name;

        @SerializedName("number")
        public int number;

        @SerializedName("course_id")
        public int courseId;

        @SerializedName("has_download")
        public int hasDownload;

        @SerializedName("listened_percent")
        public int listenedPercent;

        @SerializedName("duration")
        public String duration;

        @SerializedName("local_store_path")
        public String localStorePath;

        @SerializedName("listened_position")
        public int listenedPosition;

        public int getHasDownload() { return  hasDownload;}
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

        public String getLocalStorePath() {
            return localStorePath;
        }

        public int getNumber(){
            return  number;
        }

        public String getMp3FileName() {
            return mp3_file_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int Id) {
            this.id = id;
        }
    }
}
