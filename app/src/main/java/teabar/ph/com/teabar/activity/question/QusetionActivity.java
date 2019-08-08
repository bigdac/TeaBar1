package teabar.ph.com.teabar.activity.question;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
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
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.AnswerAdpter;
import teabar.ph.com.teabar.adpter.BasicExamAdapter1;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Question;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.FlowTagView;
import teabar.ph.com.teabar.view.PlanProgressBar;

//問卷調查頁面
public class QusetionActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_question_title)
    TextView tv_question_title;
    @BindView(R.id.tv_question_title1)
    TextView tv_question_title1;
    @BindView(R.id.rv_question_da)
    RecyclerView rv_question_da;
    AnswerAdpter answerAdpter ;
    @BindView(R.id.pl_progress)
    PlanProgressBar pl_progress;
    @BindView(R.id.tv_question_ts)
    TextView tv_question_ts;
    @BindView(R.id.bt_question_finish)
    Button bt_question_finish;
    @BindView(R.id.fv_message)
    FlowTagView fv_message;
    List<Question> list=new ArrayList<>();
    List<examOptions> optionsList=new ArrayList<>();
    com.alibaba.fastjson.JSONArray jsonArray;
    com.alibaba.fastjson.JSONObject jsonObject;
    BasicExamAdapter1 basicExamAdapter ;
    String userId;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    List <String> stringList = new ArrayList<>();
    boolean choose = false;//是否选择
    @Override
    public void initParms(Bundle parms) {

    }
    int langauge;
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
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","");

        langauge =application.IsEnglish();
        answerAdpter = new AnswerAdpter(this,optionsList,langauge);
        rv_question_da.setLayoutManager(new LinearLayoutManager(this));
        rv_question_da.setAdapter(answerAdpter);
        basicExamAdapter = new BasicExamAdapter1(this,R.layout.item_basicexam);
        fv_message.setAdapter(basicExamAdapter);
        bt_question_finish.setBackgroundResource(R.mipmap.question_nonext);
        bt_question_finish.setClickable(false);
        jsonArray = new com.alibaba.fastjson.JSONArray();
        new getTeaListAsynTask(this).execute();
        answerAdpter.SetOnclickLister(new AnswerAdpter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position, String answer ,int examId) {
                try {
                    jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("answer",answer);
                    jsonObject.put("examId",examId);
                    if (!TextUtils.isEmpty(answer)){
                        choose = true;
                        bt_question_finish.setBackgroundResource(R.mipmap.question_button);
                        bt_question_finish.setClickable(true);
                    }else {
                        choose = false;
                        bt_question_finish.setBackgroundResource(R.mipmap.question_nonext);
                        bt_question_finish.setClickable(false);
                    }
                    Log.e(TAG, "onClikner: -->"+jsonObject.toString() );
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        });

        basicExamAdapter.SetOnclickLister(new BasicExamAdapter1.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
                examOptions examOptions= basicExamAdapter.getMdata(position);
                String num = examOptions.getOptionNum();

                if (stringList.contains(num)){
                    stringList.remove(num);
                }else {
                    if (stringList.size()<3){
                        if (chooseUnit){
                            basicExamAdapter.chooseUnit(false,position);
                            chooseUnit= false;
                            stringList.clear();
                        }
                        stringList.add(num);
                    }
                }
                if (examTitle==19) {
                    if (position == basicExamAdapter.getCount() - 1) {
                        stringList.clear();
                        stringList.add(num);
                        basicExamAdapter.chooseUnit(true, position);
                        chooseUnit = true;
                    }
                }

                jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.clear();
                jsonObject.put("answer", Utils.listToString(stringList));
                jsonObject.put("examId", examOptions.getExamId());
                if (stringList.size()>0){
                    choose = true;
                    bt_question_finish.setBackgroundResource(R.mipmap.question_button);
                    bt_question_finish.setClickable(true);
                }else {
                    choose = false;
                    bt_question_finish.setBackgroundResource(R.mipmap.question_nonext);
                    bt_question_finish.setClickable(false);
                }
                Log.e("GGGGGGGGGGGGGGGG", "itemClick: -->"+stringList.size() );
            }
        });

    }
    boolean chooseUnit =false;
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
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
//            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/webExam/getExam?type="+langauge+"&currentPage=1&pageSize=100" );
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
                                if (question.getExamLanguage()==langauge){
                                    list.add(question);
                                }

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
                    Question question =  list.get(0);
                    tv_question_title1.setText( getQuestionType(question.getType()));
                    tv_question_title.setText( question.getExamTitle());
                    List<examOptions> examOptions  = question.getExamOptions();
                    answerAdpter.setmData(examOptions,question.getMultiple(),question.getSelectnum());
                    tv_question_ts.setText("1/"+list.size()+getText(R.string.question_answer).toString());
                    pl_progress.setProgress((int)(1.0f/(float) list.size()*100));
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

    public String getQuestionType (int type){
        String questionType="";
        switch (type){
            case 0:
                questionType = getText(R.string.question_type1).toString();
                break;
            case 1:
                questionType = getText(R.string.question_type2).toString();
                break;
            case 2:
                questionType = getText(R.string.question_type3).toString();
                break;
            case 3:
                questionType = getText(R.string.question_type4).toString();
                break;
            case 4:
                questionType = getText(R.string.question_type5).toString();
                break;
            case 5:
                questionType = getText(R.string.question_type6).toString();
                break;
            case 6:
             questionType = getText(R.string.question_type7).toString();
             break;
        }

        return questionType;
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
    /**
     * 自定义对话
     */
    Dialog dialog;
    private void customDialog1(  ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del1, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        et_dia_name.setText(this.getText(R.string.question_back).toString());
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);

        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();

            }
        });
        dialog.show();

    }
    /**
     * 自定义对话机器人
     */

    private void customDialog2(  ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.item_questionjqr, null);
        ImageView iv_dia_gif = view.findViewById(R.id.iv_dia_gif);
        Glide.with(this)
                .load(R.mipmap.jqir)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(iv_dia_gif, -1));
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.70f);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        dialog.show();

    }
    @OnClick({R.id.iv_power_fh,R.id.bt_question_finish})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                customDialog1();
                break;

            case R.id.bt_question_finish:
                if (choose) {
                    bt_question_finish.setBackgroundResource(R.mipmap.question_nonext);
                    bt_question_finish.setClickable(false);
                    if (examTitle< list.size()) {
                        Question question = list.get(examTitle);
                        examTitle++;
                        tv_question_title1.setText(getQuestionType(question.getType()));
                        tv_question_title.setText(  question.getExamTitle());
                        List<examOptions> examOptions = question.getExamOptions();
                        jsonArray.add(jsonObject);
//                        if ("1".equals(question.getMultiple() ))
                            if (examTitle==2||examTitle==19){
                            fv_message.setVisibility(View.VISIBLE);
                            rv_question_da.setVisibility(View.INVISIBLE);
                            basicExamAdapter.setItems(examOptions);

//                            jsonArray.add(jsonObject);
                            stringList.clear();
                            basicExamAdapter.refrashNum();
                        }else {
                            fv_message.setVisibility(View.INVISIBLE);
                            rv_question_da.setVisibility(View.VISIBLE);
                            answerAdpter.setmData(examOptions, question.getMultiple(), question.getSelectnum());
//                            jsonArray.add(jsonObject);
                            answerAdpter.cleanAnswerList();
                        }

                        tv_question_ts.setText(examTitle+"/"+list.size()+getText(R.string.question_answer).toString());
                        pl_progress.setProgress((int)((float)examTitle/(float) list.size()*100));
                        Log.e(TAG, "onClick: -->" + jsonArray.toString() + "..." + examTitle);
                        choose = false;

                    }else {
//                        showProgressDialog();
                        customDialog2();
                        jsonArray.add(jsonObject);
                        tv_question_ts.setText(list.size()+"/"+list.size()+getText(R.string.question_answer).toString());
                        pl_progress.setProgress(100);
//                        toast("已经答完");
                        Map<String,Object> params = new HashMap<>();
                        params.put("exam",jsonArray);
                        params.put("userId",userId);
                        new examEndTeaAsyncTask(this).execute(params);
                    }
                }else {
                    toast(getText(R.string.question_toa_xz).toString());
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
    class examEndTeaAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public examEndTeaAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
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
        protected void onPostExecute(BaseActivity baseActivity, String s) {


            switch (s) {
                case "200":
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessageDelayed(message,3000);

                    break;

                case "4000":

//                    toast(  "连接超时，请重试");
                    break;
                default:

//                   toast( returnMsg1);
                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                Intent intent = new Intent(QusetionActivity.this,ScoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("teaList", (Serializable) teaList);
                bundle.putDouble("bodyGrades",bodyGrades);
                bundle.putDouble("nutritionGrades",nutritionGrades);
                bundle.putDouble("mindGradesc",mindGradesc);
                bundle.putDouble("lifeGrades",lifeGrades);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

}
