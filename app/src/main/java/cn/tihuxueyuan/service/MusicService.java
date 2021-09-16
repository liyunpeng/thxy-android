package cn.tihuxueyuan.service;

import static cn.tihuxueyuan.utils.Constant.NEWPLAY;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;
import static cn.tihuxueyuan.utils.Constant.musicControl;
//import static cn.tihuxueyuan.utils.Constant.musicReceiver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.receiver.NotificationClickReceiver;
import cn.tihuxueyuan.R;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public MediaPlayer player;
    public Timer timer;

    /**
     * 通知栏控制Activity页面UI
     */
    private LiveDataBus.BusMutableLiveData<String> musicActivityLiveData;
    /**
     * 通知
     */
    private static Notification notification;
    /**
     * 通知栏视图
     */
    private static RemoteViews remoteViews;
    /**
     * 通知ID
     */
    private int NOTIFICATION_ID = 1;
    /**
     * 通知管理器
     */
    private static NotificationManager manager;

    /**
     * 音乐广播接收器
     */
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    private AppData appData;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MusicService onCreate");
        initNotificationRemoteViews();
        initNotification();
        appData = (AppData) getApplication();
        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int pos = appData.courseFileMap.get(appData.currentCourseFileId).getListenedPosition();
                int percent = appData.courseFileMap.get(appData.currentCourseFileId).getListenedPercent();
                String fileName = appData.courseFileMap.get(appData.currentCourseFileId).getFileName();
                if (pos > 0) {
                    Log.d(TAG, "播放器 文件名=" + fileName + "  percent=" + percent + ",  seek to 的位置 = " + pos);
                    if (percent == 100) {
                        Log.d(TAG, " 上次已听完， 这次seekto开始位置，重新听 ");
                        player.seekTo(0);
                    } else {
                        player.seekTo(pos);
                    }
                }
                player.start();
                musicControl.setText();
                addTimer();
                // 在播放器状态确定好之后，再显示通知栏，以保证应用界面和通知栏按钮状态一致
                updateNotificationShow(appData.currentPostion);
                musicActivityLiveData.postValue(NEWPLAY);
            }
        });
        player.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG, "音乐回调函数 onCompletion 调用");
                        SPUtils.sendListenedPerscent();

                        if (appData.currentPostion >= (appData.courseFileList.size() - 1)) {
                            Log.d(TAG, "音乐回调函数 onCompletion 调用 currentPostion  赋值");
                            appData.currentPostion = (appData.courseFileList.size() - 1);
                        } else {
                            appData.currentPostion++;
                            appData.currentCourseFileId = appData.courseFileList.get(appData.currentPostion).getId();
                            musicControl.playListened(NEWPLAY);
//                            try {
//                                String mp3url = SPUtils.getMp3Url(appData.mList.get(appData.currentPostion).getMp3FileName());
//                                player.setDataSource(mp3url);
//                                player.prepare();
//                                player.start();
//                                addTimer();
//                                // 在播放器状态确定好之后，再显示通知栏，以保证应用界面和通知栏按钮状态一致
//                                updateNotificationShow(appData.currentPostion);
//                                musicActivityLiveData.postValue(NEWPLAY);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }


//                            new Thread(new Runnable() {
//                                public void run() {
//                                    try {
//                                        Thread.sleep(1000);
////                                        if (pos > 0) {
////                                            Log.d(TAG, "播放器 文件名=" + fileName + "  percent=" + percent + ",  seek to 的位置 = " + pos);
////                                            player.seekTo(pos);
////                                        }
//                                        player.start();
//                                        addTimer();
//                                        // 在播放器状态确定好之后，再显示通知栏，以保证应用界面和通知栏按钮状态一致
//                                        updateNotificationShow(appData.currentPostion);
//                                        musicActivityLiveData.postValue(NEWPLAY);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
////                      handler.sendMessage();
//                                }
//                            }).start();
//                            setText();
                            Log.d(TAG, "onCompletion 监听到当前音乐播放完成，自动播放到下一首 ");
                        }

                    }
                }
        );
        musicActivityLiveData = LiveDataBus.getInstance().with(Constant.MusicLiveDataObserverTag, String.class);
    }

    /**
     * Activity的观察者
     */
