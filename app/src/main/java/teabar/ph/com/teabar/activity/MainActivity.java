package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
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
import teabar.ph.com.teabar.fragment.MainFragment2;
import teabar.ph.com.teabar.fragment.MyselfFragment;
import teabar.ph.com.teabar.fragment.SocialFragment;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

public class MainActivity extends BaseActivity implements FriendCircleFragment1.hidenShowView ,EqumentFragment.EquipmentCtrl,MainFragment2.FirstEquipmentCtrl {
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
    private boolean MQBound;
    public static float scale = 0 ;
    MessageReceiver receiver;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    public static boolean isRunning = false;
    Equpment FirstEqument;
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
        isRunning =true;
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
        equipmentDao = new EquipmentImpl( getApplicationContext());
        equpments= equipmentDao.findAll();
        equpments= equipmentDao.findAll();
        for (int i = 0;i<equpments.size();i++){
            if (equpments.get(i).getIsFirst()){
                FirstEqument = equpments.get(i) ;
            }
        }
        initView();
        //绑定services
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("MainActivity");
        receiver = new  MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }
    Equpment msg1;
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
             msg1 = (Equpment) intent.getSerializableExtra("msg1");
             FirstEqument = msg1;
             if (MainFragment2.isRunning){
                 mainFragment.RefrashFirstEqu1();
             }
            if (EqumentFragment.isRunning){
                equmentFragment.RefrashFirstEqu(msg1);
            }
        }
    }

    public Equpment getFirstEqu(){
        if (msg1!=null){
            return  msg1;
        }
         return null;
    }
    Intent MQintent;
    MQService MQservice;
    boolean boundservice;
    ServiceConnection MQconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            MQservice = binder.getService();
            boundservice = true;
            new FirstAsynctask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @SuppressLint("StaticFieldLeak")
    class  FirstAsynctask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            for (Equpment equpment:equpments){
                try {
                    Thread.sleep(500);
                    if (!ToastUtil.isEmpty(equpment.getMacAdress()))
                    MQservice.sendFindEqu(equpment.getMacAdress());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
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
        if (MQBound) {
            unbindService(MQconnection);
        }
        if (receiver!=null)
            unregisterReceiver(receiver);
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1000) {
            socialFragment.Refrashfriend();
        }
//        if (resultCode==2000){
//                equmentFragment.RefrashChooseEqu();
//        }

    }

    public Equpment getFirstEqument(){
        return FirstEqument;
    }

    public void setFirstEqument(Equpment firstEqument){
        this.FirstEqument = firstEqument;
    }

    @Override
    public void open(int type,String mac) {
            MQservice.sendOpenEqu(type,mac);
            equmentFragment.Synchronization(type);//设备同步
        Log.e(TAG, "open: --------------->"+type+">>>>>>"+mac );
    }
    @Override
    public void open1(int type,String mac) {
        MQservice.sendOpenEqu(type,mac);
        mainFragment.Synchronization(type);//设备同步
        Log.e(TAG, "open: --------------->"+type+">>>>>>"+mac );
    }


    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                     application.removeAllActivity();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
