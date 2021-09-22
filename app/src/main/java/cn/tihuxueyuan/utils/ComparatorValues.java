package cn.tihuxueyuan.utils;

import java.util.Comparator;

import cn.tihuxueyuan.model.CourseFileList.CourseFile;

public class ComparatorValues implements Comparator<CourseFile> {

    @Override
    public int compare(CourseFile m1, CourseFile m2) {
        int result = 0;
        int old1 = m1.number;
        int old2 = m2.number;

        if (Constant.order == true) {
            if (old1 >= old2) {
                result = 1;
            } else {
                result = -1;
            }
        } else {
            if (old1 <= old2) {
                result = 1;
            } else {
                result = -1;
            }
        }

        return result;
    }

}
