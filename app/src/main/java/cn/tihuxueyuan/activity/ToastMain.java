package cn.tihuxueyuan.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.setting.CToast;
import cn.tihuxueyuan.utils.Constant;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class ToastMain extends AppCompatActivity {


    private CToast mCToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constant.mWM = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.layout_toast);
        inint();
    }

    private void inint() {
        findViewById(R.id.button_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int time= TextUtils.isEmpty(mEditText.getText().toString())?CToast.LENGTH_SHORT:Integer.valueOf(mEditText.getText().toString());
                int time= 1000;
                mCToast=CToast.makeText(getApplicationContext(), "我来自CToast!",time);
                mCToast.show();

//                showToast2();
//                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                View toast_view = inflater.inflate(R.layout.layout_toast, null);
//                Toast toast2 = new Toast(getApplicationContext());
//                toast2.setView(toast_view);
//                showMyToast(toast2, 10 * 1000);
            }
        });

    }


    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                toast.show();

                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    private void showtoast() {
//        Toast toast = Toast.makeText(this, "haha", Toast.LENGTH_LONG);
//        toast.show();

        Toast toast1 = Toast.makeText(this, "haha11111111", Toast.LENGTH_LONG);
        LinearLayout toast_linearlayout = (LinearLayout) toast1.getView();
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.mipmap.ic_launcher);
        toast_linearlayout.addView(iv, 0);
        toast1.show();

//        Fieldfield=toast.getClass().getDeclaredField("mTN");
//        field.setAccessible(true);
//        Objectobj=field.get(toast);
////TN对象中获得了show方法
//        Methodmethod=obj.getClass().getDeclaredMethod("show",null);
////调用show方法来显示Toast信息提示框
//        method.invoke(obj,null);
    }

    private void showToast2() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View toast_view = inflater.inflate(R.layout.layout_toast, null);
        Toast toast2 = new Toast(this);
        toast2.setView(toast_view);
//

//        try {
//            @SuppressLint("SoonBlockedPrivateApi") Field field = toast2.getClass().getDeclaredField("mTN");
//            field.setAccessible(true);
//            Object obj = field.get(toast2);
////TN对象中获得了show方法
//            Method method = obj.getClass().getDeclaredMethod("show", null);
////调用show方法来显示Toast信息提示框
//            method.invoke(obj, null);
//        } catch (Exception e) {
//        }
//
//        int i = 10;
//        toast2.setDuration(i);
        toast2.show();
    }

}