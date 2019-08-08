package teabar.ph.com.teabar.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.activity.tkActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.UserEntry;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.SharePreferenceManager;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;

public class RegisterActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.et_regist_nick)
    EditText et_regist_nick;
    @BindView(R.id.et_regist_user)
    EditText et_regist_user;
    @BindView(R.id.et_regist_code)
    EditText et_regist_code;
    @BindView(R.id.et_regist_pasw)
    EditText et_regist_pasw;
    @BindView(R.id.bt_register_code)
    Button bt_register_code;
    @BindView(R.id.tv_login_tk)
    TextView tv_login_tk;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;//dialog
    String user;
    String password;
    UserEntryImpl userEntryDao;
    EquipmentImpl equipmentDao;
    int language;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_register;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        if (application.IsEnglish()==0){
            tv_login_tk.setText(Html.fromHtml("通過登錄你同意Lify的<u>《隱私政策和條款》</u>"));
        }else {
            tv_login_tk.setText(Html.fromHtml(" By signing up, you agree to our <u>Terms of Use and Cookies&Privacy Policy.</u>"));
        }
        language = application.IsEnglish();
        userEntryDao = new UserEntryImpl(getApplicationContext());
        equipmentDao = new EquipmentImpl(getApplicationContext());
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        }

    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        return super.getResources();

    }

    @Override
        public void doBusiness(Context mContext) {

        }

        @Override
        public void widgetClick(View v) {

        }
        @OnClick({ R.id.bt_register_code,R.id.et_regist_nick,R.id.bt_regist_ensure ,R.id.tv_login_regist,R.id.regist_back})
        public void onClick(View view){
            switch (view.getId()){


                case R.id.bt_register_code:
                    user = et_regist_user.getText().toString().trim();
                    if (TextUtils.isEmpty(user)){
                        toast(getText(R.string.register_toa_zh).toString());
                    }else {
                        Map<String,Object> params1=new HashMap<>();
                        if (user.contains("@")){
                            params1.put("email",user);
                        }else {
                            if (user.length()==8){
                                user = "+852"+user;
                            }
                            params1.put("phone",user);
                        }
                        showProgressDialog();
                        new HasCountAsyncTask().execute(params1);

                    }

                    break;

                case R.id.bt_regist_ensure:

                    String nick =  et_regist_nick.getText().toString().trim();
                    String code=et_regist_code.getText().toString().trim();
                    password=et_regist_pasw.getText().toString().trim();
                    user = et_regist_user.getText().toString().trim();
                    if (TextUtils.isEmpty(nick)){
                        toast( getText(R.string.register_toa_name).toString());
                        break;
                    }
                    if (TextUtils.isEmpty(code)){
                        toast( getText(R.string.login_et_code).toString());
                        break;
                    }
                    if (TextUtils.isEmpty(password)){
                        toast( getText(R.string.register_toa_pass).toString());
                        break;
                    }
                    if (TextUtils.isEmpty(user)){
                        toast( getText(R.string.login_et_user).toString());
                        break;
                    }
                    if (password.length()<6||password.length()>18){
                        toast( getText(R.string.register_toa_passlen).toString());
                        break;
                    }

                        Map<String,Object> params=new HashMap<>();
                        params.put("userName",nick);
                        params.put("verification",code);
                        String MD5password = Utils.shaEncrypt(password);
                        params.put("password",MD5password);
                        if (user.contains("@")){
                         params.put("email",user);
                        }else {
                         params.put("phone",user);
                        }
                        showProgressDialog();
                        new RegistAsyncTask().execute(params);

                break;

                case R.id.tv_login_regist:
                   if (!Utils.isFastClick()){
                       startActivity(tkActivity.class);
                   }

                    break;

                case R.id.regist_back:
                    finish();
                    break;
        }
    }

    //显示dialog
    public void showProgressDialog() {
        tipDialog.show();
    }

    /**
     *  注册
     *
     */
    int type;
    String userId;
    String returnMsg1,returnMsg2;
    class RegistAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/regist",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2 = jsonObject.getString("message3");
                        if ("200".equals(code)) {
                            JSONObject returnData = jsonObject.getJSONObject("data");
                             userId = returnData.getString("userId");
                            String userName = returnData.getString("userName");
                            String token = returnData.getString("token");
                             type = returnData.getInt("type");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user",user);
                            editor.putString("password",password);
                            editor.putString("userId", userId);
                            editor.putString("token",token);
                            editor.putString("userName",userName);
                            editor.putInt("type",type);
                            editor.commit();
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
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    LoginJM();
//                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    Intent intent = new Intent(RegisterActivity.this, MQService.class);
                    startService(intent);// 启动服务
                    startActivity( BaseQuestionActivity.class);
                    break;
                case "4000":
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;

            }
        }
    }



    public void LoginJM(){
        JMessageClient.login(userId+"", "123456", new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {

                if (responseCode == 0) {
                    SharePreferenceManager.setCachedPsw(password);
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    File avatarFile = myInfo.getAvatarFile();
                    //登陆成功,如果用户有头像就把头像存起来,没有就设置null
                    if (avatarFile != null) {
                        SharePreferenceManager.setCachedAvatarPath(avatarFile.getAbsolutePath());
                    } else {
                        SharePreferenceManager.setCachedAvatarPath(null);
                    }
                    String username = myInfo.getUserName();
                    String appKey = myInfo.getAppKey();

                    UserEntry user = userEntryDao.findById(1);
                    if (null == user) {
                        user = new UserEntry(1,userId,username, appKey);
                        userEntryDao.insert(user);
                    }


                } else {
//                    toast(  "登陆失败" + responseMessage);
                }
            }
        });
    }


    /**
     *  获取验证码
     *
     */
    class GetCheckCodeAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/getCheckCode",prarms);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "200":
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    countTimer=new CountTimer(60000,1000);
                    countTimer.start();
                    break;
                case "4000":
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;

            }
        }
    }

    /**
     *  检验账号是否存在
     *
     */
    class HasCountAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/checkUser",prarms);
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

                case "400":
                    Map<String,Object> params1=new HashMap<>();
                    if (user.contains("@")){
                        params1.put("email",user);
                    }else {
                        params1.put("phone",user);
                    }
                    new GetCheckCodeAsyncTask().execute(params1);
                    break;
                case "4000":
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;

            }
        }
    }
    CountTimer  countTimer;
    class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时过程中调用
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Tag", "倒计时=" + (millisUntilFinished/1000));
            if (bt_register_code!=null){
                bt_register_code.setText(millisUntilFinished / 1000 + "s");
                //设置倒计时中的按钮外观
                bt_register_code.setClickable(false);//倒计时过程中将按钮设置为不可点击
            }
        }

        /**
         * 倒计时完成后调用
         */
        @Override
        public void onFinish() {
            Log.e("Tag", "倒计时完成");
            if (bt_register_code!=null){
                bt_register_code.setText(getText(R.string.register_toa_cx).toString());
                bt_register_code.setClickable(true);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countTimer!=null)
            countTimer.cancel();
    }
}
