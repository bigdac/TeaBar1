package teabar.ph.com.teabar.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {
    SharedPreferences preferences;//共享参数用来保存第一次启动的键值对
    private static final int DELAY = 2000;// 延时
    private static final int GO_HOME = 1000;//进入主页面标志
    private static final int GO_GUIDE = 1001;//进入引导页标志
    private boolean mIsFirst = false;//启动页是否第一次启动
    private static final String START_KEY = "isFirst";//第一次启动的键
    MyApplication application;
    static int count=0;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_splash;
    }

    /**
     * autosize 适配页面布局 。按屏幕高度来机配
     * @return
     */
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        return super.getResources();

    }
    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        count++;
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        if (preferences.contains("count")){
            count=preferences.getInt("count",0);
        }
        Log.e("SplashCount","-->"+count);
        application.addActivity(this);
        if (count<=3){
            setLang(Locale.ENGLISH);
        }
        init();

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
        MyApplication.initLanguage=1;
        startActivity(new Intent().setClass(SplashActivity.this,
                SplashActivity.class));
        SplashActivity.this.finish();
    }
    /**
     * 初始化mIsFirst值
     */
    private void init() {
        // 读取保存的键的值
        mIsFirst = preferences.getBoolean(START_KEY, true);
        Log.i("mIsFirst","mIsFirst="+mIsFirst);
        if (!mIsFirst) {
            hander.sendEmptyMessageDelayed(GO_HOME, DELAY);
        } else {
            if (count==4){
                hander.sendEmptyMessageDelayed(GO_GUIDE, DELAY);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(START_KEY, false);
                editor.putInt("count",4);
                editor.commit();
            }
        }
    }

    // 去主界面
    private void gohome() {
        startActivity(new Intent(SplashActivity.this, EncourageActivity.class));
        finish();
    }

    // 去引导界面
    private void goguide() {
        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
        finish();
    }
    @SuppressLint("HandlerLeak")
    //接收消息
    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    gohome();
                    break;
                case GO_GUIDE:
                    goguide();
                    break;
            }
        }
    };



    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
