package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import teabar.ph.com.teabar.adpter.AnswerAdapter;
import teabar.ph.com.teabar.adpter.EvaluateAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.view.FlowTagView;

public class QusetionActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    //标签类相关
    private AnswerAdapter adapter_ques;
    @BindView(R.id.fv_answer)
    FlowTagView fv_answer;

    List<String> list=new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_question;
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
        initView();
    }

    @Override
    public void doBusiness(Context mContext) {
        for(int i=0;i<5;i++){
            list.add("手冰冷");
        }
        adapter_ques.setItems(list);
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initView() {
        /*口味*/
        adapter_ques = new AnswerAdapter(this, R.layout.item_answer);
        fv_answer.setAdapter(adapter_ques);
        fv_answer.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                String e = adapter_ques.getItem(position).toString();
                adapter_ques.setSelection(position);
//                Intent intent = new Intent( this, ShopSearchResultActivity.class);
//                intent.putExtra("goodsName", e);
//                setHistory(e);
//                startActivity(intent);
            }
        });

    }
    @OnClick({R.id.iv_power_fh,R.id.bt_question_finish})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.bt_question_finish:
                startActivity(ScoreActivity.class);
                break;
        }

    }


}
