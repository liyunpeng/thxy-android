package cn.tihuxueyuan.receiver;

import android.app.Activity;
//import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.tihuxueyuan.basic.ActivityManager;


//import com.llw.goodmusic.basic.MyActivityManager;
//import com.llw.goodmusic.utils.BLog;
//

/**
 * 通知点击广播接收器  跳转到栈顶的Activity ,而不是new 一个新的Activity
 *
 * @author llw
 */
public class NotificationClickReceiver extends BroadcastReceiver {

    public static final String TAG = "tag2";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"通知栏点击");

        //获取栈顶的Activity
        Activity currentActivity = ActivityManager.getCurrentActivity();
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, currentActivity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }
}
