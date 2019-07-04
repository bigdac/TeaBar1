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
import teabar.ph.com.teabar.adpter.AnswerPlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.view.ScoreProgressBar;

public class AnswerPlanActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.rv_plan)
    RecyclerView rv_plan;
    AnswerPlanAdapter answerPlanAdapter;

    List<String> list=new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_goodplan;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }

        application.addActivity(this);
        for (int i = 0;i<4;i++){
            list.add(i+"");
        }
        answerPlanAdapter = new AnswerPlanAdapter(this,list);
        rv_plan.setLayoutManager(new LinearLayoutManager(this));
        rv_plan.setAdapter(answerPlanAdapter);
        answerPlanAdapter.SetOnItemClick(new AnswerPlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(BuyPlanActivity.class);
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });

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


    @OnClick({R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;
        }

    }


}
