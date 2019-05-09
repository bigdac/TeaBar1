package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.view.BarChartManager;


public class DrinkNumActivity extends BaseActivity {

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
    MyApplication application;

    private BarChart mBarChart;
    private BarData mBarData;
    BarChartManager barChartManager;
    ArrayList<String> xValuesDay = new ArrayList<>();/**X轴day值*/
    List<Integer> yValueDay = new ArrayList<>();/**Y轴day值*/
    ArrayList<String> xValuesWeek = new ArrayList<>();/**X轴week值*/
    List<Integer> yValueWeek = new ArrayList<>();/**Y轴week值*/
    ArrayList<String> xValuesMonth = new ArrayList<>();/**X轴month值*/
    List<Integer> yValueMonth = new ArrayList<>();/**Y轴month值*/
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_drinknum;
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        initView();



    }
    private void initView() {

        mBarChart = findViewById(R.id.bar_chart);
        for (int i = 1; i <= 24; i++) {
            xValuesDay.add(i+"");
            yValueDay.add((int) (Math.random() * 50));
        }
        for (int i = 1; i <=7; i++) {
            xValuesWeek.add(i+"");
            yValueWeek.add((int) (Math.random() * 50));
        }
        for (int i = 1; i <= 30; i++) {
            xValuesMonth.add(i+"");
            yValueMonth.add((int) (Math.random() * 50));
        }
        barChartManager = new BarChartManager(mBarChart);
        setValueDay();
        barChartManager.setYAxis(50,0,10);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({R.id.tv_power_day ,R.id.tv_power_week,R.id.tv_power_month,R.id.iv_power_fh,R.id.tv_drink_set})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_power_day:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.white));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                mBarChart.setScaleMinima(1.0f, 1.0f);
                mBarChart.getViewPortHandler().refresh(new Matrix(), mBarChart, true);
                setValueDay();

                break;
            case R.id.tv_power_week:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.nomal_green));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                mBarChart.setScaleMinima(1.0f, 1.0f);
                mBarChart.getViewPortHandler().refresh(new Matrix(), mBarChart, true);
                setValueWeek();
                break;
            case R.id.tv_power_month:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.white));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right));
                mBarChart.setScaleMinima(1.0f, 1.0f);
                mBarChart.getViewPortHandler().refresh(new Matrix(), mBarChart, true);
                setValueMonth();
                break;
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.tv_drink_set:
                startActivity(SetDrinkActivity.class);
                break;

        }
    }
    /**
     * 设置day用水量
     */
    private void setValueDay(){
        if (!yValueDay.isEmpty()){

            barChartManager.showBarChart(xValuesDay, yValueDay, "", Color.parseColor("#FE638C"));
            barChartManager.setDescription("小时");
        }

    }
    /**
     * 设置day用水量
     */
    private void setValueWeek(){
        if (!xValuesWeek.isEmpty()){

            barChartManager.showBarChart(xValuesWeek, yValueWeek, "", Color.parseColor("#FE638C"));
            barChartManager.setDescription("周");
        }

    }
    /**
     * 设置day用水量
     */
    private void setValueMonth(){
        if (!xValuesWeek.isEmpty()){

            barChartManager.showBarChart(xValuesMonth, yValueMonth, "", Color.parseColor("#FE638C"));
            barChartManager.setDescription("月");
        }

    }
}
