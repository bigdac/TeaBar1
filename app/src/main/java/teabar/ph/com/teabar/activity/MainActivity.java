package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
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
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.SharePreferenceManager;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.ChangeDialog;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

/**
 * 主页面
 * 用来加载首页，设备页，社区，商城，我的这5个页面
 */
public class MainActivity extends BaseActivity implements FriendCircleFragment2.hidenShowView, EqumentFragment2.EquipmentCtrl, MainFragment3.FirstEquipmentCtrl {
    @BindView(R.id.main_viewPage)
    NoSrcollViewPage main_viewPage;//不可滑动的ViewPage
    @BindView(R.id.main_tabLayout)
    TabLayout main_tabLayout;
    List<String> mainMemu = new ArrayList<>();
    List<BaseFragment> fragmentList = new ArrayList<>();//用来管理fragment的列表集合
    MyApplication application;
    MainFragment3 mainFragment;//首页
    EqumentFragment2 equmentFragment;//设备页
    SocialFragment socialFragment;//社区页
    MailFragment1 mailFragment;//商城页
    MyselfFragment1 myselfFragment;//我的（个人设置页面 type1为0时的个人页面
    MyselfFragment myselfFragment1;//我的(个人设置页面 type=1时的个人页面)
    private boolean MQBound;
    public static float scale = 0;
    MessageReceiver receiver;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    public static boolean isRunning = false;
    Equpment FirstEqument;
    UserEntryImpl userEntryDao;
    SharedPreferences preferences;
    String userId;
    int type1;//用户类型，根据类型来加载主页面的选项卡页面

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

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void initView(View view) {
        isRunning = true;
        scale = getApplicationContext().getResources().getDisplayMetrics().density;
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        type1 = preferences.getInt("type1", 0);
        mainFragment = new MainFragment3();
        equmentFragment = new EqumentFragment2();
        socialFragment = new SocialFragment();
        mailFragment = new MailFragment1();
        myselfFragment = new MyselfFragment1();
        myselfFragment1 = new MyselfFragment();
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equpments = equipmentDao.findAll();
        for (int i = 0; i < equpments.size(); i++) {
            if (equpments.get(i).getIsFirst()) {
                FirstEqument = equpments.get(i);
            }
        }
        initView();
        //绑定services
        MQintent = new Intent(this, MQService.class);
        MQBound = bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("MainActivity");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
        userEntryDao = new UserEntryImpl(getApplicationContext());
        LoginJM();
        requestOverlayPermission();
        Map<String, Object> params = new HashMap<>();
        params.put("appType", 2);
        new upDateAppAsyncTask().execute(params);
    }

