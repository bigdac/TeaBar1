package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;

public class EncourageActivity extends BaseActivity {
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_encourage;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.encourage_bt_in})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.encourage_bt_in:
                startActivity(LoginActivity.class);
                break;
        }
    }
}
