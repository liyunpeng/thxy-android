package cn.tihuxueyuan.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseTypeList {
    @SerializedName("coursetypes")
    private List<CourseType> courseTypes;

    public List<CourseType> getCourseType() {
        return courseTypes;
    }

    public void setCourseType(List<CourseType> courseTypes) {
        this.courseTypes = courseTypes;
    }

    public static class CourseType {
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private int id;

        @SerializedName("course_update_version")
        private int courseUpdateVersion;

        public int getCourseUpdateVersion() {
            return courseUpdateVersion;
        }

        public String getName() {
            return name;
        }

        public void setName(String coursetype) {
            this.name = coursetype;
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

        public void setId(int id) {
            this.id = id;
        }

        public void setCourseUpdateVersion(int updateVersion) {
            this.courseUpdateVersion = updateVersion;
        }
    }
}
