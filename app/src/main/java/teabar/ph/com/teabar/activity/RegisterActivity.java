package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class RegisterActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_register;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        application.addActivity(this);
        }

        @Override
        public void doBusiness(Context mContext) {

        }

        @Override
        public void widgetClick(View v) {

        }
        @OnClick({R.id.tv_regist_login})
        public void onClick(View view){
            switch (view.getId()){
                case R.id.tv_regist_login:
                    finish();
                    break;
        }
    }
}
