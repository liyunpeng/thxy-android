package cn.tihuxueyuan.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.model.CourseTypeList.CourseType;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class TabAdapterA implements TabAdapter {
    public  List<CourseType> titles;


    {
        titles = new ArrayList<>();
//        titles.add("头条");
//        titles.add("社会");
//        titles.add("国际");
//        titles.add("娱乐");
//        titles.add("体育");
//        titles.add("军事");
//        titles.add("科技");
//        titles.add("财经");
//        titles.add("时尚");
//                Collections.addAll(titles);
    }
    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public TabView.TabBadge getBadge(int position) {
        return null;
    }

    @Override
    public QTabView.TabIcon getIcon(int position) {
        return null;
    }

    @Override
    public QTabView.TabTitle getTitle(int position) {
        return new QTabView.TabTitle.Builder()
                .setContent(titles.get(position).getName())
//                        .setTextColor(Color.BLUE, Color.BLACK)
                .build();
    }

    @Override
    public int getBackground(int position) {
        return 0;
    }
}
