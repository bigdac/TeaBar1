package teabar.ph.com.teabar.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.TabFragmentPagerAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.GuideFragment;

/**
 * 引导页 使用ViewPager与FragmentPagerAdapter来处理引导页
 */
public class GuideActivity extends BaseActivity {
    ViewPager viewPager;
    List<Fragment> guildList;
    MyApplication application;
    LinearLayout linearout;//引导页的指示器布局
    MyOnPageChangeListener listener;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_guide;
    }
    private void setLang(Locale l) {
        // 获得res资源对象
        Resources resources = getResources();
        // 获得设置对象
        Configuration config = resources.getConfiguration();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 语言
        config.locale = l;
        resources.updateConfiguration(config, dm);
        // 刷新activity才能马上奏效
        startActivity(new Intent().setClass(GuideActivity.this,
                GuideActivity.class));
        finish();
    }
    /**
     * 初始化引导页和加载引导页fragment
     * @param view
     */
    @Override
    public void initView(View view) {
//        setLang(Locale.ENGLISH);
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        viewPager = (ViewPager) findViewById(R.id.guild_viewpager);
        linearout=findViewById(R.id.linearout);
        guildList = new ArrayList<>();
        Bundle bundle = new Bundle();
        // 步骤5:往bundle中添加数据
        for (int i=0 ;i<3;i++){
            GuideFragment guideFragment = new GuideFragment();
            guideFragment.setType(i);
            guildList.add(guideFragment);
        }

        FragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), guildList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        listener = new MyOnPageChangeListener(this, viewPager, linearout, 3);
        viewPager.addOnPageChangeListener(listener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }


    //实现页面变化监听器OnPageChangeListener
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private Context context;
        private ViewPager viewPager;
        private LinearLayout dotLayout;
        private int size;
        private List<ImageView> dotViewLists = new ArrayList<>();

        public MyOnPageChangeListener(Context context, ViewPager viewPager, LinearLayout dotLayout, int size) {
            this.context = context;
            this.viewPager = viewPager;
            this.dotLayout = dotLayout;
            this.size = size;

            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                //为小圆点左右添加间距
                params.leftMargin = 10;
                params.rightMargin = 10;
                if (i == 0) {
                    imageView.setBackgroundResource(R.mipmap.img_glide_checked);
                } else {
                    imageView.setBackgroundResource(R.mipmap.img_glide_unchecked);
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //为LinearLayout添加ImageView
                dotLayout.addView(imageView, params);
                dotViewLists.add(imageView);
            }
        }


        @Override
        //当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法会一直得到调用。
        /**
         * arg0:当前页面，及你点击滑动的页面
         * arg1:当前页面偏移的百分比
         *arg2:当前页面偏移的像素位置
         */
        public void onPageScrolled(int position, float arg1, int arg2) {
            int versible=0;
            for (int i = 0; i < size; i++) {
                //选中的页面改变小圆点为选中状态，反之为未选中
                if (position%size==2){
                    dotLayout.setVisibility(View.GONE);
                }else {
                    dotLayout.setVisibility(View.VISIBLE);
                    versible=1;
                }
                if (versible==1){
                    if ((position % size) == i) {
                        ((View) dotViewLists.get(i)).setBackgroundResource(R.mipmap.img_glide_checked);
                    } else {
                        ((View) dotViewLists.get(i)).setBackgroundResource(R.mipmap.img_glide_unchecked);
                    }
                }

            }
        }

        @Override
        //当页面状态改变的时候调用
        /**
         * arg0
         *  1:表示正在滑动
         *  2:表示滑动完毕
         *  0:表示什么都没做，就是停在那
         */
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        //页面跳转完后调用此方法
        /**
         * arg0是页面跳转完后得到的页面的Position（位置编号）。
         */
        public void onPageSelected(int position) {
//            Message msg = handler.obtainMessage();
//            msg.what = position;
//            handler.sendMessage(msg);

        }
    }


}
