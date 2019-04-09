package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class ChangePassActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    MyApplication application;
    QMUITipDialog tipDialog;//dialog
    @BindView(R.id.et_oldPassword)
    EditText et_oldPassword;
    @BindView(R.id.et_newPassword1)
    EditText et_newPassword1;
    @BindView(R.id.et_newPassword2)
    EditText et_newPassword2;

    SharedPreferences preferences;
    long id ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_changpswd;
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
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        id = preferences.getLong("userId",0);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_pass_fh,R.id.bt_change_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_pass_fh:
                finish();
                break;

            case R.id.bt_change_esure:
                String oldPassword = et_oldPassword.getText().toString().trim();
                String newPassword1 = et_newPassword1.getText().toString().trim();
                String newPassword2 = et_newPassword2.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)){
                    toast("原密码不能为空");
                    break;
                }
                if (TextUtils.isEmpty(newPassword1)){
                    toast("新密码不能为空");
                    break;
                }
                if (TextUtils.isEmpty(newPassword2)){
                    toast("新密码不能为空");
                    break;
                }
                if (newPassword1.equals(newPassword2)){
                    showProgressDialog();
                    Map<String,Object> params=new HashMap<>();
                    params.put("id",id);
                    params.put("oldPassword",oldPassword);
                    params.put("newPassword",newPassword1);
                    new ChangePassAsyncTask().execute(params);
                }else {
                    toast("两次密码输入不相同");
                }


        }
    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在修改密码，请稍后")
                .create();
        tipDialog.show();
    }
    /**
     *  修改密码
     *
     */
    String returnMsg1;
    class ChangePassAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/changePassword",prarms);

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


                case "4000":
                    tipDialog.dismiss();
                    toast( "连接超时，请重试");
                    break;
                default:
                    tipDialog.dismiss();
                    finish();
                    toast( returnMsg1);
                    break;

            }
        }
    }
}
