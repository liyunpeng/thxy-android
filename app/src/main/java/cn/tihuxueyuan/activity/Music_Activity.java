package cn.tihuxueyuan.activity;

import static java.lang.Integer.parseInt;

import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.PLAY;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import java.io.IOException;

import cn.tihuxueyuan.R;

import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.fragment.list.ListFragment;
import cn.tihuxueyuan.globaldata.Data;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.service.MusicService;

public class Music_Activity extends AppCompatActivity implements View.OnClickListener {
    private static SeekBar sb;
    private static TextView tv_progress, tv_total, name_song;
    private ObjectAnimator animator;
    private String title;
    public MusicService.MusicControl musicControl;
    //    public MusicService musicService;
    String name;
    Intent intent1, intent2;
    MyServiceConn conn;
    String musicUrl;

    private boolean isUnbind = false;//记录服务是否被解绑
    Data app;

    private LiveDataBus.BusMutableLiveData<String> notificationLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
//        todo :标题居中
//        getSupportActionBar().

        title = (String) getIntent().getStringExtra("title");

        app = (Data) getApplication();
        app.currentPostion = getIntent().getIntExtra("current_position", 0);
        setTitle(title);
        intent1 = getIntent();
        init();

        //通知栏的观察者
        notificationObserver();
        //控制通知栏
        notificationLiveData = LiveDataBus.getInstance().with("notification_control", String.class);

    }

    ImageView playPauseView;

    private void init() {
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_total = (TextView) findViewById(R.id.tv_total);
        sb = (SeekBar) findViewById(R.id.sb);
        name_song = (TextView) findViewById(R.id.song_name);

        playPauseView = findViewById(R.id.play_pause);
        playPauseView.setOnClickListener(this);
        findViewById(R.id.play_previous).setOnClickListener(this);
        findViewById(R.id.play_next).setOnClickListener(this);
//        findViewById(R.id.btn_exit).setOnClickListener(this);

        name = intent1.getStringExtra("name");
        name_song.setText(name);
        intent2 = new Intent(this, MusicService.class);//创建意图对象
        conn = new MyServiceConn();//创建服务连接对象
        bindService(intent2, conn, BIND_AUTO_CREATE);//绑定服务
        //为滑动条添加事件监听
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变时，会调用此方法
                if (progress == seekBar.getMax()) {//当滑动条到末端时，结束动画
                    animator.pause();//停止播放动画
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//滑动条开始滑动时调用
//                int progress = seekBar.getProgress();//获取seekBar的进度
//                musicControl.seekTo(progress);//改变播放进度
//                int progress = seekBar.getProgress();//获取seekBar的进度
//                musicControl.setText();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//滑动条停止滑动时调用
                //根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress();//获取seekBar的进度
                musicControl.seekTo(progress);//改变播放进度
                if (musicControl.isPlaying() != true) {
                    musicControl.play();
                    playPauseView.setImageResource(R.drawable.stop);
                }

            }
        });
        ImageView iv_music = (ImageView) findViewById(R.id.iv_music);


