package cn.tihuxueyuan.utils;

import android.content.res.TypedArray;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.tihuxueyuan.R;


public class SettingItemBar extends LinearLayout  {
    private  Context con;
    private int inputview_input_icon;
    private String inputview_input_hint;
    private boolean inputview_is_pass;

    private int right_icon;
    private View inflate;
    ImageView imageView;
    TextView textView;
    TextView aboutTextView;
    private View tv_search;

    public SettingItemBar(@NonNull Context context) {
        super(context);
        init(context, null);
        this.con=context;
    }

    public SettingItemBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingItemBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setAboutText(String s){
        textView.setText(s);
    }
    private void init(Context context, AttributeSet attrs) {
        if(attrs==null){
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.nav);
        inputview_input_icon = typedArray.getResourceId(R.styleable.nav_icon, R.mipmap.ic_launcher);
        right_icon = typedArray.getResourceId(R.styleable.nav_right_icon, R.mipmap.ic_launcher);
        inputview_input_hint = typedArray.getString(R.styleable.nav_hint);
        typedArray.recycle();

        inflate = LayoutInflater.from(context).inflate(R.layout.nav_bar, this, false);
        //imageView=  (ImageView)inflate.findViewById(R.id.tou);
        textView =  inflate.findViewById(R.id.title);

        Constant.updateManager = new UpdateManager(getContext());

        imageView=  inflate.findViewById(R.id.back);
        imageView.setImageResource(inputview_input_icon);
        textView.setText(inputview_input_hint);
        //editText.setInputType(inputview_is_pass?);
        addView(inflate);
    }
}
