package teabar.ph.com.teabar.activity;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.TabFragmentPagerAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.fragment.GuideFragment;

public class GuideActivity extends BaseActivity {
    ViewPager viewPager;
    List<Fragment> guildList;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_guide;
    }

    @Override
    public void initView(View view) {
        viewPager = (ViewPager) findViewById(R.id.guild_viewpager);
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




}