//    private void activityObserver() {
//        notification0LiveData = LiveDataBus.getInstance().with("notification_control", String.class);
//        notificationLiveData.observe(MusicService.this, new Observer<String>() {
//            @Override
//            public void onChanged(String state) {
//                //UI控制
//                UIControl(state, TAG);
//            }
//        });
//    }

    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    private void initNotificationRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent);
    }

    /**
     * 初始化通知
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NotificationTrampoline")
    private void initNotification() {
        String channelId = "play_control";
        String channelName = "播放控制";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);

        //点击整个通知时发送广播
        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*

         */
        //初始化通知
        notification = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority( Notification.PRIORITY_MAX )
                .setSmallIcon(R.mipmap.goods)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }

    public void updateNotificationShow(int position) {
        if (player.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
        }
        //封面专辑
        remoteViews.setImageViewBitmap(R.id.notification_img, appData.notificationBitMap);

        remoteViews.setTextViewText(R.id.tv_notification_song_name, SPUtils.getTitleFromName(appData.courseFileList.get(position).getFileName()));

        //歌手名
//        remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getSinger());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);

        Log.d(TAG, "显示通知栏完成  ");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        manager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    /**
     * 关闭音乐通知栏
     */
    public void closeNotification() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            }
        }
        manager.cancel(NOTIFICATION_ID);

        musicActivityLiveData.postValue(CLOSE);
    }


    /**
     * Activity的观察者
     */
