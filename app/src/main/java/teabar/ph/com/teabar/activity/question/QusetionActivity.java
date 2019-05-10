package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jmessage.support.google.gson.Gson;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.ScoreActivity;
import teabar.ph.com.teabar.adpter.AnswerAdpter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Question;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class QusetionActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.tv_question_title)
    TextView tv_question_title;
    @BindView(R.id.tv_question_title1)
    TextView tv_question_title1;
    @BindView(R.id.rv_question_da)
    RecyclerView rv_question_da;
    AnswerAdpter answerAdpter ;
    List<Question> list=new ArrayList<>();
    List<examOptions> optionsList=new ArrayList<>();
    com.alibaba.fastjson.JSONArray jsonArray;
    com.alibaba.fastjson.JSONObject jsonObject;
    long userId;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    boolean choose = false;//是否选择
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
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getLong("userId",0);
        answerAdpter = new AnswerAdpter(this,optionsList);
        rv_question_da.setLayoutManager(new LinearLayoutManager(this));
        rv_question_da.setAdapter(answerAdpter);
        jsonArray = new com.alibaba.fastjson.JSONArray();
        new getTeaListAsynTask().execute();
        answerAdpter.SetOnclickLister(new AnswerAdpter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position, String answer ,int examId) {
                try {
                    jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("answer",answer);
                    jsonObject.put("examId",examId);
                    if (!TextUtils.isEmpty(answer)){
                        choose = true;
                    }else {
                        choose = false;
                    }
                    Log.e(TAG, "onClikner: -->"+jsonObject.toString() );
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后...")
                .create();
        tipDialog.show();
    }
    String returnMsg1,returnMsg2;
    int  examTitle =1;

    /*  获取問題*/
    class getTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/exam/getExam" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONArray jsonArray =  jsonObject.getJSONArray("data");
                        if ("200".equals(code)) {
                            examTitle=1;
                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                List<examOptions> examOptions = new ArrayList<>();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Question question = gson.fromJson(jsonArray.get(i).toString(),Question.class);
                                JSONArray jsonArray1 =jsonObject1.getJSONArray("examOptions");
                                for (int j=0;j<jsonArray1.length();j++){
                                    examOptions examOptions1 = gson.fromJson(jsonArray1.get(j).toString(), teabar.ph.com.teabar.pojo.examOptions.class );
                                    examOptions.add(examOptions1);
                                }
                                question.setExamOptions(examOptions);
                                list.add(question);
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
                  Question question =  list.get(0);
                    tv_question_title1.setText(question.getType1());
                    tv_question_title.setText(question.getExamNum()+"、"+question.getExamTitle());
                    List<examOptions> examOptions  = question.getExamOptions();
                    answerAdpter.setmData(examOptions,question.getMultiple(),question.getSelectnum());
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
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @OnClick({R.id.iv_power_fh,R.id.bt_question_finish})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.bt_question_finish:
                if (choose) {
                    if (examTitle< list.size()) {
                        Question question = list.get(examTitle);
                        tv_question_title1.setText(question.getType1());
                        tv_question_title.setText(question.getExamNum() + "、" + question.getExamTitle());
                        List<examOptions> examOptions = question.getExamOptions();
                        answerAdpter.setmData(examOptions, question.getMultiple(), question.getSelectnum());
                        examTitle++;
                        jsonArray.add(jsonObject);
                        answerAdpter.cleanAnswerList();
                        Log.e(TAG, "onClick: -->" + jsonArray.toString() + "..." + examTitle);
                        choose = false;

                    }else {
                        showProgressDialog();
                        jsonArray.add(jsonObject);
                        toast("已经答完");
                        Map<String,Object> params = new HashMap<>();
                        params.put("exam",jsonArray);
                        params.put("userId",userId);
                        new examEndTeaAsyncTask().execute(params);
                    }
                }else {
                    toast("请选择答案在继续");
                }
                break;

        }

    }
    /**
     *  问卷结束
     *
     */
    double bodyGrades,nutritionGrades,mindGradesc,lifeGrades;

    List<Tea> teaList = new ArrayList<>();
    class examEndTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/exam/examEnd",prarms);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                             bodyGrades = jsonObject1.getDouble("bodyGrades");
                             nutritionGrades = jsonObject1.getDouble("nutritionGrades");
                             mindGradesc = jsonObject1.getDouble("mindGrades");
                             lifeGrades = jsonObject1.getDouble("lifeGrades");
                             JSONArray jsonArray = jsonObject1.getJSONArray("teaList");
                             Gson gson = new Gson();
                             for (int i = 0;i<jsonArray.length();i++){
                                 Tea tea = gson.fromJson(jsonArray.get(i).toString(),Tea.class);
                                 teaList.add(tea);
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
                    tipDialog.dismiss();
                    Intent intent = new Intent(QusetionActivity.this,PowerpicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("teaList", (Serializable) teaList);
                    bundle.putDouble("bodyGrades",bodyGrades);
                    bundle.putDouble("nutritionGrades",nutritionGrades);
                    bundle.putDouble("mindGradesc",mindGradesc);
                    bundle.putDouble("lifeGrades",lifeGrades);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case "4000":
                    tipDialog.dismiss();
                    toast(  "连接超时，请重试");
                    break;
                default:
                    tipDialog.dismiss();
                   toast( returnMsg1);
                    break;

            }
        }
    }

}
