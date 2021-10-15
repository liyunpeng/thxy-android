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
        @SerializedName("type_id")
        private int typeId;

        public int getTypeId() {
            return typeId;
        }

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

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

//        public void setCourseId(int typeId) {
//            this.typeId = typeId;
//        }

        public void setImgFileName(String imgFileName) {
            this.ImgFileName = imgFileName;
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

        public void setId(int Id) {
            this.id = id;
        }
    }
}
