package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jmessage.support.google.gson.Gson;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.MyquestionAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Question;
import teabar.ph.com.teabar.pojo.ScoreRecords;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class MyQuestionActivity extends BaseActivity {
    @BindView(R.id.rv_myquestion)
    RecyclerView rv_myquestion;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    MyquestionAdapter myquestionAdapter;
    List<ScoreRecords> mList = new ArrayList<>();
    MyApplication application;
    long userId;
    SharedPreferences preferences;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_myquestion;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getLong("userId",0);
        myquestionAdapter = new MyquestionAdapter(this,mList);
        rv_myquestion.setLayoutManager(new LinearLayoutManager(this));
        rv_myquestion.setAdapter(myquestionAdapter);
        new getTeaListAsynTask().execute();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_plan_fh })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_plan_fh:
                finish();
                break;


        }
    }

    String returnMsg1,returnMsg2;
    int  examTitle =1;

    /*  获取問題*/
    class getTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/exam/getUserExam?userId"+userId+"&currentPage=1&pageSize=1000" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            mList.clear();
                            JSONArray jsonArray  = jsonObject1.getJSONArray("items");
                            Gson gson = new Gson();
                                for (int i =0;i<jsonArray.length();i++){
                                    ScoreRecords scoreRecords = gson.fromJson(jsonArray.get(i).toString(),ScoreRecords.class);
                                    mList.add(scoreRecords);
                                }



                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    code="4000";
                }
            }
            return code;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "200":
                    myquestionAdapter.setmDatas(mList);
                    break;
                case "4000":

                    toast(  "连接超时，请重试");

                    break;
                default:

                    toast(  returnMsg1);
                    break;

            }
        }
    }

}
