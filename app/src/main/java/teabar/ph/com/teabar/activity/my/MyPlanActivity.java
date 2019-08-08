package teabar.ph.com.teabar.activity.my;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.PlanInformActivity;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.util.HttpUtils;

//我的個人計畫頁面
public class MyPlanActivity extends BaseActivity {
    @BindView(R.id.rv_myplan)
    RecyclerView rv_myplan;

    PlanAdapter mPlanAdapter;//個人計畫列表適配器
    List<Plan> plans = new ArrayList<>();
    MyApplication application;
    String userId;
    Map<String,Object> params=new HashMap<>();
    @Override
    public void initParms(Bundle parms) {
        userId=parms.getString("userId");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_myplan;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);

        params.put("userId",userId);
        Log.i("userId","-->"+userId);
        new GetPersonPlanAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);


        mPlanAdapter = new PlanAdapter(this,plans);
        rv_myplan.setLayoutManager(new LinearLayoutManager(this));
        rv_myplan.setAdapter(mPlanAdapter);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_plan_fh,R.id.tv_plan_inform})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_plan_fh:
                finish();
                break;

            case R.id.tv_plan_inform:
                startActivity(PlanInformActivity.class);
                break;
        }
    }
    class GetPersonPlanAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,MyPlanActivity>{

        public GetPersonPlanAsync(MyPlanActivity myPlanActivity) {
            super(myPlanActivity);
        }

        @Override
        protected Integer doInBackground(MyPlanActivity myPlanActivity, Map<String, Object>... maps) {
            String url=HttpUtils.ipAddress+"/app/getUserPlan";
            int code=0;
            Map<String,Object> map=maps[0];
            try {
                String result=HttpUtils.postOkHpptRequest(url,map);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("state");
                    if (code==200){
                        JSONArray data=jsonObject.getJSONArray("data");
                        int size=data.length();
                        for (int i = 0; i <size ; i++) {
                            JSONObject object=data.getJSONObject(i);
                            Gson gson=new Gson();
                            Plan plan=gson.fromJson(object.toString(), Plan.class);
                            plans.add(plan);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(MyPlanActivity myPlanActivity, Integer integer) {
            if (integer==200 && !plans.isEmpty()){
                mPlanAdapter.notifyDataSetChanged();
            }
        }
    }
}
