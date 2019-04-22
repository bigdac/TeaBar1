package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.ClickViewPageAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.EqumentFragment;
import teabar.ph.com.teabar.fragment.FriendCircleFragment1;
import teabar.ph.com.teabar.fragment.FriendFragment;
import teabar.ph.com.teabar.fragment.MailFragment;
import teabar.ph.com.teabar.fragment.MainFragment;
import teabar.ph.com.teabar.fragment.MainFragment1;
import teabar.ph.com.teabar.fragment.MainFragment2;
import teabar.ph.com.teabar.fragment.MyselfFragment;
import teabar.ph.com.teabar.fragment.SocialFragment;
import teabar.ph.com.teabar.fragment.TeaFragment;
import teabar.ph.com.teabar.pojo.FriendInfor;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

public class MainActivity extends BaseActivity implements FriendCircleFragment1.hidenShowView {
    @BindView(R.id.main_viewPage)
    NoSrcollViewPage main_viewPage;
    @BindView(R.id.main_tabLayout)
    TabLayout main_tabLayout;
    List<String> mainMemu = new ArrayList<>();
    List<BaseFragment> fragmentList = new ArrayList<>();
    MyApplication application;
    MainFragment2 mainFragment ;
    EqumentFragment equmentFragment ;
    SocialFragment socialFragment ;
    MailFragment mailFragment ;
    MyselfFragment myselfFragment ;
    TeaFragment teaFragment;
    public static float scale = 0 ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);

        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {

        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        mainFragment=new MainFragment2();
        equmentFragment=new EqumentFragment();
        socialFragment=new SocialFragment();
        mailFragment = new MailFragment();
        myselfFragment = new MyselfFragment();

        initView();
    }




    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    private void initView() {
        mainMemu.add("茶饮");
        mainMemu.add("设备");
        mainMemu.add("社区");
        mainMemu.add("商城");
        mainMemu.add("我的");
        fragmentList.add(mainFragment);
        fragmentList.add(equmentFragment);
        fragmentList.add(socialFragment);
        fragmentList.add(mailFragment);
        fragmentList.add(myselfFragment);
        ClickViewPageAdapter tabAdapter = new ClickViewPageAdapter(getSupportFragmentManager(), fragmentList, this);
        main_viewPage.setAdapter(tabAdapter);
        main_tabLayout.setupWithViewPager(main_viewPage);
        for (int i = 0; i < mainMemu.size(); i++) {
            TabLayout.Tab tab = main_tabLayout.getTabAt(i);
            //注意！！！这里就是添加我们自定义的布局
            tab.setCustomView(tabAdapter.getCustomView(i));

            //这里是初始化时，默认item0被选中，setSelected（true）是为了给图片和文字设置选中效果，代码在文章最后贴出
//                ((ImageView) tab.getCustomView().findViewById(R.id.tab_iv)).setSelected(true);
//                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(Color.parseColor("#33c62b"));
        }

        main_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 4:

                        break;
                        default:

                            break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //没有选择时候调用
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void hiden(boolean b) {

        if (b){
            main_tabLayout.setVisibility(View.GONE);
        }else {
            main_tabLayout.setVisibility(View.VISIBLE);
        }

    }
    /**
     * 收到消息
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        if (msg.getTargetType() == ConversationType.single) {
        if (FriendFragment.isRunning){
            EventBus.getDefault().post(event);
        }
        }

    }
    /**
     * 接收离线消息
     *
     * @param event 离线消息事件
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android")) {
            if (FriendFragment.isRunning){
                EventBus.getDefault().post(event);
            }
        }
    }


    String userNickname;
    //接收到好友事件
    public void onEvent(final ContactNotifyEvent event) {
        socialFragment.RefrashView();
          if (event.getType() == ContactNotifyEvent.Type.contact_deleted) {
              try {
                  Thread.sleep(2000);
                  socialFragment.Refrashfriend();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1000) {
            socialFragment.Refrashfriend();
        }
    }
}