//        String position= intent1.getStringExtra("position");
        String position = "1";
        int i = parseInt(position);
        iv_music.setImageResource(ListFragment.icons[i]);


        animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环

        musicUrl = getIntent().getStringExtra("music_url");
    }


    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.setCurrentActivity(Music_Activity.this);
    }

    public static Handler handler = new Handler() {//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg) {
            Log.d("tag1", "handleMessage: 处理消息 ： " + msg.toString());
            Bundle bundle = msg.getData();//获取从子线程发送过来的音乐播放进度
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            sb.setMax(duration);
            sb.setProgress(currentPosition);
            //歌曲总时长
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {//如果歌曲的时间中的分钟小于10
                strMinute = "0" + minute;//在分钟的前面加一个0
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + "";
            }
            tv_total.setText(strMinute + ":" + strSecond);
            //歌曲当前播放时长
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {//如果歌曲的时间中的分钟小于10
                strMinute = "0" + minute;//在分钟的前面加一个0
            } else {
                strMinute = minute + " ";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + " ";
            }
            tv_progress.setText(strMinute + ":" + strSecond);
        }
    };


    private LiveDataBus.BusMutableLiveData<String> activityLiveData;

    /**
     * 通知栏动作观察者
     */
    private void notificationObserver() {
        activityLiveData = LiveDataBus.getInstance().with("activity_control", String.class);
        activityLiveData.observe(Music_Activity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                Log.d("tag2", " onChanged state = " + state);
                switch (state) {
                    case PAUSE:
                        playPauseView.setImageResource(R.drawable.start);
                        break;
                    case PLAY:
//                    case PAUSE:
//                        btnPlay.setIcon(getDrawable(R.mipmap.icon_pause));
//                        btnPlay.setIconTint(getColorStateList(R.color.gold_color));
//                        BLog.d(TAG,state);
//                        changeUI(musicService.getPlayPosition());
//                        if (musicControl.isPlaying() != true) {
//                            musicControl.play();
//                   animator.start();
                        playPauseView.setImageResource(R.drawable.stop);
//                        } else {
////                            musicControl.pausePlay();
////                    animator.pause();
//                            playPauseView.setImageResource(R.drawable.start);
//                        }
                        break;

//                    case CLOSE:
//                        btnPlay.setIcon(getDrawable(R.mipmap.icon_play));
//                        btnPlay.setIconTint(getColorStateList(R.color.white));
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case PREV:
//                        BLog.d(TAG, "上一曲");
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case NEXT:
//                        BLog.d(TAG, "下一曲");
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case PROGRESS:
//                        //播放进度发生改变时,只改变进度，不改变其他
//                        musicProgress.setProgress(musicService.mediaPlayer.getCurrentPosition(), musicService.mediaPlayer.getDuration());
//                        break;
                    default:
                        break;
                }

            }
        });
    }

    public void startFloatingImageDisplayService() {
        if (FloatingImageDisplayService.isStarted) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
//            } else {
                startService(new Intent(Music_Activity.this, FloatingImageDisplayService.class));
//            }
        }
    }


    class MyServiceConn implements ServiceConnection {//用于实现连接服务

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d("tag1", "onServiceConnected: 服务连接成功 ");
//            musicService = (MusicService) service;
            musicControl = (MusicService.MusicControl) service;
            musicControl.init(musicUrl);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        musicControl.setText();
                        musicControl.play();
//                        animator.start();
                        playPauseView.setImageResource(R.drawable.stop);
                        musicControl.updateNotify(app.currentPostion);
                        startFloatingImageDisplayService();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                            handler.sendMessage();
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void unbind(boolean isUnbind) {
        if (!isUnbind) {//判断服务是否被解绑
            musicControl.pausePlay();//暂停播放音乐
            unbindService(conn);//解绑服务
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause://播放按钮点击事件
//                String position=intent1.getStringExtra("position");
//                int i=parseInt(position);
//                musicControl.play(i);
                if (musicControl.isPlaying() != true) {
                    musicControl.play();
//                   animator.start();
                    playPauseView.setImageResource(R.drawable.stop);
                } else {
                    musicControl.pausePlay();
//                    animator.pause();
                    playPauseView.setImageResource(R.drawable.start);
                }
                break;
            case R.id.play_previous:
                if (app.currentPostion <= 0) {
                    app.currentPostion = 0;
                } else {
                    app.currentPostion--;
                    playNextPrevious();
                    setTitle(app.mList.get(app.currentPostion).getTitle().split("\\.")[0]);
                }

                break;
            case R.id.play_next:
                if (app.currentPostion >= (app.mList.size() - 1)) {
                    app.currentPostion = (app.mList.size() - 1);
                } else {
                    app.currentPostion++;
                    playNextPrevious();
                    setTitle(app.mList.get(app.currentPostion).getTitle().split("\\.")[0]);
                }

                break;


//            case R.id.btn_pause://暂停按钮点击事件
//
//                break;
//            case R.id.btn_continue_play://继续播放按钮点击事件
//                musicControl.continuePlay();
//                animator.start();
//                break;
//            case R.id.btn_exit://退出按钮点击事件
//                unbind(isUnbind);
//                isUnbind = true;
//                finish();
//                break;
        }

        musicControl.updateNotify(app.currentPostion);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind(isUnbind);
        isUnbind = true;
        finish();
    }

    private void playNextPrevious() {

        CourseFileList.CourseFile c = app.mList.get(app.currentPostion);
        String musicUrl = c.getMp3url() + "?fileName=" + c.getMp3FileName();
        musicControl.init(musicUrl);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    musicControl.setText();
                    musicControl.play();
//                        animator.start();
                    playPauseView.setImageResource(R.drawable.stop);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


