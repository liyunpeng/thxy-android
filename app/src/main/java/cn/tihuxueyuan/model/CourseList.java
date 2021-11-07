package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseList {
    @SerializedName("course_list")
    private List<Course> courseList;

    @SerializedName("course_update_version")
    private int courseUpdateVersion;


    public int getCourseUpdateVersion() {
        return courseUpdateVersion;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public static class Course {
        @SerializedName("id")
        private int id;

        @SerializedName("type_id")
        private int typeId;

        @SerializedName("update_version")
        private int updateVersion;

        @SerializedName("title")
        private String title;

        @SerializedName("img_file_name")
        private String ImgFileName;

        @SerializedName("introduction")
        private String introduction;

        @SerializedName("store_path")
        private String storePath;

        public int getTypeId() {
            return typeId;
        }

        public String getTitle() {
            return title;
        }

        public String getIntroduction() {
            return introduction;
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

        public void setImgFileName(String imgFileName) {
            this.ImgFileName = imgFileName;
        }

        public int getId() {
            return id;
        }

        public int getUpdateVersion() {
            return updateVersion;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
