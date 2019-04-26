package teabar.ph.com.teabar.activity.device;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EqupmentInformAdapter;
import teabar.ph.com.teabar.adpter.EqupmentXqAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class EqupmentInformActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.rv_equmentxq)
    RecyclerView rv_equmentxq;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_equ_fh)
    ImageView iv_equ_fh;
    List<String> stringList;
    EqupmentInformAdapter equpmentInformAdapter;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_inform;
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
        stringList = new ArrayList<>();
        for (int i = 0;i<5;i++){
            stringList.add(i+"");
        }
        equpmentInformAdapter = new EqupmentInformAdapter(this,stringList);
        rv_equmentxq.setLayoutManager(new LinearLayoutManager(this));
        rv_equmentxq.setAdapter(equpmentInformAdapter);
        equpmentInformAdapter.SetOnItemClick(new EqupmentInformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

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
    @OnClick({R.id.iv_equ_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;
        }
    }
}
