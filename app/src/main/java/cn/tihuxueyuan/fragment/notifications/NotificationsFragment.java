package cn.tihuxueyuan.fragment.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.databinding.FragmentNotificationsBinding;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.LogcatHelper;
import cn.tihuxueyuan.utils.SettingItemBar;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        root.setcon(R.layout.user_main);
//        bind = ButterKnife.bind(this);

        root.findViewById(R.id.update_software).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( Constant.appData.serviceCurrentVersion != null && Constant.version.compareTo(Constant.appData.serviceCurrentVersion) > 0 ) {
                    Constant.updateManager.showUpdateSoftwareDialog( getContext());
                }else{
                    Toast.makeText(getContext(), "当前是最新版本，无需更新 ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SettingItemBar n = root.findViewById(R.id.about);
        n.setAboutText("版本号:" + Constant.version);

        root.findViewById(R.id.upload_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.uploadFile(LogcatHelper.PATH_LOGCAT + LogcatHelper.fileName);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}