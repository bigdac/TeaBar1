package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.AddMethodActivity;
import teabar.ph.com.teabar.activity.device.AddMethodActivity1;
import teabar.ph.com.teabar.adpter.MethodAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.MakeMethod;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;


public class MethodActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.rv_method)
    RecyclerView rv_method;
    MyApplication application;
    MethodAdapter methodAdapter;
    List<MakeMethod> mList = new ArrayList<>();
    QMUITipDialog tipDialog;
    SharedPreferences preferences;
    String userId;
    Tea tea;
    @Override
    public void initParms(Bundle parms) {
        tea = (Tea) parms.getSerializable("tea");
    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后...")
                .create();
        tipDialog.show();
    }
    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_method;
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        showProgressDialog();
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","")+"";
        Map<String,Object> params1 = new HashMap<>();
        params1.put("userId",userId);
        new FindMethordAsynTask().execute(params1);
        methodAdapter = new MethodAdapter(this,mList);
        rv_method.setLayoutManager(new LinearLayoutManager(this));
        rv_method.setAdapter(methodAdapter);
        methodAdapter.SetOnclickLister(new MethodAdapter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
               MakeMethod makeMethod = methodAdapter.getmData().get(position);
                Intent intent = new Intent(MethodActivity.this,AddMethodActivity1.class);
                intent.putExtra("type",1);
                intent.putExtra("method",makeMethod);
                intent.putExtra("tea",tea);
                startActivityForResult(intent,3000);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.iv_power_fh,R.id.iv_method_add})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.iv_method_add:
                Intent intent = new Intent(MethodActivity.this,AddMethodActivity1.class);
                intent.putExtra("type",0);
                intent.putExtra("tea",tea);
                startActivityForResult(intent,3000);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==2000){
            showProgressDialog();
            Map<String,Object> params1 = new HashMap<>();
            params1.put("userId",userId);
            new FindMethordAsynTask().execute(params1);
        }
    }

    String returnMsg1,returnMsg2;
    class FindMethordAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/showBrew",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONArray jsonArray  = jsonObject.getJSONArray("data");
                        mList.clear();
                        for (int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MakeMethod makeMethod = new MakeMethod();
                            makeMethod.setId(jsonObject1.getLong("id"));
                            makeMethod.setTemp(jsonObject1.getInt("temperature"));
                            makeMethod.setCapacity(jsonObject1.getInt("waterYield"));
                            makeMethod.setName(jsonObject1.getString("brewName"));
                            makeMethod.setUserId(jsonObject1.getLong("userId"));
                            makeMethod.setTime(jsonObject1.getInt("seconds"));
                            mList.add(makeMethod);
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
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    methodAdapter.setMethod(mList);
//                    toast( returnMsg1);

                    break;
                case "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
                    break;

            }
        }
    }

}
