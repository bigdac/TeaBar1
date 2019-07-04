package teabar.ph.com.teabar.activity.my;

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
import teabar.ph.com.teabar.activity.PlanInformActivity;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class MyPlanActivity extends BaseActivity {
    @BindView(R.id.rv_myplan)
    RecyclerView rv_myplan;

    PlanAdapter mPlanAdapter;
    List<String> mList = new ArrayList<>();
    MyApplication application;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_myplan;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);

        for (int i = 0;i<2;i++){
            mList.add(i+"");
        }

        mPlanAdapter = new PlanAdapter(this,mList);
        rv_myplan.setLayoutManager(new LinearLayoutManager(this));
        rv_myplan.setAdapter(mPlanAdapter);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_plan_fh,R.id.tv_plan_inform})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_plan_fh:
                finish();
                break;

            case R.id.tv_plan_inform:
                startActivity(PlanInformActivity.class);
                break;
        }
    }
}
