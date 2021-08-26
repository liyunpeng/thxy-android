package cn.tihuxueyuan.basic;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.utils.Constant;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        String className = this.getLocalClassName();
        Log.d(Constant.TAG, " name ="+ this.getLocalClassName());
        if (Constant.floatingControl != null) {
            if (className.contains("Music")){
                Constant.floatingControl.setVisibility(false);
            }else{
                Constant.floatingControl.setVisibility(true);
            }
        }

    }
}
