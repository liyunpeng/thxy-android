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
        private String id;

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

        public String getId() {
            return id;
        }

        public void setId(String Id) {
            this.id = id;
        }
    }
}
