package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.PersonAskAdapter;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class PersonSetActivity extends BaseActivity {
    @BindView(R.id.rv_set_ask)
    RecyclerView rv_set_ask;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    MyApplication application;
    PersonAskAdapter personAskAdapter;
    List<String> mList = new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_personalset;
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
        for (int i =0;i<5;i++){
            mList.add(i+"");
        }
        personAskAdapter = new PersonAskAdapter(this,mList);
        rv_set_ask.setLayoutManager(new LinearLayoutManager(this));
        rv_set_ask.setAdapter(personAskAdapter);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_power_fh })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;


        }
    }
}
