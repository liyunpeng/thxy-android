package cn.tihuxueyuan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import cn.tihuxueyuan.utils.Constant;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        // 获得KeyEvent对象
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {

            // 获得按键码
            int keycode = event.getKeyCode();

            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //播放下一首
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    //播放上一首
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    //中间按钮,暂停or播放
                    //可以通过发送一个新的广播通知正在播放的视频页面,暂停或者播放视频
                    Log.d(TAG, " 耳机按键事件 中键");
                    Constant.musicControl.playOrPause();
                    break;
                default:
                    break;
            }
        }
    }
}
