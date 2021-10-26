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
    private String updateMsg = "有新的版本，点确认可以下载安装";
    private String apkUrl = "http://47.102.146.8:8082/api/apkUpload";
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    private static String savePath =  null; // "/storage/emulated/0/Thxy/";// 保存apk的文件夹
    private static final String saveFileName = savePath + "a.apk";
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private int progress;
    private Thread downLoadThread; // 下载线程
    private boolean stopFlag = false;// 用户取消下载

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

    public void showUpdateSoftwareDialog(Context  c) {
        mContext = c;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        builder.setTitle("下载后自动安装");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress =  v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                stopFlag = true;
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
                } while (!stopFlag);

                outStream.flush();
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
