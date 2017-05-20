package com.riq.basebottomtablib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by riq on 2017/5/19.
 */

public class BottomTabView extends LinearLayout {

    /**
     * 记录最新的选择位置
     */
    private int lastPosition = -1;

    /**
     * 所有 TabItem 的集合
     */
    private List<TabItemView> tabItemViews;

    public BottomTabView(Context context) {
        super(context);
    }

    public BottomTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public BottomTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 连接 Viewpager
     *
     * @param viewPager
     */
    public void setupWithViewPager(final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updatePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setOnTabItemSelectListener(new OnTabItemSelectListener() {
            @Override
            public void onTabItemSelect(int position) {
                viewPager.setCurrentItem(position);
            }
        });
    }

    /**
     * 设置 Tab Item View
     */
    public void setTabItemViews(List<TabItemView> tabItemViews) {
        setTabItemViews(tabItemViews, null);
    }

    /**
     * 设置 Tab Item View
     */
    public void setTabItemViews(List<TabItemView> tabItemViews, View centerView) {
        if (this.tabItemViews != null) {
            throw new RuntimeException("不能重复设置！");
        }
        if (tabItemViews == null || tabItemViews.size() < 2) {
            throw new RuntimeException("TabItemView 的数量必须大于2！");
        }
        this.tabItemViews = tabItemViews;
        for (int i = 0; i < tabItemViews.size(); i++) {
            if (centerView != null && i == tabItemViews.size() / 2) {
                this.addView(centerView);
            }
            final TabItemView tabItemView = tabItemViews.get(i);
            this.addView(tabItemView);
            final int finalI = i;
            tabItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalI == lastPosition) {
                        // 第二次点击
                        if (onSecondSelectListener != null) {
                            onSecondSelectListener.onSecondSelect(finalI);
                        }
                        return;
                    }
                    updatePosition(finalI);
                    if (onTabItemSelectListener != null) {
                        onTabItemSelectListener.onTabItemSelect(finalI);
                    }
                }
            });
        }

        /**
         * 将所有的 TabItem 设置为 初始化状态
         */
        for (TabItemView tab : tabItemViews) {
            tab.setStatus(TabItemView.DEFAULT);
        }

        /**
         * 默认状态选择第一个
         */
        updatePosition(0);
    }

    /**
     * 更新被选中 Tab Item 的状态
     * 恢复上一个 Tab Item 的状态
     */
    public void updatePosition(int position) {
        if (lastPosition != position) {
            if (tabItemViews != null && tabItemViews.size() != 0) {
                tabItemViews.get(position).setStatus(TabItemView.PRESS);
                if (lastPosition != -1) {
                    tabItemViews.get(lastPosition).setStatus(TabItemView.DEFAULT);
                }
                lastPosition = position;
            } else {
                throw new RuntimeException("please setTabItemViews !");
            }
        }
    }

    private OnTabItemSelectListener onTabItemSelectListener;
    private OnSecondSelectListener onSecondSelectListener;

    public void setOnTabItemSelectListener(OnTabItemSelectListener onTabItemSelectListener) {
        this.onTabItemSelectListener = onTabItemSelectListener;
    }

    public void setOnSecondSelectListener(OnSecondSelectListener onSecondSelectListener) {
        this.onSecondSelectListener = onSecondSelectListener;
    }

    /**
     * 第二次被选择的监听器
     */
    public interface OnSecondSelectListener {
        void onSecondSelect(int position);
    }

    /**
     * 第一次被选择的监听器
     */
    public interface OnTabItemSelectListener {
        void onTabItemSelect(int position);
    }


    /**
     * Item
     */
    public static class TabItemView extends LinearLayout {

        /**
         * 两个状态 选中、未选中
         */
        public final static int DEFAULT = 0;
        public final static int PRESS = 1;

        /**
         * Item 的标题
         */
        public String title;
        /**
         * 标题的两个状态的颜色 选中、未选中
         */
        public int textColorDefault;    //默认文字颜色
        public int textColorPress;      //点击后的颜色

        /**
         * 两个图标的 资源 id ，选中、未选中
         */
        public int iconResDefault;      //默认图标
        public int iconResPress;        //点击后图标

        public TextView tvTitle;        //文字
        public ImageView ivIcon;        //图标


        public TabItemView(Context context) {
            super(context);
        }

        /**
         * @param title            标题
         * @param leftPadding      图标padding
         * @param textColorDefault 标题默认颜色
         * @param textColorPress   标题被选中时的颜色
         * @param iconResDefault   默认图标
         * @param iconResPress     被选中时的图标
         */
        public TabItemView(Context context, String title, int leftPadding, int topPadding, int rightPadding
                , int bottomPadding, int textColorDefault, int textColorPress, int iconResDefault, int iconResPress) {
            super(context);

            this.title = title;
            this.textColorDefault = textColorDefault;
            this.textColorPress = textColorPress;
            this.iconResDefault = iconResDefault;
            this.iconResPress = iconResPress;
            View view = LayoutInflater.from(super.getContext()).inflate(R.layout.view_tab_item, this);
            // TODO: 2017/5/20 去掉点击的背景效果
            LinearLayout viewTabView = (LinearLayout) findViewById(R.id.viewTabView);
            viewTabView.setBackgroundResource(R.color.transparent);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            view.setLayoutParams(layoutParams);
            // TODO: 2017/5/20 如果设置标题为空，则只显示图标
            if (title.trim().equals("")) {
                tvTitle.setVisibility(GONE);
            } else {
                tvTitle.setText(title);
            }
            view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        }

        /**
         * 设置状态
         */
        private void setStatus(int status) {
            tvTitle.setTextColor(ContextCompat.getColor(super.getContext(), status == PRESS ? textColorPress : textColorDefault));
            ivIcon.setImageResource(status == PRESS ? iconResPress : iconResDefault);
        }
    }
}
