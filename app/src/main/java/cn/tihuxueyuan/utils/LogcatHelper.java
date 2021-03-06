package cn.tihuxueyuan.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    public static String PATH_LOGCAT;
    public static String fileName;
    private LogDumper mLogDumper = null;
    private int mPId;

    public void init(Context context) {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 优先保存到SD卡中
//            Log.d(TAG, "优先保存到SD卡中");
//            PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "documents/";
//        } else {// 如果SD卡不存在，就保存到本应用的目录下
//            Log.d(TAG, "如果SD卡不存在，就保存到本应用的目录下");
//            PATH_LOGCAT = context.getFilesDir().getAbsolutePath() + File.separator + "documents/";
//        }

        PATH_LOGCAT = context.getFilesDir().getAbsolutePath() + File.separator;
// PATH_LOGCAT =        /data/user/0/cn.tihuxueyuan/files/log-2021-11-01-03:17:25.log
//        PATH_LOGCAT = Environment.getDataDirectory() + File.separator + "Thxy/";
//        PATH_LOGCAT = Environment.getRootDirectory() + File.separator + "Thxy/";
        Log.d(TAG, " log 初始化 PATH_LOGCAT  =" + PATH_LOGCAT);
        File file = new File(PATH_LOGCAT);

        if (!file.exists()) {
            Log.d(TAG, " 创建了log目录");
            file.mkdirs();
        } else {
            Log.d(TAG, " log目录已经存在，  不创建log目录");
        }
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
//        mLogDumper.run();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                // 有些人自己做的很好， 而对他从 生了排斥心， 瞧不起心，  最容易帮我们出离傲慢， 盯着别人的优点看， 不要看缺点
                fileName = "log-" + getFileName() + ".log";
                out = new FileOutputStream(new File(dir, fileName));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**`
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                Log.d("thxy", " log helper run ");
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }

    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }

//        public  String getDateEN() {
//            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String date1 = format1.format(new Date(System.currentTimeMillis()));
//            return date1;// 2012-10-03 23:41:31
//        }
}