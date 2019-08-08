package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;

//更改用户密码
public class ChangePassActivity extends BaseActivity {

    MyApplication application;
    QMUITipDialog tipDialog;//dialog
    @BindView(R.id.et_oldPassword)
    EditText et_oldPassword;
    @BindView(R.id.et_newPassword1)
    EditText et_newPassword1;
    @BindView(R.id.et_newPassword2)
    EditText et_newPassword2;

    SharedPreferences preferences;
    String id ;
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
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        id = preferences.getString("userId","");
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    String oldPassword,newPassword1;
    @OnClick({R.id.iv_pass_fh,R.id.bt_change_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_pass_fh:
                finish();
                break;

            case R.id.bt_change_esure:
                 oldPassword = et_oldPassword.getText().toString().trim();
                 newPassword1 = et_newPassword1.getText().toString().trim();
                String newPassword2 = et_newPassword2.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)){
                    toast(getText(R.string.set_pass_ynull).toString());
                    break;
                }
                if (TextUtils.isEmpty(newPassword1)){
                    toast(getText(R.string.set_pass_xnull).toString());
                    break;
                }
                if (TextUtils.isEmpty(newPassword2)){
                    toast(getText(R.string.set_pass_qnull).toString());
                    break;
                }
                if (newPassword1.equals(newPassword2)){
                    showProgressDialog();
                    Map<String,Object> params=new HashMap<>();
                    params.put("id",id);
                    params.put("oldPassword", Utils.shaEncrypt(oldPassword));
                    params.put("newPassword",Utils.shaEncrypt(newPassword1));
                    new ChangePassAsyncTask(ChangePassActivity.this).execute(params);
                }else {
                    toast(getText(R.string.set_pass_notsame).toString());
                }


        }
    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }
    /**
     *  修改密码
     *
     */
    String returnMsg1,returnMsg2;
    class ChangePassAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public ChangePassAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/changePassword",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2 = jsonObject.getString("message3");
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
                    tipDialog.dismiss();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("password",newPassword1);
                    editor.commit();
                    finish();
                    break;

                case "4000":
                    tipDialog.dismiss();
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    tipDialog.dismiss();
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;

            }
        }
    }
}
