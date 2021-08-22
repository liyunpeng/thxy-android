package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseFileList {
    /*
    	CourseId    int    `json:"course_id"`
	Title       string `json:"title"`
	ImgSrc      string `json:"img_src"`
	Mp3Url      string `json:"mp3_url"`
	Mp3FileName string `json:"mp3_file_name"`
     */
    @SerializedName("courseFileList")
    private List<CourseFile> courseFileList;

    public List<CourseFile> getCourseFileList() {
        return courseFileList;
    }

    public void setCourseList(List<CourseFile> courseList) {
        this.courseFileList = courseList;
    }

    public static class CourseFile {
        @SerializedName("title")
        private String title;
        @SerializedName("id")
        private String id;
        @SerializedName("mp3_url")
        private String mp3_url;
        @SerializedName("mp3_file_name")
        private String mp3_file_name;
        @SerializedName("img_src")
        private String img_src;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMp3url() {
            return mp3_url;
        }
//
//        public void setArtistname(String artistname) {
//            this.artistname = artistname;
//        }

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
