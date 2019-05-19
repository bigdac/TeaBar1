package teabar.ph.com.teabar.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.peihou.daemonservice.DaemonHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import okhttp3.OkHttpClient;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.service.NotificationClickEventReceiver;
import teabar.ph.com.teabar.util.language.LocalManageUtil;


/**
 * Created by hongming.wang on 2018/1/23.
 */

public class MyApplication extends Application {
    public static final String APP_NAME = "XXX";
    public static boolean isDebug=true;
    private int count = 0;
    private List<Activity> activities;
    private List<Fragment> fragments;
    private static Context mContext;
    public static Map<Long, Boolean> isAtMe = new HashMap<>();
    public static Map<Long, Boolean> isAtall = new HashMap<>();
    public static List<String> forAddFriend = new ArrayList<>();
    public static List<Message> forwardMsg = new ArrayList<>();
    public static List<UserInfo> alreadyRead = new ArrayList<>();
    public static List<UserInfo> unRead = new ArrayList<>();
    public static List<Message> ids = new ArrayList<>();
    public static Context getContext(){
        return mContext;

    }
    private static MyApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        DaemonHolder.init(this, MQService.class);

        fragments=new ArrayList<>();
        app = this;
        mContext=getApplicationContext();
        activities=new ArrayList<>();
        fragments=new ArrayList<>();
        FacebookSdk.sdkInitialize(getApplicationContext());
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext());
        LocalManageUtil.setApplicationLanguage(this);
        Log.e("ZZZZZZZZZZZZZZZ", "onCreate: -->"+ LocalManageUtil.getSetLanguageLocale(this) );

//        SMSSDK.initSDK(this,"257a640199764","125aced6309709d59520e466e078ba15");
        //设置Notification的模式
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                count ++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if(count > 0) {
                    count--;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });

    }
    private static Map<String,Activity> destoryMap = new HashMap<>();
    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName,activity);
    }
    /**
     *销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }
    public static MyApplication getApp(){
        return app;
    }



    public void addActivity(Activity activity){
        if (!activities.contains(activity)){
            activities.add(activity);
        }
    }
    public void addFragment(Fragment fragment){
        if (!fragments.contains(fragment)){
            fragments.add(fragment);
        }
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
    public void removeFragment(Fragment fragment){
        if (fragments.contains(fragment)){
            fragments.remove(fragment);
        }
    }
    public void removeAllFragment(){
        fragments.clear();
    }

    public void removeActivity(Activity activity){
        if (activities.contains(activity)){
            activities.remove(activity);
            activity.finish();
        }
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void removeAllActivity(){
        for (Activity activity:activities){
            activity.finish();
        }
    }
    /**
     * 判断app是否在后台
     * @return
     */
    public boolean isBackground(){
        if(count <= 0){
            return true;
        } else {
            return false;
        }
    }
    /*0中文1 英文*/
    public int IsEnglish(){
        if ("en_US".equals(LocalManageUtil.getSetLanguageLocale(this))){
            return 1;
        }else {
            return 0;
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LocalManageUtil.setLocal(base));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(getApplicationContext());

    }


}
