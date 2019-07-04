package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.ClickViewPageAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.EqumentFragment2;
import teabar.ph.com.teabar.fragment.FriendCircleFragment2;
import teabar.ph.com.teabar.fragment.FriendFragment;
import teabar.ph.com.teabar.fragment.MailFragment1;
import teabar.ph.com.teabar.fragment.MainFragment3;
import teabar.ph.com.teabar.fragment.MyselfFragment;
import teabar.ph.com.teabar.fragment.MyselfFragment1;
import teabar.ph.com.teabar.fragment.SocialFragment;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.UserEntry;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.SharePreferenceManager;
import teabar.ph.com.teabar.view.ChangeDialog;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

public class MainActivity extends BaseActivity implements FriendCircleFragment2.hidenShowView ,EqumentFragment2.EquipmentCtrl,MainFragment3.FirstEquipmentCtrl {
    @BindView(R.id.main_viewPage)
    NoSrcollViewPage main_viewPage;
    @BindView(R.id.main_tabLayout)
    TabLayout main_tabLayout;
    List<String> mainMemu = new ArrayList<>();
    List<BaseFragment> fragmentList = new ArrayList<>();
    MyApplication application;
    MainFragment3 mainFragment ;
    EqumentFragment2 equmentFragment ;
    SocialFragment socialFragment ;
    MailFragment1 mailFragment ;
    MyselfFragment1 myselfFragment ;
    MyselfFragment myselfFragment1 ;
    private boolean MQBound;
    public static float scale = 0 ;
    MessageReceiver receiver;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    public static boolean isRunning = false;
    Equpment FirstEqument;
    UserEntryImpl userEntryDao;
    SharedPreferences preferences;
    String userId;
    int type1;
    @Override
    public void initParms(Bundle parms) {

    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 360, true);//如果有自定义需求就用这个方法
        return super.getResources();

    }
    @Override
    public int bindLayout() {
//        setSteepStatusBar(true);

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
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","") ;
        type1 = preferences.getInt("type1",0);
        mainFragment=new MainFragment3();
        equmentFragment=new EqumentFragment2();
        socialFragment=new SocialFragment();
        mailFragment = new MailFragment1();
        myselfFragment = new MyselfFragment1();
        myselfFragment1 = new MyselfFragment();
        equipmentDao = new EquipmentImpl( getApplicationContext());
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
        userEntryDao = new UserEntryImpl(getApplicationContext());
        LoginJM();
        requestOverlayPermission();
    }

    public void LoginJM(){
        JMessageClient.login(userId+"", "123456", new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {

                if (responseCode == 0) {
                    SharePreferenceManager.setCachedPsw("123456");
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    File avatarFile = myInfo.getAvatarFile();
                    //登陆成功,如果用户有头像就把头像存起来,没有就设置null
                    if (avatarFile != null) {
                        SharePreferenceManager.setCachedAvatarPath(avatarFile.getAbsolutePath());
                    } else {
                        SharePreferenceManager.setCachedAvatarPath(null);
                    }
                    String username = myInfo.getUserName();
                    String appKey = myInfo.getAppKey();

                    UserEntry user = userEntryDao.findById(1);
                    if (null == user) {
                        user = new UserEntry(1,userId,username, appKey);
                        userEntryDao.insert(user);
                    }

                }
            }
        });
    }

    private static final int REQUEST_OVERLAY = 4444;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                changeDialog();
            }
        }
    }


    ChangeDialog dialog ;
    private void changeDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTitle(getText(R.string.alarm_qxsq).toString());
        dialog.setTips(getText(R.string.alarm_dkxfc).toString());

        backgroundAlpha(0.4f);
        dialog.setOnNegativeClickListener(new ChangeDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new ChangeDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_OVERLAY);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        dialog.show();
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (MQService.reset==1){
            FirstEqument = null;
            MQService.reset=0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirstEqument!=null&&MQservice!=null){
            if (FirstEqument.getMStage()!=0xb6&&FirstEqument.getMStage()!=0xb7){
                MQservice.sendFindEqu(FirstEqument.getMacAdress());
            }
        }
    }

    Equpment msg1;
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            msg1 = (Equpment) intent.getSerializableExtra("msg1");
            FirstEqument = msg1;
            int reset =  intent.getIntExtra("reset",0);
            if (reset==1){
                FirstEqument = null;
            }
             if (MainFragment3.isRunning){
                 mainFragment.RefrashFirstEqu1();
             }
            if (EqumentFragment2.isRunning){
                equmentFragment.RefrashFirstEqu1();
            }

        }
    }

    public Equpment getFirstEqu(){
        if (FirstEqument!=null){
            return  FirstEqument;
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
                 MQservice.loadAlltopic();
            for (Equpment equpment:equpments){
                try {
                    Thread.sleep(500);
                    if (!TextUtils.isEmpty(equpment.getMacAdress()))
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

        if (type1==0) {
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
        }else {
            mainMemu.add("茶饮");
            mainMemu.add("设备");
            mainMemu.add("商城");
            mainMemu.add("我的");
            fragmentList.add(mainFragment);
            fragmentList.add(equmentFragment);
            fragmentList.add(mailFragment);
            fragmentList.add(myselfFragment1);
        }
        ClickViewPageAdapter tabAdapter = new ClickViewPageAdapter(getSupportFragmentManager(), fragmentList, this,type1);
        main_viewPage.setAdapter(tabAdapter);
        main_tabLayout.setupWithViewPager(main_viewPage);
        for (int i = 0; i < mainMemu.size(); i++) {
            TabLayout.Tab tab = main_tabLayout.getTabAt(i);
            //注意！！！这里就是添加我们自定义的布局
            tab.setCustomView(tabAdapter.getCustomView(i));

            //这里是初始化时，默认item0被选中，setSelected（true）是为了给图片和文字设置选中效果，代码在文章最后贴出
            if (i == 0) {
                ((ImageView) tab.getCustomView().findViewById(R.id.tab_iv)).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
            }
        }

        main_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ImageView) tab.getCustomView().findViewById(R.id.tab_iv)).setSelected(true);
                if (type1==0){
                switch (tab.getPosition()) {
                    case 0:
                        ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                        }
                        break;
                    case 1:
                        ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                        }
                        break;
                    case 2:
                        ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                        }
                        break;
                    case 3:
                        ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                             getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                           }
                        break;
                    case 4:
                        ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_tit1));

