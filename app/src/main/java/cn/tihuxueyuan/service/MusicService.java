package cn.tihuxueyuan.service;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static cn.tihuxueyuan.utils.Constant.NEWPLAY;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.CONTINURE_PLAY;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
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

import cn.tihuxueyuan.activity.MusicActivity;
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
    public MediaPlayer mPlayer;
    public Timer mTimer;
    private LiveDataBus.BusMutableLiveData<String> mMusicActivityLiveData;
    private LiveDataBus.BusMutableLiveData<String> mBaseActivityFloatLiveData;
    public static Notification mNotification;
    public static RemoteViews mNotificationRemoteViews;
    public static int NOTIFICATION_ID = 1;
    public static NotificationManager mNotificationManager;
    private AudioManager mAudioManager;
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver myNoisyAudioStreamReceiver;
    private AppData appData;
    private int mCurrentCourseId;

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_AUDIO_BECOMING_NOISY 触发 ");
                // Pause the playback
            }
        }
    }

    public MusicService() {
//        myNoisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
    }

//    private void startPlayback() {
//        registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
//    }
//
//    private void stopPlayback() {
//
//        unregisterReceiver(myNoisyAudioStreamReceiver);
//    }

    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                        Log.d(TAG, " AUDIOFOCUS_LOSS_TRANSIENT 事件，音乐暂停 ");
                        musicControl.pause();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        Log.d(TAG, " AUDIOFOCUS_GAIN 事件, 不要恢复播放");
//                        musicControl.play();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        Log.d(TAG, " AUDIOFOCUS_LOSS 事件, 音乐暂停");
//                        am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);

                        mAudioManager.abandonAudioFocus(afChangeListener);
                        musicControl.pause();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        Log.d(TAG, " AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK 事件, 无动作");
// Lower the volume
                    }
                }

            };

    private void initAudioLoseListener() {
        Context mContext = getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
// Request audio focus for playback
        int result = mAudioManager.requestAudioFocus(afChangeListener,
// Use the music stream.
                AudioManager.STREAM_MUSIC,
// Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            am.registerMediaButtonEventReceiver(recei);
// Start playback.
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MusicService onCreate");
        initNotificationRemoteViews();
        initNotification();
        appData = Constant.appData;
        mPlayer = new MediaPlayer();
        initAudioLoseListener();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int pos = appData.playingCourseFileMap.get(appData.playingCourseFileId).getListenedPosition();
                int percent = appData.playingCourseFileMap.get(appData.playingCourseFileId).getListenedPercent();
                mCurrentCourseId = appData.playingCourseFileMap.get(appData.playingCourseFileId).getCourseId();
                String fileName = appData.playingCourseFileMap.get(appData.playingCourseFileId).getFileName();
                Constant.currentMusicName = SPUtils.getTitleFromName(fileName);
                Constant.appData.lastCourseFileId = Constant.appData.playingCourseFileId;
                Log.d(TAG, " player onPrepared...");
                if (pos > 0) {
                    Log.d(TAG, "播放器 文件名=" + fileName + "  percent=" + percent + ",  seek to 的位置 = " + pos);
                    if (percent == 100) {
                        Log.d(TAG, " 上次已听完， 这次seekto开始位置，重新听 ");
                        mPlayer.seekTo(0);
                    } else {
                        Log.d(TAG, " seek to pos= " + pos);
                        mPlayer.seekTo(pos);
                    }
                } else {
                    Log.d(TAG, "pos=0, percent=" + percent +
                            ", currentCourseFileId=" + appData.playingCourseFileId +
                            ", fileName=" + fileName);
                    mPlayer.seekTo(0);

                }
                mPlayer.start();
                musicControl.setText();
                addTimer();

                // 在播放器状态确定好之后，再显示通知栏，以保证应用界面和通知栏按钮状态一致
                updateNotificationShow(Constant.currentMusicName);
                mBaseActivityFloatLiveData.postValue(Constant.currentMusicName);
                mMusicActivityLiveData.postValue(NEWPLAY);
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, " 切换 下一首 时发生错误 ， 错误被拦截， 不会回调到onCompletion ");
                return true;
            }
        });

        mPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG, "音乐回调函数 onCompletion 调用");
