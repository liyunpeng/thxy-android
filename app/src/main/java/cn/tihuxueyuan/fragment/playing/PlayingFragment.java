package cn.tihuxueyuan.fragment.playing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import cn.tihuxueyuan.R;

public class PlayingFragment extends Fragment {
    private View zj;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        zj = inflater.inflate(R.layout.frag2_layout, null);
        ImageView listView = zj.findViewById(R.id.lv);
        return zj;

    }
}