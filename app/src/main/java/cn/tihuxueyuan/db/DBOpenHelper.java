package cn.tihuxueyuan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
    final String CREATE_DOCUMENT = "create table tb_document (_id integer primary key autoincrement," +
            "time, height, weight, blood_pressure, temperature, pulse, breathe)";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

//    public DBOpenHelper(Context context) {
//        super(context);
//    }

    //核心代码
    private static final int DB_VERSION = 1;
    public static String DB_NAME = "bxg.db";
    public static final String U_USER_INFO = "userInfo";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DOCUMENT);

        /**
         * 当这个SQLiteOpenHelper的子类类被实例化时会创建指定名的数据库，在onCreate中创建个人信息表
         * **/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + U_USER_INFO + "( "
                + "_id  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userName VARCHAR, "
                + "nickName VARCHAR, "
                + "sex VARCHAR, "
                + "signature VARCHAR, "
                + "qq VARCHAR "
                + ")");
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