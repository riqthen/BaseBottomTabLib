package com.riq.basebottomtablib;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * 使用方法及功能说明：
 * setVpScrollable() 设置ViewPager是否可滑动
 * setCenterView() 设置中间按钮
 * onCenterViewClick() 中间按钮的点击事件
 * onCenterViewLongClick() 中间按钮的长按事件
 * onSecondClick() 选定了按钮，再次点击的事件
 */
public abstract class BaseBottomTabActivity extends AppCompatActivity {

    private MyViewPager mvp;
    private BottomTabView btvTab;
    private LinearLayout lyMain;
    private boolean isOutTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        lyMain = (LinearLayout) findViewById(R.id.ly_main);
        btvTab = (BottomTabView) findViewById(R.id.btv_tab);
        mvp = (MyViewPager) findViewById(R.id.mvp);
        //是否有CenterView的情况
        if (setCenterView(iconRes, iconWidth, iconHeight, leftMargin, rightMargin, isOutTab, null, null) == null) {
            btvTab.setTabItemViews(getTabViews());
        } else {
            btvTab.setTabItemViews(getTabViews(), setCenterView(iconRes, iconWidth, iconHeight, leftMargin, rightMargin, isOutTab, null, null));
        }
        // TODO: viewPager是否可滑动  Follow：1 --->
        mvp.setScrollable(isVpScrollable());
        mvp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        btvTab.setOnTabItemSelectListener(new BottomTabView.OnTabItemSelectListener() {
            @Override
            public void onTabItemSelect(int position) {
                mvp.setCurrentItem(position);
            }
        });
        mvp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                btvTab.updatePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //CenterView是否溢出Tab的情况
        if (isOutTab) {
            btvTab.setGravity(Gravity.BOTTOM);
            lyMain.setClipChildren(false);
        } else {
            btvTab.setGravity(Gravity.CENTER);
            lyMain.setClipChildren(true);
        }
        // TODO: 2017/5/20 设置底部栏整体的padding，直接在xml中设置
//        setBottomTabViewPadding(left, top, right, bottom);
        // TODO: 2017/5/20 已选择之后再点击
        btvTab.setOnSecondSelectListener(new BottomTabView.OnSecondSelectListener() {
            @Override
            public void onSecondSelect(int position) {
                onSecondClick();
            }
        });
//        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(this, setBottomTabHeight()));
//        btvTab.setLayoutParams(params);
    }

    /**
     * @return 默认viewPager不可滑动
     */
    public boolean isVpScrollable() {
        return false;
    }

//    /**
//     * 设置底部栏的高度，可在xml中设置
//     *
//     * @return 默认50dp
//     */
//    public int setBottomTabHeight() {
//        return 50;
//    }

    /**
     * 设置底部tab按钮
     *
     * @return
     */
    protected abstract List<BottomTabView.TabItemView> getTabViews();

    /**
     * 设置fragment
     *
     * @return
     */
    protected abstract List<Fragment> getFragments();

    /**
     * 按钮选择之后，再次点击的效果
     */
    public void onSecondClick() {
    }


//    /**
//     * 设置底部栏整体的Padding，可在xml中设置
//     *
//     * @param left
//     * @param top
//     * @param right
//     * @param bottom
//     */
//    private int left;
//    private int top;
//    private int right;
//    private int bottom;
//
//    public void setBottomTabViewPadding(int left, int top, int right, int bottom) {
//        this.left = left;
//        this.top = top;
//        this.right = right;
//        this.bottom = bottom;
//        btvTab.setPadding(left, top, right, bottom);
//    }


    private int iconRes;
    private int iconWidth;
    private int iconHeight;
    private int leftMargin;
    private int rightMargin;

    /**
     * 设置CenterView按钮
     *
     * @param iconRes             按钮图片
     * @param iconWidth           图片宽 ViewGroup.LayoutParams.WRAP_CONTENT
     * @param iconHeight          图片高
     * @param leftMargin          图片左Margin
     * @param rightMargin         图片右Margin
     * @param isOutTab            是否溢出边缘
     * @param onClickListener     点击事件
     * @param onLongClickListener 长按事件
     * @return
     */
    public View setCenterView(@DrawableRes int iconRes, int iconWidth, int iconHeight, int leftMargin, int rightMargin, boolean isOutTab, final View.OnClickListener onClickListener, final View.OnLongClickListener onLongClickListener) {
        if (iconRes == 0)
            return null;
        this.iconRes = iconRes;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.isOutTab = isOutTab;

        ImageView centerView = new ImageView(this);
        centerView.setImageResource(iconRes);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconWidth, iconHeight);
        params.leftMargin = leftMargin;
        params.rightMargin = rightMargin;
        centerView.setLayoutParams(params);
        //CenterView按钮点击事件
        centerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
            }
        });
        //CenterView按钮长按事件
        centerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongClickListener != null) {
                    onLongClickListener.onLongClick(v);
                }
                return true;
            }
        });
        return centerView;
    }

    //适配器
    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragments().get(position);
        }

        @Override
        public int getCount() {
            return getFragments().size();
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
}
