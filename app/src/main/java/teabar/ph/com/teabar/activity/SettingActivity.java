package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    MyApplication application;
    @BindView(R.id.iv_set_open)
    ImageView iv_set_open;
    boolean isOpen = true;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_setting;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_set_fh,R.id.rl_set_password,R.id.rl_set_mess,R.id.rl_set_user})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_set_fh:
                finish();
                break;

            case R.id.rl_set_password:
                startActivity(ChangePassActivity.class);
                break;

            case R.id.rl_set_mess:
                if (isOpen){
                    iv_set_open.setImageResource(R.mipmap.equ_close);
                    isOpen=false;
                }else {
                    iv_set_open.setImageResource(R.mipmap.equ_open);
                    isOpen=true;
                }
                break;
            case R.id.rl_set_user:
                startActivity(FeedbackActivity.class);
                break;

        }
    }
}
