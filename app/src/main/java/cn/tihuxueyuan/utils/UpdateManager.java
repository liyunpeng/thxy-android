package cn.tihuxueyuan.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.tihuxueyuan.R;

public class UpdateManager {
    private Context mContext;
    private String updateMsg = "有最新的软件包，请下载！";
    // 下载安装包的网络路径
    private String apkUrl = "http://47.102.146.8:8082/api/apkUpload";
//    private static final String BASE_URL = "http://47.102.146.8:8082/api/";
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    private static String savePath =  null; // "/storage/emulated/0/Thxy/";// 保存apk的文件夹
    private static final String saveFileName = savePath + "a.apk";
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private int progress;
    private Thread downLoadThread; // 下载线程
    private boolean interceptFlag = false;// 用户取消下载
    // 通知处理刷新界面的handler
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public UpdateManager(Context context) {
        this.mContext = context;
        savePath = context.getFilesDir().getAbsolutePath() + File.separator + "abc.apk";
    }
    // 显示更新程序对话框，供主程序调用
    public  void updateSoftware(Context  c) {
        mContext = c;
        showNoticeDialog();
    }
    private void showNoticeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                mContext); // Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }
    protected void showDownloadDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("下载后自动更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);// 设置对话框的内容为一个View
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    protected void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            Log.d(Constant.TAG, "下载文件不存在");
            return;
        }else{
            Log.d(Constant.TAG, "下载文件存在, 启动安装");
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");// File.toString()会返回路径信息
        mContext.startActivity(i);
    }
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;

                ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
                File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file1 = new File(directory, "app-debug.apk");


//                File ApkFile = new File(apkFile);
//                ApkFile.createNewFile();
                FileOutputStream outStream = new FileOutputStream(file1);
//
//                out = new FileOutputStream(new File(dir, "log-"
//                        + getFileName() + ".log"));
//

                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载

                outStream.flush();
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