    /**
     * 极光登录操作
     */
    public void LoginJM() {
        JMessageClient.login(userId + "", "123456", new BasicCallback() {
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
                        user = new UserEntry(1, userId, username, appKey);
                        userEntryDao.insert(user);
                    }
                    findFriend();

                }
            }
        });


    }

    public void findFriend() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                if (0 == responseCode) {
                    //获取好友列表成功
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("friendNum", userInfoList.size() + "");
                    editor.commit();


                } else {
                    //获取好友列表失败

                }
            }
        });
    }

    private static final int REQUEST_OVERLAY = 4444;

    /**
     * 請求開啟懸浮穿權限
     */
    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                changeDialog();
            }
        }
    }


    ChangeDialog dialog;

    /**
     * 请求开启悬浮窗权限对话框
     */
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
        if (MQService.reset == 1) {
            FirstEqument = null;
            MQService.reset = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirstEqument != null && MQservice != null) {
            if (FirstEqument.getMStage() != 0xb6 && FirstEqument.getMStage() != 0xb7) {
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
            int reset = intent.getIntExtra("reset", 0);
            if (reset == 1) {
                FirstEqument = null;
            }
            if (MainFragment3.isRunning) {
                mainFragment.RefrashFirstEqu1();
            }
            if (EqumentFragment2.isRunning) {
                equmentFragment.RefrashFirstEqu1();
            }

        }
    }

    public Equpment getFirstEqu() {
        if (FirstEqument != null) {
            return FirstEqument;
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

    /**
     * 向页面中所有的设备发送查询命令，从而获得设备在线状态
     */
    @SuppressLint("StaticFieldLeak")
    class FirstAsynctask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            MQservice.getBasicData();
            return null;
        }
    }

    String appVersion = "";

    /**
     * 更新App
     * 从后台获取当前app的版本，如果获取的版本和用户使用的app版本一样就不更新，不一样就更新
     */
    class upDateAppAsyncTask extends AsyncTask<Map<String, Object>, Void, String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> params = maps[0];

            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress + "/app/getAPPVersion", params);
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("state");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    appVersion = jsonObject1.getString("appVersion");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                code = "4000";
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "4000":
                    toast(getText(R.string.toast_all_cs).toString());

                    break;
                case "200":
                    String name = Utils.getVerName(MainActivity.this);
                    if (name.equals(appVersion)) {

                    } else {
                        customDialog3();
                    }

                    break;
            }
        }
    }

    /*
     * 更新APP
     * */
    Dialog dialog1;

    private void customDialog3() {
        dialog1 = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(this.getText(R.string.set_upapp).toString());
        et_dia_name.setText(this.getText(R.string.set_upapp1).toString());
        dialog1.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog1.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog1.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.pgyer.com/9obk");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                dialog1.dismiss();

            }
        });
        dialog1.show();

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    private void initView() {

        if (type1 == 0) {
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
        } else {
            mainMemu.add("茶饮");
            mainMemu.add("设备");
            mainMemu.add("商城");
            mainMemu.add("我的");
            fragmentList.add(mainFragment);
            fragmentList.add(equmentFragment);
            fragmentList.add(mailFragment);
            fragmentList.add(myselfFragment1);
        }
        ClickViewPageAdapter tabAdapter = new ClickViewPageAdapter(getSupportFragmentManager(), fragmentList, this, type1);
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
                if (type1 == 0) {
                    switch (tab.getPosition()) {
                        case 0:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 1:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 2:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 3:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                             getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 4:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().setStatusBarColor(getResources().getColor(R.color.main_tit1));

//                            getWindow().addFlags(
//                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            }

                            break;
                    }
                } else {
                    switch (tab.getPosition()) {
                        case 0:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 1:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;

                        case 2:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                                getWindow().setStatusBarColor(getResources().getColor(R.color.main_title));
                            }
                            break;
                        case 3:
                            ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(getResources().getColor(R.color.nomal_green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                if (type1 == 0) {
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
                } else {

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

        if (b) {
            main_tabLayout.setVisibility(View.GONE);
        } else {
            main_tabLayout.setVisibility(View.VISIBLE);
        }

    }

    public int getLanguage() {
        return application.IsEnglish();
    }

    /**
     * 收到消息
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        if (msg.getTargetType() == ConversationType.single) {
            if (FriendFragment.isRunning) {
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
            if (FriendFragment.isRunning) {
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
        if (receiver != null)
            unregisterReceiver(receiver);
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            socialFragment.Refrashfriend();
        }
        if (resultCode == 2000) {
            equipmentDao = new EquipmentImpl(getApplicationContext());
            equpments = equipmentDao.findAll();
            if (equpments.size() > 0) {
                for (int i = 0; i < equpments.size(); i++) {
                    if (equpments.get(i).getIsFirst()) {
                        FirstEqument = equpments.get(i);
                    }
                }
            } else {
                FirstEqument = null;
            }
            mainFragment.RefrashFirstEqu1();
            equmentFragment.RefrashFirstEqu1();

        }

    }

    public Equpment getFirstEqument() {
        return FirstEqument;
    }

    public void setFirstEqument(Equpment firstEqument) {
        this.FirstEqument = null;
        this.FirstEqument = firstEqument;
    }

    @Override
    public void open(int type, String mac) {
        MQservice.sendOpenEqu(type, mac);
        equmentFragment.Synchronization(type);//设备同步    0Xc1:正在预热 0Xc0：休眠（关闭预热发一条
        Log.e(TAG, "open: --------------->" + type + ">>>>>>" + mac);
    }

    @Override
    public void open1(int type, String mac) {
        MQservice.sendOpenEqu(type, mac);
        mainFragment.Synchronization(type);//设备同步
        Log.e(TAG, "open: --------------->" + type + ">>>>>>" + mac);
    }


    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(this, getText(R.string.toast_main_exit), Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    application.removeAllActivity();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