//                        if (true){
//                            return;
//                        }
                        SPUtils.sendListenedPerscent();

                        if (appData.playingCourseFileListPostion >= (appData.playingCourseFileList.size() - 1)) {
                            Log.d(TAG, "音乐回调函数 onCompletion 调用 currentPostion  赋值");
                            appData.playingCourseFileListPostion = (appData.playingCourseFileList.size() - 1);
                        } else {
                            appData.playingCourseFileListPostion++;
                            appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
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
        mMusicActivityLiveData = LiveDataBus.getInstance().with(Constant.MusicLiveDataObserverTag, String.class);
        mBaseActivityFloatLiveData = LiveDataBus.getInstance().with(Constant.BaseActivityFloatTextViewDataObserverTag, String.class);
    }


    private void initNotificationRemoteViews() {
        mNotificationRemoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(CONTINURE_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent);
    }

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
        mNotification = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.thxy)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCustomContentView(mNotificationRemoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }

    public void updateNotificationShow(String musicTitle) {
        if (mNotificationRemoteViews == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mNotificationRemoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
        } else {
            mNotificationRemoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
        }

        if (mCurrentCourseId != appData.lastCourseId) {
            Log.d(TAG, " 更新通知栏 当前currentCourseId 与上次 记录的 courseId 不同，走网络获取图片");
            SPUtils.httpGetCourseImage(getApplicationContext(), appData.playingCourseId, null);
        } else {
            mNotificationRemoteViews.setImageViewBitmap(R.id.notification_img, appData.notificationBitMap);
        }

        mNotificationRemoteViews.setTextViewText(R.id.tv_notification_song_name, musicTitle);

        //歌手名
//        remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getSinger());
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);

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
        mNotificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);
    }

    public void closeNotification() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }
        mNotificationManager.cancel(NOTIFICATION_ID);
        mMusicActivityLiveData.postValue(CLOSE);
    }

    public void addTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (mPlayer == null)
                        return;
                    int duration = mPlayer.getDuration();
                    int currentPosition = mPlayer.getCurrentPosition();
                    Message msg = MusicActivity.mMusicActivityHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    msg.setData(bundle);
                    MusicActivity.mMusicActivityHandler.sendMessage(msg);
                }
            };
            // 每5秒开始，每500毫秒执行
            mTimer.schedule(task, 5, 500);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(MusicService.this, "App要退出了", Toast.LENGTH_SHORT).show();
    }

    public class MusicControl extends Binder {
        public void release() {
            if (mPlayer == null)
                return;
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mNotificationManager.cancel(NOTIFICATION_ID);
        }

        public int getCurrentCourseId() {
            return mCurrentCourseId;
        }

        public void playListened(String action) {
            boolean isPlaying = mPlayer.isPlaying();
            Log.d(TAG, "播放器是否在播放 isPlaying = " + isPlaying);
            if ((action == CONTINURE_PLAY || action == PAUSE) && isPlaying && appData.lastCourseFileId == appData.playingCourseFileId) {
                Log.d(TAG, "不初始化播放器, 也不播放 ");
//                player.pause();

            } else {
                if (action == NEWPLAY  ) {
                    String mp3url = SPUtils.getImgOrMp3Url(appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getCourseId(), appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getMp3FileName());
                    Log.d(TAG, "初始化播放器 ");
                    initPlayer(mp3url);
//                    musicActivityLiveData.postValue(action);
                }else if ( action == CONTINURE_PLAY && !isPlaying ) {
                    Log.d(TAG, "不初始化播放器， 直接播放");
                    mPlayer.start();
//                    musicActivityLiveData.postValue(action);
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
//            mMusicActivityLiveData.postValue(action);
            updateOtherActivity(action);
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
                case CONTINURE_PLAY:
                case PAUSE:
                    playOrPause();
                    break;

                case PREV:
                    if (appData.playingCourseFileListPostion <= 0) {
                        appData.playingCourseFileListPostion = 0;
                    } else {
                        appData.playingCourseFileListPostion--;
                        appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
                        musicControl.playListened(NEWPLAY);
                    }
                    break;
                case NEXT:
                    if (appData.playingCourseFileListPostion >= (appData.playingCourseFileList.size() - 1)) {
                        appData.playingCourseFileListPostion = (appData.playingCourseFileList.size() - 1);
                    } else {
                        appData.playingCourseFileListPostion++;
                        appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
                        musicControl.playListened(NEWPLAY);
                    }
                    break;


//
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
            return mPlayer.getCurrentPosition();
        }

        public int getDuration() {
            return mPlayer.getDuration();
        }

        public int getListenedPercent() {
            if (mPlayer == null) return 0;

            float duration = mPlayer.getDuration();//获取歌曲总时长
            float currentPosition = mPlayer.getCurrentPosition();//获取播放进度
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
            if (mPlayer == null) return;

            int duration = mPlayer.getDuration();//获取歌曲总时长
            int currentPosition = mPlayer.getCurrentPosition();//获取播放进度
            Message msg = MusicActivity.mMusicActivityHandler.obtainMessage();//创建消息对象
            //将音乐的总时长和播放进度封装至消息对象中
            Bundle bundle = new Bundle();
            bundle.putInt("duration", duration);
            bundle.putInt("currentPosition", currentPosition);
            msg.setData(bundle);
            //将消息发送到主线程的消息队列
            MusicActivity.mMusicActivityHandler.sendMessage(msg);
        }

        public void initPlayer(String url) {
            if (appData.lastCourseFileId == appData.playingCourseFileId) {
                Log.d(TAG, " musicControl init 调用， 因为appData.lastCourseFileId == appData.currentCourseFileId， play不需要reset 和 setDataSource");
                return;
            }

            if (mPlayer.isLooping()) {
                mPlayer.stop();
                mPlayer.release();
                Log.d(TAG, "musicControl init 调用， 执行player.stop() player.release()");
            }

            Log.d(TAG, "musicControl init 调用， 执行player.reset()");
            mPlayer.reset();
            try {
                mPlayer.setDataSource(url);
                mPlayer.prepareAsync();
//                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void play() {
//            registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
            mPlayer.start();
            addTimer();
            updateOtherActivity(CONTINURE_PLAY);
        }

        public void pause() {
//            unregisterReceiver(myNoisyAudioStreamReceiver);
            if (mPlayer != null) {
                mPlayer.pause();
                updateOtherActivity(PAUSE);
            }
        }

        public void playOrPause() {
            Log.d(TAG, "playOrPause 调用 ");

            String action;
            if (mPlayer.isPlaying()) {
//                player.pause();
//                action = PAUSE;
//
                pause();
            } else {
//                player.start();
//                addTimer();
//                action = PLAY;
                play();
            }
//            updateOtherActivity(action);
        }

        private void updateOtherActivity(String action) {
            if (mBaseActivityFloatLiveData != null)
                mBaseActivityFloatLiveData.postValue(Constant.currentMusicName);
            if (mMusicActivityLiveData != null)
                mMusicActivityLiveData.postValue(action);
            updateNotificationShow(Constant.currentMusicName);
        }

        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        public boolean isActivie() {
            return mPlayer.isLooping();
        }

        public void seekTo(int progress) {
            mPlayer.seekTo(progress);//设置音乐的播放位置
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MusicService onDestroy");
    }
}