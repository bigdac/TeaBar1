package teabar.ph.com.teabar.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.EncourageActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class SplashActivity extends BaseActivity {
    SharedPreferences preferences;
    private static final int DELAY = 2000;// 延时
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private boolean mIsFirst = false;
    private static final String START_KEY = "isFirst";
    MyApplication application;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_splash;
    }
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
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        init();
    }

    private void init() {

        // 读取保存的键的值
        mIsFirst = preferences.getBoolean(START_KEY, true);
        if (!mIsFirst) {
            hander.sendEmptyMessageDelayed(GO_HOME, DELAY);
        } else {
            hander.sendEmptyMessageDelayed(GO_GUIDE, DELAY);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(START_KEY, false);
            editor.commit();
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
