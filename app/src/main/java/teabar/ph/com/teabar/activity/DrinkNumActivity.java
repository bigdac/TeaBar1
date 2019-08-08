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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Numbers;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.view.BarChartManager;

//茶飲量詳情頁面 展示用戶 day,week,month的茶飲進度
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
    int month;
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
        setNum = preferences.getInt("drinkNum",0);
        tv_drink_num.setText(setNum+"");
        mBarChart = findViewById(R.id.bar_chart);
        mBarChart1 = findViewById(R.id.bar_chart1);
        Calendar calendar = Calendar.getInstance();
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        month = a.get(Calendar.DATE);
        initView();
//        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry entry, Highlight highlight) {
//                Highlight highlight1 = highlight;
//               float fisrthx = highlight.getY();
//                tv_drink_num.setText( (int) fisrthx+"");
//                Log.v("tfhr", "点击某一条" + highlight.getYPx()+"....");
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
//        mBarChart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry entry, Highlight highlight) {
//                Highlight highlight1 = highlight;
//                float fisrthx = highlight.getY();
//                tv_drink_num.setText( (int) fisrthx+"");
//                Log.v("tfhr", "点击某一条" + highlight.getYPx()+"....");
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
    }
    private void initView() {

        for (int i = 0; i <= 23; i++) {
            xValuesDay.add(i+"");
//            yValueDay.add((int) (Math.random() * 8));
            yValueDay.add(0);
        }
        for (int i = 1; i <=7; i++) {
            xValuesWeek.add(i+"");
//            yValueWeek.add((int) (Math.random() * 8 ));
            yValueWeek.add(0);
        }
        for (int i = 1; i <= month; i++) {
            xValuesMonth.add(i+"");
//            yValueMonth.add((int) (Math.random() *8 ));
            yValueMonth.add(0);
        }
        barChartManager = new BarChartManager(mBarChart,this);
        barChartManager1 = new BarChartManager(mBarChart1,this);
        setValueDay();
        new getNumAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getUserDrink?userId="+userId);

            Log.i("getNumAsyncTask","--->"+result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("state");
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArrayday = jsonObject1.getJSONArray("dayCount");
                JSONArray jsonArrayweek = jsonObject1.getJSONArray("weekCount");
                JSONArray jsonArraymonth = jsonObject1.getJSONArray("monthCount");
                for (int i=0;i<jsonArrayday.length();i++){
                    JSONObject jsonObject2  = jsonArrayday.getJSONObject(i);
                    String time = jsonObject2.getString("time");
                    String num = jsonObject2.getString("num");
                    if (!TextUtils.isEmpty(time)){
                        yValueDay.set(Integer.valueOf(time),Integer.valueOf(num));
                    }
                }
                for (int i=0;i<jsonArrayweek.length();i++){
                    JSONObject jsonObject2  = jsonArrayweek.getJSONObject(i);
                    String time = jsonObject2.getString("date");
                    String num = jsonObject2.getString("num");
                    if (!TextUtils.isEmpty(time)){
                        yValueWeek.set(Integer.valueOf(time),Integer.valueOf(num));
                    }
                }
                for (int i=0;i<jsonArraymonth.length();i++){
                    JSONObject jsonObject2  = jsonArraymonth.getJSONObject(i);
                    int time = jsonObject2.getInt("date");
                    int num = jsonObject2.getInt("num");
                    if (time>1){
                        yValueMonth.set(time-1,num);
                    }
                }
            } catch ( Exception e) {
                e.printStackTrace();
            }

            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "200":
                    setValueDay();

                    break;

                    default:

                        break;
            }
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
