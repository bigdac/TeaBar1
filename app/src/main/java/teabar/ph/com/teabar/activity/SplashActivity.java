package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    SharedPreferences preferences;
    private static final int DELAY = 2000;// 延时
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private boolean mIsFirst = false;
    private static final String START_KEY = "isFirst";

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_splash;
    }

    @Override
    public void initView(View view) {
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
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
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
