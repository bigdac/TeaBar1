package teabar.ph.com.teabar.activity.question;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.internal.CustomAdapt;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class ChooseWhichActivity extends BaseActivity implements CustomAdapt {
    MyApplication application;
    @BindView(R.id.bt_question_finish)
    Button bt_question_finish;
    @BindView(R.id.rl_choose_base)
    RelativeLayout rl_choose_base;
    @BindView(R.id.rl_choose_wj)
    RelativeLayout rl_choose_wj;
    int which= -1;
    @Override
    public void initParms(Bundle parms) {

    }
    int langauge;
    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_choosewhich;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        bt_question_finish.setBackgroundResource(R.mipmap.question_nonext);
        bt_question_finish.setClickable(false);

    }
    @OnClick({R.id.rl_choose_base,R.id.rl_choose_wj,R.id.bt_question_finish,R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_choose_base:
                which =1 ;
                rl_choose_wj.setBackgroundResource(R.mipmap.question1_nochoose);
                rl_choose_base.setBackgroundResource(R.mipmap. question1_choose1);
                bt_question_finish.setBackgroundResource(R.mipmap.question_button);
                bt_question_finish.setClickable(true);
                break;

            case R.id.rl_choose_wj:
                which=2;
                rl_choose_wj.setBackgroundResource(R.mipmap.question1_choose1);
                rl_choose_base.setBackgroundResource(R.mipmap.question1_nochoose);
                bt_question_finish.setBackgroundResource(R.mipmap.question_button);
                bt_question_finish.setClickable(true);
                break;
            case R.id.bt_question_finish:
                if (which==1){
                    startActivity(BaseQuestionActivity.class);
                }else if (which==2){
                    startActivity(QusetionActivity.class);
                }
                break;
            case R.id.iv_power_fh:
                finish();
                break;

        }
    }



    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 667;
    }
}
