package cn.tihuxueyuan.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        helper = new DBOpenHelper(context, "abcdefghijklmno.db", null, 1);
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

    public void saveCourseFiles() {
        for (CourseFileList.CourseFile c : Constant.appData.courseFileList) {
            ContentValues cv = new ContentValues();
            cv.put("course_file_id", c.getId());
            cv.put("id", c.getId());
            cv.put("course_id", c.getCourseId());
            cv.put("number", c.getNumber());
            cv.put("mp3_file_name", c.getMp3FileName());
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
            u.listenedFiles = cursor.getString(cursor.getColumnIndex("listened_files"));
            u.lastListenedCourseFileId = cursor.getInt(cursor.getColumnIndex("last_listened_course_file_id"));
            return u;
        } else {
            return null;
        }
    }

    public void insertUserListenedCourse(String code, int courseId, String listenedFiles) {
        ContentValues cv = new ContentValues();
        cv.put("code", code);
        cv.put("course_id", courseId);
        cv.put("id", courseId);
        cv.put("listened_files", listenedFiles);
        db.insert(DBOpenHelper.USER_LISTENED_COURSE, null, cv);
        /*
                db.execSQL("CREATE TABLE IF NOT EXISTS  " + USER_LISTENED_COURSE + " ( "
                + "id  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_listened_course_id INT, "
                + "code VARCHAR, "
                + "course_id INT, "
                + "last_listened_course_file_id INT, "
                + "listened_files INT "
                + ")");
         */
    }

    public void updateUserListenedCourse(String code, int courseId, String listenedFiles) {
        ContentValues cv = new ContentValues();
//        cv.put("code", code);
//        cv.put("course_id", courseId);
        cv.put("listened_files", listenedFiles);

        /*
        ContentValues values = new ContentValues();

values.put("price", 10.99);

db.update("Book", values, "name = ?", new String[] { "The DaVinci Code" });


         */
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
            CourseFileList.CourseFile bean = new CourseFileList.CourseFile();
            //根据列索引获取对应的数值，因为这里查询结果只有一个，我们也不需要对模型UserBean进行修改，
            //直接将对应用户名的所有数据从表中动态赋值给bean
            bean.mp3_file_name = cursor.getString(cursor.getColumnIndex("mp3_file_name"));
            bean.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            bean.number = cursor.getInt(cursor.getColumnIndex("number"));
            bean.id = cursor.getInt(cursor.getColumnIndex("id"));
            bean.courseFileId = cursor.getInt(cursor.getColumnIndex("course_file_id"));
//            bean.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
//            bean.sex = cursor.getString(cursor.getColumnIndex("sex"));
//            bean.signature = cursor.getString(cursor.getColumnIndex("signature"));
//            bean.beanqq = cursor.getString(cursor.getColumnIndex("qq"));
            Log.d(Constant.TAG, " sqlite mp3: " + bean.mp3_file_name);
            lc.add(bean);
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
