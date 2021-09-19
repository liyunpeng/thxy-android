package cn.tihuxueyuan.db;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBOpenHelper extends SQLiteOpenHelper {
    final String CREATE_DOCUMENT = "create table tb_document (_id integer primary key autoincrement," +
            "time, height, weight, blood_pressure, temperature, pulse, breathe)";

    private static final int DB_VERSION = 1;
    public static String DB_NAME = "bxg.db";
    public static final String U_USER_INFO = "userInfo";

    Context mContext;
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d( TAG, " 开始创建sqlite3 数据库表 ");

        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_LONG).show();
        db.execSQL(CREATE_DOCUMENT);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + U_USER_INFO + "( "
                + "_id  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userName VARCHAR, "
                + "nickName VARCHAR, "
                + "sex VARCHAR, "
                + "signature VARCHAR, "
                + "qq VARCHAR "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS  user_listend_courses ( "
                + "_id  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "code VARCHAR, "
                + "course_id INT, "
                + "last_listend_course_file_id INT, "
                + "listened_files INT "
                + ")");

        Log.d( TAG, " 成功创建sqlite3 数据库表 ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("健康档案表","版本更新"+oldVersion+"-->"+newVersion);
    }



//    /**
//     * 当数据库版本号增加才会调用此方法
//     **/
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + U_USER_INFO);
//        onCreate(db);
//    }

}