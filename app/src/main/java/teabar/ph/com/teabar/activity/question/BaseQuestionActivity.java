package teabar.ph.com.teabar.activity.question;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.questionFragment.Question1Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question2Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question3Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question4Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question5Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question6Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question7Fragment;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class BaseQuestionActivity extends BaseActivity {
    @BindView(R.id.li_question)
    LinearLayout li_question;
    Question1Fragment question1Fragment;
    Question2Fragment question2Fragment;
    Question3Fragment question3Fragment;
    Question4Fragment question4Fragment;
    Question5Fragment question5Fragment;
    Question6Fragment question6Fragment;
    Question7Fragment question7Fragment;
    BaseFragment [] baseFragments;
    String  sex,birthday,country,province,city,area;
    String height , weight;
    int  conceive=0; //懷孕  1 懷孕 0 沒懷孕
    SharedPreferences preferences;
    MyApplication application;
    String userId;
    int type ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_basequestion;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
         userId = preferences.getString("userId","");
         type = preferences.getInt("type",0);
         question1Fragment = new Question1Fragment();
         question2Fragment = new Question2Fragment();
         question3Fragment = new Question3Fragment();
         question4Fragment = new Question4Fragment();
         question5Fragment = new Question5Fragment();
         question6Fragment = new Question6Fragment();
        question7Fragment = new Question7Fragment();
         baseFragments= new BaseFragment[]{question1Fragment,question2Fragment,question3Fragment,question4Fragment,question5Fragment,question6Fragment,question7Fragment};
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.li_question,question1Fragment ).commit();
        new getTeaListAsynTask(this).execute();


    }

    public int getLanguage (){
        return application.IsEnglish();
    }

    public int getType (){
        return type;
    }
    public void  rePlaceFragment (int type){
        if (type==5){
            Map<String,Object> params = new HashMap<>();
            params.put("weight",weight);
            params.put("userId",userId);
            params.put("sex",sex);
            params.put("height",height);
            params.put("birthday",birthday);
            params.put("country",country);
            params.put("province",province);
            params.put("city",city);
            params.put("area",area);
            params.put("conceive",conceive);
            new SetBasicAsyncTask(this).execute(params);
        }
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.li_question,baseFragments[type]).commit();

    }
    public int getNunber(){
       return application.IsEnglish();
    }

    public void setMesssage(  String sex ,String birthday  ){

        this.sex = sex;
        this.birthday = birthday;
    }
    public void setAdress(String country,String province,String city,String area){
        this.city = null;
        this.province = null;
        this.city = null;
        this.area = null;
        this.country = country;
        this.province = province;
        this.city = city;
        this.area = area;
        Log.e(TAG, "setAdress: -->"+country+"..."+province+"..."+city+"..."+area );
    }

    public void setMesssage1(int  conceive ){
         this.conceive = conceive;
    }
    public void setMesssage2(String height ,String weight){
        this.height = height;
        this.weight = weight;
    }
    Tea tea1;
    Tea tea2;
    Tea tea3;
    public void setMesssage3(Tea tea1 ,Tea tea2, Tea tea3){
        this.tea1 = tea1;
        this.tea2 = tea2;
        this.tea3 = tea3;
    }

    public void toStarActivity(int choose){
        Intent intent = new Intent(this,RecommendActivity.class);
                    intent.putExtra("tea1",tea1);
                    intent.putExtra("tea2",tea2);
                    intent.putExtra("tea3",tea3);
                    intent.putExtra("choose",choose);
                    startActivity(intent);
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    public String getExamTitle(){
        return examTitle;
    }
    public List<examOptions> getQuesList(){
        return list;
    }

    String returnMsg1,returnMsg2;
    String examTitle;
    List<examOptions> list = new ArrayList();
    /*  获取問題*/
    class getTeaListAsynTask extends BaseWeakAsyncTask<Void,Void,String,BaseActivity> {

        public getTeaListAsynTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Void... voids) {
            int type =29;
            if ( getLanguage()==0){
                type=2;
            }
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/exam/getBasicExam?examId="+type );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            examTitle = jsonObject1.getString("examTitle");
                            JSONArray jsonArray  = jsonObject1.getJSONArray("examOptions");
                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                examOptions examOptions = gson.fromJson(jsonArray.get(i).toString(),examOptions.class);
                                list.add(examOptions);
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

                    break;
                case "4000":

                    toast( getText(R.string.toast_all_cs).toString());

                    break;
                default:

                    toast(   getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }
    /**
     *  基礎問卷
     *
     */

    class SetBasicAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public SetBasicAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/setBasic",prarms);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2=jsonObject.getString("message3");
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

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(BaseQuestionActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    application.removeAllActivity();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
