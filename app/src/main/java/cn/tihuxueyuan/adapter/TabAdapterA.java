package cn.tihuxueyuan.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class TabAdapterA implements TabAdapter {
    public  List<CourseType> titles;


    {
        titles = new ArrayList<>();
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
                .setContent(titles.get(position).getName()).setTextColor(0xFF757575, 0xFFff0000).setTextSize(20)
//                        .setTextColor(Color.BLUE, Color.BLACK)
                .build();
    }

    @Override
    public int getBackground(int position) {
        return 0;
    }
}
