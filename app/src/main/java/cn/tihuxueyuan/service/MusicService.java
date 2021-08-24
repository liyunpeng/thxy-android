package cn.tihuxueyuan.service;

import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.globaldata.Data;
import cn.tihuxueyuan.receiver.NotificationClickReceiver;
import cn.tihuxueyuan.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public MediaPlayer player;
    public Timer timer;

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
    private MusicReceiver musicReceiver;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
        //注册动态广播
        registerMusicReceiver();
        player = new MediaPlayer();//创建音乐播放器对象
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

    Data data;

    /**
     * 更改通知的信息和UI
     *
     * @param position
     */
    public void updateNotificationShow(int position) {
        //播放状态判断
        if (player.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
        }
        //封面专辑
//        remoteViews.setImageViewBitmap(R.id.iv_album_cover, MusicUtils.getAlbumPicture(this, mList.get(position).getPath(), 0));
        //歌曲名
        data = (Data) getApplication();

        remoteViews.setTextViewText(R.id.tv_notification_song_name, data.mList.get(position).getTitle());
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
     * 注册动态广播
     */
    private void registerMusicReceiver() {
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }

    /**
     * 广播接收器 （内部类）
     */
    public class MusicReceiver extends BroadcastReceiver {

        public static final String TAG = "MusicReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("tag1", "onReceive : "+ intent.toString());
//            SPUtils.putBoolean(Constant.IS_CHANGE, true, context);
//            //UI控制

//            UIControl(intent.getAction(), TAG);
        }
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

    public class MusicControl extends Binder { //Binder是一种跨进程的通信方式

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
            player.reset();
            try {
//                        player.setDataSource("http://47.102.146.8:8082/api/fileDownload?fileName=一声佛号一声心.mp3");
                player.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.prepareAsync();


            setText();


        }

        public void play() {//String path
//                Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/"+"music"+i);
            try {

                player.start(); //播放音乐
/*
                    player.reset(); //重置音乐播放器
                    //加载多媒体文件
                    player=MediaPlayer.create(getApplicationContext(),uri);
                    player.start(); //播放音乐
*/


                addTimer();//添加计时器
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void pausePlay() {
            player.pause();//暂停播放音乐
        }

        public void continuePlay() {
            player.start();//继续播放音乐
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
        if (player == null) return;
        if (player.isPlaying()) player.stop();//停止播放音乐
        player.release();//释放占用的资源
        player = null;//将player置为空

        if (musicReceiver != null) {
            //解除动态注册的广播
            unregisterReceiver(musicReceiver);
        }
    }
}
