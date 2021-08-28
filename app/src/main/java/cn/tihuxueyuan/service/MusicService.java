package cn.tihuxueyuan.service;

import static cn.tihuxueyuan.utils.Constant.NEWPLAY;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;
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
        //为prev控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        //为play控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        //为close控件注册事件
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

        //初始化通知
        notification = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.goods)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }



    /**
     * 更改通知的信息和UI
     *
     * @param position
     */
    public void updateNotificationShow(int position) {
        if (player.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
        }
        //封面专辑
//        remoteViews.setImageViewBitmap(R.id.iv_album_cover, MusicUtils.getAlbumPicture(this, mList.get(position).getPath(), 0));
        appData = (AppData) getApplication();
        remoteViews.setTextViewText(R.id.tv_notification_song_name, SPUtils.getTitleFromName(appData.mList.get(position).getFileName()));

        //歌手名
//        remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getSinger());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
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

        public void playNew() {
            String mp3url = SPUtils.getMp3Url(appData.mList.get(appData.currentPostion).getMp3FileName());
            init(mp3url);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        playOrPause();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                      handler.sendMessage();
                }
            }).start();

            musicActivityLiveData.postValue(NEWPLAY);
        }

        public void UIControl(String state, String tag) {
            switch (state) {
                case PLAY:
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

        public void setText() {
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

        public void init(String url) {
            if (player.isLooping()) {
                player.stop();
                player.release();
            }
            player.reset();
            try {
                player.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.prepareAsync();
            setText();
        }

        public void playOrPause() {
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
