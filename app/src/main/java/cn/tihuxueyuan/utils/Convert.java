package cn.tihuxueyuan.utils;

import androidx.annotation.NonNull;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.ListenedFile;

public  class Convert<T> {


    public Convert() {
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

    public Map<Integer, ListenedFile> listToMap3(ListenedFile[] aa) {
        Map<Integer, ListenedFile> m = new AbstractMap<Integer, ListenedFile>() {
            @NonNull
            @Override
            public Set<Entry<Integer, ListenedFile>> entrySet() {
                return null;
            }
        };
        m.clear();

        for (ListenedFile c : aa) {
            int i = c.courseFileId;
            m.put(i, c);
        }
        return m;
    }
}
