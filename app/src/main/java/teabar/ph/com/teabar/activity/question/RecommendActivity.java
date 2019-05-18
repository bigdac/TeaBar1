package teabar.ph.com.teabar.activity.question;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddDeviceActivity1;
import teabar.ph.com.teabar.adpter.RecommendAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class RecommendActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.rv_recommend)
    RecyclerView rv_recommend ;
    List<Tea> list = new ArrayList<>();
    MyApplication application;
    RecommendAdapter recommendAdapter;
    SharedPreferences preferences;
    String userId;
    @Override
    public void initParms(Bundle parms) {
        Tea tea1 = (Tea) parms.getSerializable("tea1");
        Tea tea2 = (Tea) parms.getSerializable("tea2");
        Tea tea3 = (Tea) parms.getSerializable("tea3");
        list.add(tea1);
        list.add(tea2);
        list.add(tea3);
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_recommend;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);

         userId = preferences.getString("userId","" )+"";
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        recommendAdapter  = new RecommendAdapter(this,list,userId);
        rv_recommend.setLayoutManager(new GridLayoutManager(this,2));
        rv_recommend.setAdapter(recommendAdapter);
    }

    @OnClick({R.id.tv_recom_skip,R.id.bt_device_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_recom_skip:
                application.removeActivity(this);
                startActivity(AddDeviceActivity1.class);
                break;

            case R.id.bt_device_add:
              customDialog1();
                break;
        }
    }
    /**
     * 添加喜愛dialog
     */
    Dialog dialog;
    boolean which;
    private void customDialog1( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText( R.string.dialog_love_title);
        et_dia_name.setText(R.string.dialog_love_title1);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
                String teaId = "";
                for (int i=0;i<list.size();i++){
                    if (i== list.size()){
                        teaId = teaId+ list.get(i).getId();
                    }else {
                        teaId = teaId+ list.get(i).getId()+",";
                    }
                }
                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId );
                params.put("teaId",teaId);
                new CollectTeaAsyncTask().execute(params);

                dialog.dismiss();

            }
        });
        dialog.show();

    }


    /**
     *  添加喜愛
     *
     */
    String returnMsg1,returnMsg2;
    class CollectTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
                ip ="/app/collectTea";
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+ip,prarms);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {
                case "200":
                    if (application.IsEnglish()==0){
                        toast(  returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
                    application.removeActivity(RecommendActivity.this);
                    startActivity(AddDeviceActivity1.class);
                    break;

                case "4000":
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (application.IsEnglish()==0){
                        toast(  returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
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

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText( this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
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
