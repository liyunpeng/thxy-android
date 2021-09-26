package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseList {
    @SerializedName("courseList")
    private List<Course> courseList;

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public static class Course {
        @SerializedName("title")
        private String title;
        @SerializedName("id")
        private int id;
        @SerializedName("img_file_name")
        private String ImgFileName;
        @SerializedName("store_path")
        private String storePath;

        public String getTitle() {
            return title;
        }

        public String getStorePath() {
            return storePath;
        }

        public String getImgFileName() { return ImgFileName;}
        public void setTitle(String title) {
            this.title = title;
        }

//        public String getArtistname() {
//            return artistname;
//        }
//
//        public void setArtistname(String artistname) {
//            this.artistname = artistname;
//        }

        public int getId() {
            return id;
        }

        public void setId(String Id) {
            this.id = id;
        }
    }
}
