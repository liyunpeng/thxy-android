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

import java.util.Collections;

import cn.tihuxueyuan.R;


public class nav_bar extends LinearLayout  {
    private  Context con;
    private int inputview_input_icon;
    private String inputview_input_hint;
    private boolean inputview_is_pass;

    private int right_icon;
    private View inflate;
    ImageView imageView;
    TextView editText;
    private View tv_search;

    public nav_bar(@NonNull Context context) {
        super(context);
        init(context, null);
        this.con=context;
    }

    public nav_bar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public nav_bar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
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
        editText=  (TextView)inflate.findViewById(R.id.title);

        Constant.updateManager = new UpdateManager(getContext());
        editText.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.updateManager.checkUpdateInfo( getContext());
            }
        });

        imageView=  (ImageView)inflate.findViewById(R.id.back);
        imageView.setImageResource(inputview_input_icon);
        editText.setText(inputview_input_hint);
        //editText.setInputType(inputview_is_pass?);
        addView(inflate);

    }


}