//    private void activityObserver() {
//        notificationLiveData = LiveDataBus.getInstance().with("notification_control", String.class);
//        notificationLiveData.observe(MusicService.this, new Observer<String>() {
//            @Override
//            public void onChanged(String state) {
//                //UI控制
//                UIControl(state, TAG);
//            }
//        });
//    }
    public void addTimer() { //添加计时器用于设置音乐播放器中的播放进度条
        if (timer == null) {
            timer = new Timer();//创建计时器对象
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (player == null ) return;

                    int duration = player.getDuration();//获取歌曲总时长
                    int currentPosition = player.getCurrentPosition();//获取播放进度
                    Message msg = Music_Activity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至消息对象中
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    msg.setData(bundle);
                    //将消息发送到主线程的消息队列
                    Music_Activity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒执行一次
            timer.schedule(task, 5, 500);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(MusicService.this, "App要退出了", Toast.LENGTH_SHORT).show();
    }

    public class MusicControl extends Binder { //Binder是一种跨进程的通信方式
        public void release() {
            if (player == null) return;
            if (player.isPlaying()) player.stop();//停止播放音乐
            player.release();//释放占用的资源
            player = null;//将player置为空
            manager.cancel(NOTIFICATION_ID);
        }

        public void playListened(String action) {
            boolean isPlaying = player.isPlaying();
            Log.d(TAG, "播放器是否在播放 isPlaying = " + isPlaying);
            if ((action == PLAY || action == PAUSE) && isPlaying && appData.lastCourseFileId == appData.currentCourseFileId) {
//                player.pause();
//                musicActivityLiveData.postValue(PAUSE);
            } else {
                if (action == NEWPLAY) {
                    String mp3url = SPUtils.getImgOrMp3Url(appData.courseFileList.get(appData.currentPostion).getCourseId(), appData.courseFileList.get(appData.currentPostion).getMp3FileName());
                    initPlayer(mp3url);
                }

//                int pos = appData.mListMap.get(appData.currentCourseFileId).getListenedPosition();
//                int percent = appData.mListMap.get(appData.currentCourseFileId).getListenedPercent();
//                String fileName = appData.mListMap.get(appData.currentCourseFileId).getFileName();
//
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            Thread.sleep(1000);
//                            if (pos > 0) {
//                                Log.d(TAG, "播放器 文件名=" + fileName + "  percent=" + percent + ",  seek to 的位置 = " + pos);
//                                player.seekTo(pos);
//                            }
//                            player.start();
//                            setText();
//                            addTimer();
//                            // 在播放器状态确定好之后，再显示通知栏，以保证应用界面和通知栏按钮状态一致
//                            updateNotificationShow(appData.currentPostion);
//                            musicActivityLiveData.postValue(action);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
////                      handler.sendMessage();
//                    }
//                }).start();
            }

        }

//        // 适用于 next previous
//        public void playNew() {
//            Log.d(TAG, " playNew 调用");
//            String mp3url = SPUtils.getMp3Url(appData.mList.get(appData.currentPostion).getMp3FileName());
//            init(mp3url);
//
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                        player.start();
//                        addTimer();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                      handler.sendMessage();
//                }
//            }).start();
//            updateNotificationShow(appData.currentPostion);
//            musicActivityLiveData.postValue(NEWPLAY);
//        }

        public void UIControl(String state, String tag) {
            switch (state) {
                case PLAY:
                case PAUSE:
                    playOrPause();
                    break;
//                case PREV:
//                    app.currentPostion--;
//
//                    break;
//                case NEXT:
//                    app.currentPostion++;
//                    String mp3url = SPUtils.getMp3Url(app.mList.get(app.currentPostion).getMp3FileName());
//                    init(mp3url);
//                    play();
//
//                    Log.d(tag, NEXT);
//                    break;
                case CLOSE:
                    closeNotification();
                    break;
                default:
                    break;
            }
        }

        public int getPosition() {
            return player.getCurrentPosition();
        }

        public int getDuration() {
            return player.getDuration();
        }

        public int getListenedPercent() {
            if (player == null) return 0;

            float duration = player.getDuration();//获取歌曲总时长
            float currentPosition = player.getCurrentPosition();//获取播放进度
            float percentFloat = (currentPosition / duration);
            int percentInt;
            if (percentFloat > 0 && percentFloat <= 0.01) {
                percentInt = 1;
            } else {
                percentInt = (int) (percentFloat * 100);
            }
            Log.d(TAG, " 计算已听百分比, 小数点百分比=" + percentFloat + ", 整数百分比=" + percentInt);
            return percentInt;
        }

        public void setText() {
            Log.d(TAG, " musicControl.setText 调用, 调用getDuration ");
            if (player == null) return;

            int duration = player.getDuration();//获取歌曲总时长
            int currentPosition = player.getCurrentPosition();//获取播放进度
            Message msg = Music_Activity.handler.obtainMessage();//创建消息对象
            //将音乐的总时长和播放进度封装至消息对象中
            Bundle bundle = new Bundle();
            bundle.putInt("duration", duration);
            bundle.putInt("currentPosition", currentPosition);
            msg.setData(bundle);
            //将消息发送到主线程的消息队列
            Music_Activity.handler.sendMessage(msg);
        }

        public void initPlayer(String url) {
            if (appData.lastCourseFileId == appData.currentCourseFileId) {
                Log.d(TAG, " musicControl init 调用， 因为appData.lastCourseFileId == appData.currentCourseFileId， play不需要reset 和 setDataSource");
                return;
            }

            if (player.isLooping()) {
                player.stop();
                player.release();
                Log.d(TAG, "musicControl init 调用， 执行player.stop() player.release()");
            }

            Log.d(TAG, "musicControl init 调用， 执行player.reset()");
            player.reset();
            try {
                player.setDataSource(url);
                player.prepareAsync();
//                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void playOrPause() {
            Log.d(TAG, "playOrPause 调用 ");
            if (player.isPlaying()) {
                player.pause();
                musicActivityLiveData.postValue(PAUSE);
            } else {
                player.start();
                addTimer();
                musicActivityLiveData.postValue(PLAY);
            }
            updateNotificationShow(appData.currentPostion);
        }

        public boolean isPlaying() {
            return player.isPlaying();
        }

        public void seekTo(int progress) {
            player.seekTo(progress);//设置音乐的播放位置
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MusicService onDestroy");
    }
}
