package teabar.ph.com.teabar.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
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
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.UserEntry;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.SharePreferenceManager;
import teabar.ph.com.teabar.util.ToastUtil;

public class RegisterActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
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
    SharedPreferences preferences;
    QMUITipDialog tipDialog;//dialog
    String user;
    String password;
    UserEntryImpl userEntryDao;
    EquipmentImpl equipmentDao;
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

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        application.addActivity(this);
        userEntryDao = new UserEntryImpl(getApplicationContext());
        equipmentDao = new EquipmentImpl(getApplicationContext());
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后...")
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
        @OnClick({R.id.tv_regist_login,R.id.bt_register_code,R.id.et_regist_nick,R.id.bt_regist_ensure})
        public void onClick(View view){
            switch (view.getId()){
                case R.id.tv_regist_login:
                    finish();
                    break;

                case R.id.bt_register_code:
                    user = et_regist_user.getText().toString().trim();
                    if (TextUtils.isEmpty(user)){
                        toast("账户不能为空");
                    }else {
                        Map<String,Object> params1=new HashMap<>();
                        if (user.contains("@")){
                            params1.put("email",user);
                        }else {
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
                        toast("昵称不能为空");
                        break;
                    }
                    if (TextUtils.isEmpty(code)){
                        toast( "请输入验证码");
                        break;
                    }
                    if (TextUtils.isEmpty(password)){
                        toast( "请输入密码");
                        break;
                    }
                    if (TextUtils.isEmpty(user)){
                        toast( "请输入手机号或邮箱");
                        break;
                    }
                    if (password.length()<6||password.length()>18){
                        toast( "密码位数应该大于6小于18");
                    }else {
                        Map<String,Object> params=new HashMap<>();
                        params.put("userName",nick);
                        params.put("verification",code);
                        params.put("password",password);
                        if (user.contains("@")){
                         params.put("email",user);
                        }else {
                         params.put("phone",user);
                        }
                        showProgressDialog();
                        new RegistAsyncTask().execute(params);

                    }
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
    long userId;
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
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)) {
                            JSONObject returnData = jsonObject.getJSONObject("data");
                             userId = returnData.getLong("userId");
                            String userName = returnData.getString("userName");
                            String token = returnData.getString("token");
                             type = returnData.getInt("type");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user",user);
                            editor.putString("password",password);
                            editor.putLong("userId", userId);
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
                    toast( "登录成功");

                    break;
                case "4000":
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
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

                    UserEntry user = userEntryDao.findById(userId);
                    if (null == user) {
                        user = new UserEntry(userId,username, appKey);
                        userEntryDao.insert(user);
                    }
                    Intent intent = new Intent(RegisterActivity.this, MQService.class);
                    startService(intent);// 启动服务
                    startActivity( BaseQuestionActivity.class);

                } else {
                    toast(  "登陆失败" + responseMessage);
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
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
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
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
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
                bt_register_code.setText("重新发送");
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
