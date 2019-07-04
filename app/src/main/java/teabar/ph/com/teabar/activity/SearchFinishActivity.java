package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EvaluateAdapter;
import teabar.ph.com.teabar.adpter.MyplanAdapter;
import teabar.ph.com.teabar.adpter.TeaAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.chat.adpter.TextWatcherAdapter;
import teabar.ph.com.teabar.view.FlowTagView;

public class SearchFinishActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.rv_main_tealist)
    RecyclerView rv_main_tealist;
    @BindView(R.id.rv_main_jh)
    RecyclerView rv_main_jh;
    String userId ;
    String name;
    SharedPreferences preferences;
    List<Tea> teaList= new ArrayList<>();
    List<Plan> planList = new ArrayList<>();
    TeaAdapter teaAdapter1;
    MyplanAdapter myplanAdapter ;
    @Override
    public void initParms(Bundle parms) {
        name = parms.getString("searchName");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_searchfinish;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }

        application.addActivity(this);
        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        userId = preferences.getString("userId","");
        teaAdapter1 = new TeaAdapter(this,teaList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_main_tealist.setLayoutManager(linearLayoutManager);
        rv_main_tealist.setAdapter(teaAdapter1);
        myplanAdapter = new MyplanAdapter(this,planList);
        //解决滑动冲突、滑动不流畅
        rv_main_tealist.setHasFixedSize(true);
        rv_main_tealist.setNestedScrollingEnabled(false);
        rv_main_jh.setLayoutManager(new LinearLayoutManager(this));
        rv_main_jh.setAdapter(myplanAdapter);
        rv_main_jh.setHasFixedSize(true);
        rv_main_jh.setNestedScrollingEnabled(false);
        new getSearchResultAsynTask().execute();
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
    @OnClick({R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                setResult(3000);
                finish();
                break;
        }

    }
    String returnMsg1;
    /*  获取問題*/
    class getSearchResultAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/searchTea?userId="+userId+"&search="+name);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {

                            JSONArray jsonArray  = jsonObject1.getJSONArray("tea");
                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                Tea tea = gson.fromJson(jsonArray.get(i).toString(),Tea.class);
                                teaList.add(tea);
                            }
                            JSONArray jsonArray1  = jsonObject1.getJSONArray("plan");
                            for (int i = 0;i<jsonArray1.length();i++){
                                Plan plan = gson.fromJson(jsonArray1.get(i).toString(),Plan.class);
                                planList.add(plan);
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
                    if (teaList.size()==0&&planList.size()==0){
                        toast(getText(R.string.toast_search_no).toString());
                    }
                    teaAdapter1.update(teaList);
                    myplanAdapter.update(planList);
                    break;
                case "4000":

                    toast( getText(R.string.toast_all_cs).toString());

                    break;
                default:

                    toast( getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(3000);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);

    }
}
