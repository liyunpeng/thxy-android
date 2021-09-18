package cn.tihuxueyuan.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.utils.Constant;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }
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

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constant.TAG, " onDestroy className ="+ this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
//        String className = this.getLocalClassName();
//        if (Constant.floatingControl != null) {
//            if (className.contains("Music")){
//                Constant.floatingControl.setVisibility(false);
//            }else{
//                Constant.floatingControl.setVisibility(true);
//            }
//        }
    }
}
