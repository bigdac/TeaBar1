package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Numbers;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.view.BarChartManager;


public class DrinkNumActivity extends BaseActivity {


    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.tv_power_day)
    TextView tv_power_day;
    @BindView(R.id.tv_power_week)
    TextView tv_power_week;
    @BindView(R.id.tv_power_month)
    TextView tv_power_month;
    @BindView(R.id.tv_drink_num)
            TextView tv_drink_num;
    MyApplication application;

    private BarChart mBarChart,mBarChart1;
    private BarData mBarData;
    BarChartManager barChartManager,barChartManager1;
    ArrayList<String> xValuesDay = new ArrayList<>();/**X轴day值*/
    List<Integer> yValueDay = new ArrayList<>();/**Y轴day值*/
    ArrayList<String> xValuesWeek = new ArrayList<>();/**X轴week值*/
    List<Integer> yValueWeek = new ArrayList<>();/**Y轴week值*/
    ArrayList<String> xValuesMonth = new ArrayList<>();/**X轴month值*/
    List<Integer> yValueMonth = new ArrayList<>();/**Y轴month值*/
    String userId ;
    SharedPreferences preferences;
    int setNum = 0;
    List <Numbers> listDay = new ArrayList<>();
    List <Numbers> listWeek = new ArrayList<>();
    List <Numbers> listMonth = new ArrayList<>();
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


        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId =preferences.getString("userId","");
        setNum = preferences.getInt("drinkNum",4);
        initView();

        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                Highlight highlight1 = highlight;
               float fisrthx = highlight.getY();
                tv_drink_num.setText( (int) fisrthx+"");
                Log.v("tfhr", "点击某一条" + highlight.getYPx()+"....");
            }

            @Override
            public void onNothingSelected() {

            }
        });
        mBarChart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                Highlight highlight1 = highlight;
                float fisrthx = highlight.getY();
                tv_drink_num.setText( (int) fisrthx+"");
                Log.v("tfhr", "点击某一条" + highlight.getYPx()+"....");
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    private void initView() {

        mBarChart = findViewById(R.id.bar_chart);
        mBarChart1 = findViewById(R.id.bar_chart1);
        for (int i = 1; i <= 24; i++) {
            xValuesDay.add(i+"");
            yValueDay.add((int) (Math.random() * 8));
        }
        for (int i = 1; i <=7; i++) {
            xValuesWeek.add(i+"");
            yValueWeek.add((int) (Math.random() * 8 ));
        }
        for (int i = 1; i <= 30; i++) {
            xValuesMonth.add(i+"");
            yValueMonth.add((int) (Math.random() *8 ));
        }
        barChartManager = new BarChartManager(mBarChart,this);
        barChartManager1 = new BarChartManager(mBarChart1,this);
        setValueDay();
        barChartManager.setYAxis(10,0,10);
        barChartManager1.setYAxis(10,0,10);
        barChartManager.setHightLimitLine(setNum,"",Color.parseColor("#777777"));
        barChartManager1.setHightLimitLine(setNum,"",Color.parseColor("#777777"));


    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    class getNumAsyncTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"http://47.98.131.11:8081/app/getUserDrink?userId="+userId);

            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("state");
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArrayday = jsonObject1.getJSONArray("dayCount");
                JSONArray jsonArrayweek = jsonObject1.getJSONArray("weekCount");
                JSONArray jsonArraymonth = jsonObject1.getJSONArray("monthCount");


            } catch ( Exception e) {
                e.printStackTrace();
            }

            return code;
        }
    }


    @OnClick({R.id.tv_power_day ,R.id.tv_power_week,R.id.tv_power_month,R.id.iv_power_fh,R.id.tv_drink_set})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_power_day:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.white));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                mBarChart.setScaleMinima(1.0f, 1.0f);
                tv_power_day.setTextColor(getResources().getColor(R.color.white));
                tv_power_week.setTextColor(getResources().getColor(R.color.social_gray));
                tv_power_month.setTextColor(getResources().getColor(R.color.social_gray));
                mBarChart.getViewPortHandler().refresh(new Matrix(), mBarChart, true);
                mBarChart1.setVisibility(View.GONE);
                mBarChart.setVisibility(View.VISIBLE);
                setValueDay();

                break;
            case R.id.tv_power_week:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.nomal_green));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right1));
                tv_power_day.setTextColor(getResources().getColor(R.color.social_gray));
                tv_power_week.setTextColor(getResources().getColor(R.color.white));
                tv_power_month.setTextColor(getResources().getColor(R.color.social_gray));
                mBarChart1.setScaleMinima(1.0f, 1.0f);
                mBarChart1.getViewPortHandler().refresh(new Matrix(), mBarChart, true);

//                mBarChart.fitScreen();
                mBarChart1.setVisibility(View.VISIBLE);
                mBarChart.setVisibility(View.GONE);
                setValueWeek();

                break;
            case R.id.tv_power_month:
                tv_power_day.setBackground(getResources().getDrawable(R.drawable.power_button_left1));
                tv_power_week.setBackgroundColor(getResources().getColor(R.color.white));
                tv_power_month.setBackground(getResources().getDrawable(R.drawable.power_button_right));
                tv_power_day.setTextColor(getResources().getColor(R.color.social_gray));
                tv_power_week.setTextColor(getResources().getColor(R.color.social_gray));
                tv_power_month.setTextColor(getResources().getColor(R.color.white));
                mBarChart.setScaleMinima(1.0f, 1.0f);
                mBarChart.getViewPortHandler().refresh(new Matrix(), mBarChart, true);
                mBarChart1.setVisibility(View.GONE);
                mBarChart.setVisibility(View.VISIBLE);
                setValueMonth();
                break;
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.tv_drink_set:
                Intent intent = new Intent(this,SetDrinkActivity.class);
                startActivityForResult(intent,100);
                break;

        }
    }
    /**
     * 设置day用水量
     */
    private void setValueDay(){
        if (!yValueDay.isEmpty()){
            barChartManager.showBarChart(xValuesDay, yValueDay, "", Color.parseColor("#dedede"));
            barChartManager.setDescription("");
        }

    }
    /**
     * 设置day用水量
     */
    private void setValueWeek(){
        if (!xValuesWeek.isEmpty()){
            barChartManager1.showBarChart(xValuesWeek, yValueWeek, "", Color.parseColor("#dedede"));
            barChartManager1.setDescription("");
        }

    }
    /**
     * 设置day用水量
     */
    private void setValueMonth(){
        if (!xValuesWeek.isEmpty()){
            barChartManager.showBarChart(xValuesMonth, yValueMonth, "", Color.parseColor("#dedede"));
            barChartManager.setDescription("");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==100){
            String num = data.getStringExtra("num");
            if (!TextUtils.isEmpty(num)){
                tv_drink_num.setText(num);
                barChartManager.setHightLimitLine(Integer.valueOf(num),"",Color.parseColor("#777777"));
                barChartManager1.setHightLimitLine(Integer.valueOf(num),"",Color.parseColor("#777777"));
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("drinkNum",Integer.valueOf(num));
                editor.commit();
            }
        }
    }




}
