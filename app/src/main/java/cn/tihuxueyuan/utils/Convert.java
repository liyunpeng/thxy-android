package cn.tihuxueyuan.utils;

import androidx.annotation.NonNull;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.ListendFile;

public  class Convert<T> {


    public Convert() {
//        helper = new DBOpenHelper(context, "abcde.db", null, 1);
//        db = helper.getWritableDatabase();
    }

    private static Convert instance;

    public static Convert getInstance() {
        if (instance == null) {
            instance = new Convert();
        }
        return instance;
    }


    // list 转 map
    public void listToMap2(List<T> aa) {
        Map<Integer, CourseFileList.CourseFile> m = Constant.appData.courseFileMap;
        m.clear();
        for (CourseFileList.CourseFile c : Constant.appData.courseFileList) {
            int i = c.getId();
            m.put(i, c);
        }
    }

//    private java.util.ArrayList<E> list = new java.util.ArrayList<E> ();
    public void listToMap1(java.util.ArrayList<T> aa) {
        Map<Integer, CourseFileList.CourseFile> m = Constant.appData.courseFileMap;
        m.clear();
        for (CourseFileList.CourseFile c : Constant.appData.courseFileList) {
            int i = c.getId();
            m.put(i, c);
        }
    }

    public Map<Integer, ListendFile> listToMap3(ListendFile[] aa) {
        Map<Integer, ListendFile> m = new AbstractMap<Integer, ListendFile>() {
            @NonNull
            @Override
            public Set<Entry<Integer, ListendFile>> entrySet() {
                return null;
            }
        };
        m.clear();

        for (ListendFile c : aa) {
            int i = c.courseFileId;
            m.put(i, c);
        }
        return m;
    }
}
