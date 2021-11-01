package cn.tihuxueyuan.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseList;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.model.UserListenedCourse;

public class DBUtils {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private static DBUtils instance;

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

        helper = new DBOpenHelper(context, "112233445566778899101011abcd.db", null, 1);
        db = helper.getWritableDatabase();
    }

    public static DBUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }

    public void saveUserInfo(UserBean bean) {
        ContentValues cv = new ContentValues();
        cv.put("userName", bean.userName);
        db.insert(DBOpenHelper.U_USER_INFO, null, cv);
    }

    public void saveCourseTypes(List<CourseTypeList.CourseType> courseTypeList) {
        for (CourseTypeList.CourseType courseType : courseTypeList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", courseType.getName());
            contentValues.put("id", courseType.getId());
            db.insert(DBOpenHelper.COURSE_TYPE, null, contentValues);
        }
    }

    public void saveCourseList(List<CourseList.Course> courseList) {
        for (CourseList.Course course : courseList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", course.getTitle());
            contentValues.put("img_file_name", course.getImgFileName());
            contentValues.put("type_id", course.getTypeId());
            contentValues.put("introduction", course.getIntroduction());
            contentValues.put("id", course.getId());
            db.insert(DBOpenHelper.COURSE, null, contentValues);
        }
    }

    @SuppressLint("Range")
    public List<CourseList.Course> getCourseListByTypeId(int typeId) {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE + " WHERE type_id =?";
        String args[] = {String.valueOf(typeId)};
        List<CourseList.Course> courseList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, args);
        while (cursor.moveToNext()) {
            CourseList.Course course = new CourseList.Course();
            course.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            course.setTypeId(cursor.getInt(cursor.getColumnIndex("type_id")));
            course.setId(cursor.getInt(cursor.getColumnIndex("id")));
            course.setImgFileName(cursor.getString(cursor.getColumnIndex("img_file_name")));
            courseList.add(course);
        }
        cursor.close();
        return courseList;
    }

    public void saveCourseFiles(List<CourseFileList.CourseFile> courseFileList) {
        for (CourseFileList.CourseFile courseFile : courseFileList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", courseFile.getId());
            contentValues.put("course_id", courseFile.getCourseId());
            contentValues.put("number", courseFile.getNumber());
            contentValues.put("mp3_file_name", courseFile.getMp3FileName());
            contentValues.put("duration", courseFile.getDuration());
            contentValues.put("download_mode", 0);
            db.insert(DBOpenHelper.COURSE_FILE, null, contentValues);
        }
    }

    public void updateCourseFileDownload( int id, int downloadMode,  String storeLocalPath) {
        ContentValues cv = new ContentValues();
//        cv.put("download_mode", 1);
        cv.put("local_store_path", storeLocalPath);
        cv.put("download_mode", downloadMode);
        String args[] = {String.valueOf(id) };
        db.update(DBOpenHelper.COURSE_FILE, cv, " id = ?", args);
    }

    @SuppressLint("Range")
    public List<CourseTypeList.CourseType> getCourseTypes() {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE_TYPE;
        List<CourseTypeList.CourseType> courseTypeList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            CourseTypeList.CourseType bean = new CourseTypeList.CourseType();
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            courseTypeList.add(bean);
        }
        cursor.close();
        return courseTypeList;
    }


    @SuppressLint("Range")
    public UserListenedCourse getUserListenedCourseByUserCodeAndCourseId(String code, int courseId) {
        String sql = "SELECT * FROM " + DBOpenHelper.USER_LISTENED_COURSE + " WHERE code=? and course_id=? ";
        String args[] = {code, String.valueOf(courseId)};
        Cursor cursor = db.rawQuery(sql, args);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UserListenedCourse userListenedCourse = new UserListenedCourse();
            userListenedCourse.code = cursor.getString(cursor.getColumnIndex("code"));
            userListenedCourse.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            userListenedCourse.id = cursor.getInt(cursor.getColumnIndex("id"));
            userListenedCourse.listenedFiles = cursor.getString(cursor.getColumnIndex("listened_files"));
            userListenedCourse.lastListenedCourseFileId = cursor.getInt(cursor.getColumnIndex("last_listened_course_file_id"));
            return userListenedCourse;
        } else {
            return null;
        }
    }

    public void insertUserListenedCourse(String code, int courseId, String listenedFiles, int fileId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("course_id", courseId);
        contentValues.put("id", courseId);
        contentValues.put("listened_files", listenedFiles);
        contentValues.put("last_listened_course_file_id", fileId);

        db.insert(DBOpenHelper.USER_LISTENED_COURSE, null, contentValues);
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
    public List<CourseFileList.CourseFile> getSqliteCourseFileList(int courseId) {
        String sql = "SELECT * FROM " + DBOpenHelper.COURSE_FILE + " WHERE course_id =?";
        String args[] = {String.valueOf(courseId)};
        List<CourseFileList.CourseFile> lc = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, args);
        List<CourseFileList.CourseFile> beans = null;
        while (cursor.moveToNext()) {
            CourseFileList.CourseFile courseFile = new CourseFileList.CourseFile();
            courseFile.id = cursor.getInt(cursor.getColumnIndex("id"));
            courseFile.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            courseFile.mp3_file_name = cursor.getString(cursor.getColumnIndex("mp3_file_name"));
            courseFile.number = cursor.getInt(cursor.getColumnIndex("number"));
            courseFile.duration = cursor.getString(cursor.getColumnIndex("duration"));
            courseFile.downloadMode = cursor.getInt(cursor.getColumnIndex("download_mode"));
            courseFile.localStorePath = cursor.getString(cursor.getColumnIndex("local_store_path"));
            lc.add(courseFile);
        }

        cursor.close();
        return lc;
    }

    @SuppressLint("Range")
    public UserBean getUserInfo(String userName) {
        String sql = "SELECT * FROM " + DBOpenHelper.U_USER_INFO + " WHERE userName=?";
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        UserBean bean = null;
        while (cursor.moveToNext()) {
            bean = new UserBean();
            bean.userName = cursor.getString(cursor.getColumnIndex("userName"));
        }
        cursor.close();
        return bean;
    }

    public void updateUserInfo(String key, String value, String userName) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        db.update(DBOpenHelper.U_USER_INFO, cv, "userName=?", new String[]
                {userName});
    }
}