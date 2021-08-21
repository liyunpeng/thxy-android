package cn.tihuxueyuan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends ViewPager {
    public VerticalViewPager(Context context) {
        this(context, null);
    }
    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置viewpage的切换动画,这里设置才能真正实现垂直滑动的viewpager
//        setPageTransformer(true, new DefaultTransformer());
    }
    /**
     * 拦截touch事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(swapEvent(ev));
        swapEvent(ev);
        return intercept;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapEvent(ev));
    }
    private MotionEvent swapEvent(MotionEvent event) {
        //获取宽高
        float width = getWidth();
        float height = getHeight();
        //将Y轴的移动距离转变成X轴的移动距离
        float swappedX = (event.getY() / height) * width;
        //将X轴的移动距离转变成Y轴的移动距离
        float swappedY = (event.getX() / width) * height;
        //重设event的位置
        event.setLocation(swappedX, swappedY);
        return event;
    }

    /**
     * a.关联TabLayout和ViewPager
     * b.创建TabLayout的数据适配器
     * c.设置TabLayout的数据适配器
     */
//    private void bindTabAndPager(List<Classfiy> classfiys){
//        tabLayout.setupWithViewPager(initViewPager(classfiys));
//        ClassfiyMenuTabAdapter classfiyMenuTabAdapter =
//                new ClassfiyMenuTabAdapter(classfiys);
//        tabLayout.setTabAdapter(classfiyMenuTabAdapter);
//    }
//
//    private ViewPager initViewPager(List<Classfiy> classfiys) {
//        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
//            @Override
//            public Fragment getItem(int i) {
//                return ClassfiyFragment.newInstance(classfiys.get(i));
//            }
//            @Override
//            public int getCount() {
//                return classfiys.size();
//            }
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return classfiys.get(position).getMainName();
//            }
//        });
//        return viewPager;
//    }
}