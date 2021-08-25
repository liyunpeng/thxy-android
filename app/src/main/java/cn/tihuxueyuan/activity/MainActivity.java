package cn.tihuxueyuan.activity;

import android.os.Bundle;
import android.util.Log;
import cn.tihuxueyuan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.databinding.ActivityMainBinding;
import cn.tihuxueyuan.setting.AppConfig;
import cn.tihuxueyuan.utils.Constant;

public class MainActivity extends BaseActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());


        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        String appId = "android_" + AppConfig.getVersionName(MainActivity.this) + "_" + AppConfig.getAppMetaData(MainActivity.this, "UMENG_CHANNEL");

        Log.d(Constant.Tag, "onCreate: id:" + appId);
    }

}