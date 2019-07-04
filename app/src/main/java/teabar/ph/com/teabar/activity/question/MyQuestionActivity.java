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
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Question;
import teabar.ph.com.teabar.pojo.ScoreRecords;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class MyQuestionActivity extends BaseActivity {
    @BindView(R.id.rv_myquestion)
    RecyclerView rv_myquestion;

    MyquestionAdapter myquestionAdapter;
    List<ScoreRecords> mList = new ArrayList<>();
    MyApplication application;
    String userId;
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

        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","");
        myquestionAdapter = new MyquestionAdapter(this,mList);
        rv_myquestion.setLayoutManager(new LinearLayoutManager(this));
        rv_myquestion.setAdapter(myquestionAdapter);
        new getTeaListAsynTask(this).execute();
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
    class getTeaListAsynTask extends BaseWeakAsyncTask<Void,Void,String,BaseActivity> {

        public getTeaListAsynTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/web/getUserExam?userId="+userId  );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2=jsonObject.getString("message3");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            mList.clear();
                            JSONArray jsonArray  = jsonObject1.getJSONArray("examTea");
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
        protected void onPostExecute(BaseActivity baseActivity, String s) {


            switch (s) {

                case "200":
                    myquestionAdapter.setmDatas(mList);
                    break;
                case "4000":
                    toast( getText(R.string.toast_all_cs).toString());

                    break;
                default:
                    if (application.IsEnglish()==0){
                        toast(returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
                    break;

            }
        }
    }

}
