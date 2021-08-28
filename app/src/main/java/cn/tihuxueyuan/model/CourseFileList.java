package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseFileList {
    @SerializedName("courseFileList")
    private List<CourseFile> courseFileList;
    public List<CourseFile> getCourseFileList() {
        return courseFileList;
    }
    public void setCourseList(List<CourseFile> courseList) {
        this.courseFileList = courseList;
    }

    public static class CourseFile {
        @SerializedName("id")
        private String id;
        @SerializedName("mp3_file_name")
        private String mp3_file_name;
        @SerializedName("img_src")
        private String img_src;
        @SerializedName("number")
        private String number;

        public String getFileName() {
            return mp3_file_name;
        }

        public String getNumber(){
            return  number;
        }

        public String getMp3FileName() {
            return mp3_file_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String Id) {
            this.id = id;
        }
    }
}
