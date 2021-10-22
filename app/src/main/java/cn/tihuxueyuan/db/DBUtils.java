package cn.tihuxueyuan.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseList;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.model.UserListenedCourse;
import cn.tihuxueyuan.utils.Constant;

public class DBUtils {
    DBOpenHelper helper;
    SQLiteDatabase db;

    /**
     * 构造方法，只有当类被实例化时候调用
     * 实例化SQLiteHelper类，从中得到一个读写的数据库
     **/
    public DBUtils(Context context) {

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AAA";
//        File pathFile = new File(path);
//        File file = new File(path+"/aa.db");
//        try{
//            if(!pathFile.exists()){
//                pathFile.mkdirs();
//            }
//            if(!file.exists()){
//                file.createNewFile();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        db = SQLiteDatabase.openOrCreateDatabase(file,null);

        helper = new DBOpenHelper(context, "1122.db", null, 1);
        db = helper.getWritableDatabase();
    }

    private static DBUtils instance;

    /**
     * 得到这个类的实例
     **/
    public static DBUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }

    //保存个人资料信息
    public void saveUserInfo(UserBean bean) {
        ContentValues cv = new ContentValues();
        cv.put("userName", bean.userName);
//        cv.put("nickName", bean.nickName);
//        cv.put("sex", bean.sex);
//        cv.put("signature", bean.signature);
//        cv.put("qq",bean.qq);
//        //Convenience method for inserting a row into the database.
        //注意，我们是从数据库使用插入方法，传入表名和数据集完成插入
        db.insert(DBOpenHelper.U_USER_INFO, null, cv);
    }

    public void saveCourseTypes(List<CourseTypeList.CourseType> cc) {
        for (CourseTypeList.CourseType c : cc) {
            ContentValues cv = new ContentValues();
            cv.put("name", c.getName());
            cv.put("id", c.getId());
            db.insert(DBOpenHelper.COURSE_TYPE, null, cv);
        }
    }

    public void saveCourseList(List<CourseList.Course> cc) {
        for (CourseList.Course c : cc) {
            ContentValues cv = new ContentValues();
            cv.put("title", c.getTitle());
            cv.put("img_file_name", c.getImgFileName());
            cv.put("type_id", c.getTypeId());
            cv.put("course_id", c.getId());
            cv.put("id", c.getId());
            db.insert(DBOpenHelper.COURSE, null, cv);
        }
    }

    public void saveCourseFiles(List<CourseFileList.CourseFile> courseFiles) {
        for (CourseFileList.CourseFile courseFile : courseFiles) {
            ContentValues cv = new ContentValues();
//            cv.put("course_file_id", courseFile.getId());
            cv.put("id", courseFile.getId());
            cv.put("course_id", courseFile.getCourseId());
            cv.put("number", courseFile.getNumber());
            cv.put("mp3_file_name", courseFile.getMp3FileName());
            cv.put("duration", courseFile.getDuration());
            db.insert(DBOpenHelper.COURSE_FILE, null, cv);
        }
    }

    @SuppressLint("Range")
    public List<CourseTypeList.CourseType> getCourseTypes() {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE_TYPE;
        List<CourseTypeList.CourseType> lc = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            CourseTypeList.CourseType bean = new CourseTypeList.CourseType();
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            lc.add(bean);
        }
        cursor.close();
        return lc;
    }

    @SuppressLint("Range")
    public List<CourseList.Course> getCourseList(int typeId) {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE + " WHERE type_id =?";
        String args[] = {String.valueOf(typeId)};
        List<CourseList.Course> lc = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            CourseList.Course course = new CourseList.Course();
            course.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            course.setTypeId(cursor.getInt(cursor.getColumnIndex("type_id")));
            course.setId(cursor.getInt(cursor.getColumnIndex("id")));
            course.setImgFileName(cursor.getString(cursor.getColumnIndex("img_file_name")));
            lc.add(course);
        }
        cursor.close();
        return lc;
    }

    @SuppressLint("Range")
    public UserListenedCourse getUserListenedCourseByUserCodeAndCourseId(String code, int courseId) {
        String sql = "SELECT * FROM " + DBOpenHelper.USER_LISTENED_COURSE + " WHERE code=? and course_id=? ";
        String args[] = {code, String.valueOf(courseId)};
        Cursor cursor = db.rawQuery(sql, args);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UserListenedCourse u = new UserListenedCourse();
            u.code = cursor.getString(cursor.getColumnIndex("code"));
            u.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            u.id = cursor.getInt(cursor.getColumnIndex("id"));
            u.listenedFiles = cursor.getString(cursor.getColumnIndex("listened_files"));
            u.lastListenedCourseFileId = cursor.getInt(cursor.getColumnIndex("last_listened_course_file_id"));
            return u;
        } else {
            return null;
        }
    }

    public void insertUserListenedCourse(String code, int courseId, String listenedFiles, int fileId) {
        ContentValues cv = new ContentValues();
        cv.put("code", code);
        cv.put("course_id", courseId);
        cv.put("id", courseId);
        cv.put("listened_files", listenedFiles);
        cv.put("last_listened_course_file_id", fileId);

        db.insert(DBOpenHelper.USER_LISTENED_COURSE, null, cv);
    }

    public void updateUserListenedCourse(String code, int courseId, String listenedFiles, int fileId) {
        ContentValues cv = new ContentValues();
        cv.put("listened_files", listenedFiles);
        cv.put("course_id", courseId);
        cv.put("id", courseId);
        cv.put("last_listened_course_file_id", fileId);
        String args[] = {code, String.valueOf(courseId)};
        db.update(DBOpenHelper.USER_LISTENED_COURSE, cv, " code=? and course_id = ?", args);
    }

    public int getFileCountByCourseId(int courseId) {
        String sql = "SELECT count(id) FROM " + DBOpenHelper.COURSE_FILE + " WHERE course_id =?";
        String args[] = {String.valueOf(courseId)};
        Cursor cursor = db.rawQuery(sql, args);
        cursor.moveToFirst();

        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getCourseTypeCount() {
        String sql = "SELECT count(id) FROM " + DBOpenHelper.COURSE_TYPE;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getCourseListCountByTypeId(int typeId) {
        String sql = "SELECT count(id) FROM " + DBOpenHelper.COURSE + " WHERE type_id =?";
        String args[] = {String.valueOf(typeId)};
        Cursor cursor = db.rawQuery(sql, args);
        cursor.moveToFirst();

        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }


    @SuppressLint("Range")
    public List<CourseFileList.CourseFile> getSqlite3CourseFileList(int courseId) {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE_FILE + " WHERE course_id =?";
        //?和下面数组内元素会逐个替换，可以多条件查询=?and =?
        //You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs.
        String args[] = {String.valueOf(courseId)};
        List<CourseFileList.CourseFile> lc = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, args);
        List<CourseFileList.CourseFile> beans = null;
        //Move the cursor to the next row.
        while (cursor.moveToNext()) {
            CourseFileList.CourseFile courseFile = new CourseFileList.CourseFile();
            //根据列索引获取对应的数值，因为这里查询结果只有一个，我们也不需要对模型UserBean进行修改，
            //直接将对应用户名的所有数据从表中动态赋值给bean
            courseFile.id = cursor.getInt(cursor.getColumnIndex("id"));
            courseFile.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            courseFile.mp3_file_name = cursor.getString(cursor.getColumnIndex("mp3_file_name"));
            courseFile.number = cursor.getInt(cursor.getColumnIndex("number"));
//            bean.courseFileId = cursor.getInt(cursor.getColumnIndex("course_file_id"));
//            bean.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
//            bean.sex = cursor.getString(cursor.getColumnIndex("sex"));
//            bean.signature = cursor.getString(cursor.getColumnIndex("signature"));
//            bean.beanqq = cursor.getString(cursor.getColumnIndex("qq"));
            Log.d(Constant.TAG, " sqlite mp3: " + courseFile.mp3_file_name+ ", id= " + courseFile.id );
            lc.add(courseFile);
        }

        cursor.close();
        return lc;
    }

    //获取个人资料信息
    @SuppressLint("Range")
    public UserBean getUserInfo(String userName) {
        String sql = "SELECT * FROM " + DBOpenHelper.U_USER_INFO + " WHERE userName=?";
        //?和下面数组内元素会逐个替换，可以多条件查询=?and =?
        //You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs.
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        UserBean bean = null;
        //Move the cursor to the next row.
        while (cursor.moveToNext()) {
            bean = new UserBean();
            //根据列索引获取对应的数值，因为这里查询结果只有一个，我们也不需要对模型UserBean进行修改，
            //直接将对应用户名的所有数据从表中动态赋值给bean
            bean.userName = cursor.getString(cursor.getColumnIndex("userName"));
//            bean.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
//            bean.sex = cursor.getString(cursor.getColumnIndex("sex"));
//            bean.signature = cursor.getString(cursor.getColumnIndex("signature"));
//            bean.beanqq = cursor.getString(cursor.getColumnIndex("qq"));
        }
        cursor.close();
        return bean;
    }

    //修改个人资料信息,这里的key指代表字段，value表示数值
    public void updateUserInfo(String key, String value, String userName) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        //Convenience method for updating rows in the database.
        db.update(DBOpenHelper.U_USER_INFO, cv, "userName=?", new String[]
                {userName});
    }

}
