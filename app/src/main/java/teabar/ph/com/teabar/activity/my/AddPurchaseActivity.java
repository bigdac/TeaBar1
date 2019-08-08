package teabar.ph.com.teabar.activity.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;

//添加購買計畫
public class AddPurchaseActivity extends BaseActivity {


    @BindView(R.id.rl_fits)
    RecyclerView rl_fits;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_add_purchase;
    }

    @Override
    public void initView(View view) {

    }

    @OnClick({R.id.iv_plan_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_plan_fh:
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
}