//                            getWindow().addFlags(
//                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }

                        break;
                }
                }else {
                    switch (tab.getPosition()) {
                        case 0:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 1:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;

                        case 2:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 3:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor( getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_tit1));

//                            getWindow().addFlags(
//                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            }

                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //没有选择时候调用
                ((ImageView) tab.getCustomView().findViewById(R.id.tab_iv)).setSelected(false);
                if (type1==0) {
                    switch (tab.getPosition()) {
                        case 0:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                            break;
                        case 1:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                            break;
                        case 2:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                            break;
                        case 3:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                            break;
                        case 4:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                            break;
                    }
                }else {

                        switch (tab.getPosition()) {
                            case 0:
                                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                                break;
                            case 1:
                                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                                break;
                            case 2:
                                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                                break;
                            case 3:
                                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.social_gray));
                                break;
                         }

                }
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

    public int getLanguage(){
        return application.IsEnglish();
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
        if (resultCode==2000){
            equipmentDao = new EquipmentImpl( getApplicationContext());
            equpments= equipmentDao.findAll();
            if (equpments.size()>0){
            for (int i = 0;i<equpments.size();i++){
                if (equpments.get(i).getIsFirst()){
                    FirstEqument = equpments.get(i) ;
                }
             }
            }else {
                FirstEqument=null;
            }
            mainFragment.RefrashFirstEqu1();
            equmentFragment.RefrashFirstEqu1();

        }

    }

    public Equpment getFirstEqument(){
        return FirstEqument;
    }

    public void setFirstEqument(Equpment firstEqument){
        this.FirstEqument= null;
        this.FirstEqument = firstEqument;
    }

    @Override
    public void open(int type,String mac) {
            MQservice.sendOpenEqu(type,mac);
            equmentFragment.Synchronization(type);//设备同步    0Xc1:正在预热 0Xc0：休眠（关闭预热发一条
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
                    Toast.makeText( this,getText(R.string.toast_main_exit),Toast.LENGTH_SHORT).show();
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
