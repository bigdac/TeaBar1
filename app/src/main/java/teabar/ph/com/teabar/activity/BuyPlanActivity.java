package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.MyplanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.util.GlideCircleTransform;

public class BuyPlanActivity extends BaseActivity {

    MyApplication application;

    @BindView(R.id.iv_buy_head)
    ImageView iv_buy_head;
    @BindView(R.id.tv_buy_des)
    TextView tv_buy_des;
    @BindView(R.id.tv_buy_about)
    TextView tv_buy_about;
    @BindView(R.id.tv_buy_td)
    TextView tv_buy_td;
    @BindView(R.id.tv_buy_js)
    TextView tv_buy_js;
    @BindView(R.id.tv_buy_name)
    TextView tv_buy_name;
    @BindView(R.id.iv_buy_pic)
    ImageView iv_buy_pic;
    @BindView(R.id.tv_buy_planname)
    TextView tv_buy_planname;
    @BindView(R.id.bt_plan_buy)
    Button bt_plan_buy;
    @BindView(R.id.tv_buy_week)
            TextView tv_buy_week;
    Plan plan;
    String title,week;
    @Override
    public void initParms(Bundle parms) {
        plan = (Plan) parms.getSerializable("plan");
        title = parms.getString("title");
        week = parms.getString("week");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_buyplan;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }


        application.addActivity(this);

        if (plan!=null){
            Glide.with(this).load(plan.getPlanPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.white).into( iv_buy_head);
            tv_buy_des.setText(plan.getDescribeEn());
            tv_buy_about.setText(plan.getAboutEn());
            tv_buy_td.setText(plan.getFeaturesEn());
            tv_buy_js.setText(plan.getDietitianDescribeEn());
            tv_buy_name.setText(plan.getDietitianEn());
            tv_buy_planname.setText(title);
            tv_buy_week.setText(week);
            Glide.with(this).load(plan.getDietitianPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(this)).into(iv_buy_pic);
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_buy_back,R.id.bt_plan_buy})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_buy_back:
                finish();
                break;

            case R.id.bt_plan_buy:
                startActivity(MailActivity.class);
                break;
        }
    }
}
