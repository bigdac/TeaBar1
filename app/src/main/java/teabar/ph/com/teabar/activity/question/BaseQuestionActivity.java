package teabar.ph.com.teabar.activity.question;


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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.questionFragment.Question1Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question2Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question3Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question4Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question5Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question6Fragment;
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
    BaseFragment [] baseFragments;
    String  sex,birthday,country,province,city,area;
    String height , weight;
    int  conceive=0; //懷孕  1 懷孕 0 沒懷孕
    SharedPreferences preferences;
    MyApplication application;
    String userId;
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
         question1Fragment = new Question1Fragment();
         question2Fragment = new Question2Fragment();
         question3Fragment = new Question3Fragment();
         question4Fragment = new Question4Fragment();
         question5Fragment = new Question5Fragment();
         question6Fragment = new Question6Fragment();
         baseFragments= new BaseFragment[]{question1Fragment,question2Fragment,question3Fragment,question4Fragment,question5Fragment,question6Fragment};
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.li_question,question1Fragment ).commit();


    }

    public int getLanguage (){
        return application.IsEnglish();
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
            new SetBasicAsyncTask().execute(params);
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

    public void     setMesssage1(int  conceive ){
         this.conceive = conceive;
    }
    public void setMesssage2(String height ,String weight){
        this.height = height;
        this.weight = weight;
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    /**
     *  基礎問卷
     *
     */
    String returnMsg1;
    class SetBasicAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/setBasic",prarms);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");

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

                    break;

                case "4000":

                    toast( "连接超时，请重试");
                    break;
                default:
                    toast( returnMsg1);
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
