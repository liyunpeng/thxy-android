package cn.tihuxueyuan.fragment.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collections;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.databinding.FragmentNotificationsBinding;
import cn.tihuxueyuan.utils.ComparatorValues;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.UpdateManager;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

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
                Constant.updateManager.updateSoftware( getContext());
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