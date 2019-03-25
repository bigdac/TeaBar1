package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zlpolygonview.ZLPolygonView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.view.ValuePopupWindow;

public class PowerpicActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.tv_power_day)
    TextView tv_power_day;
    @BindView(R.id.tv_power_week)
    TextView tv_power_week;
    @BindView(R.id.tv_power_month)
    TextView tv_power_month;
    ZLPolygonView polygonView;
    MyApplication application;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_powerpic;
    }

    @Override
    public void initView(View view) {
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                ScreenUtils.getStatusBarHeight());
//        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        polygonView = findViewById(R.id.polygonview);
        List<Float> values = new ArrayList<>();
        for (int i=0; i<8; i++) {
            values.add((float) (Math.random()*50/100+0.5));
        }
        polygonView.setPolygonValues(values);
        String json = "[{\"text\":\"1\",\"value\":\"0.4\"},{\"text\":\"2\",\"value\":\"0.6\"}," +
                "{\"text\":\"3\",\"value\":\"0.2\"},{\"text\":\"4\",\"value\":\"0.8\"},{\"text\":\"5\",\"value\":\"0.8\"}" +
                ",{\"text\":\"6\",\"value\":\"0.8\"},{\"text\":\"7\",\"value\":\"0.8\"},{\"text\":\"8\",\"value\":\"0.8\"}]";
        polygonView.setJsonString(json);
        polygonView.setOnClickPolygonListeren(new ZLPolygonView.onClickPolygonListeren() {
            @Override
            public void onClickPolygon(MotionEvent event, int index) {
          /*      View rootView = LayoutInflater.from(PowerpicActivity.this).inflate(R.layout.activity_powerpic, null);
                new ValuePopupWindow(PowerpicActivity.this).showAtLocation(rootView,
                        Gravity.TOP|Gravity.LEFT,
                        (int)event.getRawX(), (int)event.getRawY());*/
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({R.id.tv_power_day ,R.id.tv_power_week,R.id.tv_power_month})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_power_day:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.pink2));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                break;
            case R.id.tv_power_week:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.pink1));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                break;
            case R.id.tv_power_month:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.pink2));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right));
                break;

        }
    }
}
