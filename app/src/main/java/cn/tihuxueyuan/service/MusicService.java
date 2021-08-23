package cn.tihuxueyuan.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import cn.tihuxueyuan.activity.Music_Activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public MediaPlayer player;
    public Timer timer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();//创建音乐播放器对象

    }

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
    }
}
