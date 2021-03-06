package teabar.ph.com.teabar.activity.my;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.FriendInforImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.ChangePassActivity;
import teabar.ph.com.teabar.activity.FeedbackActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.login.LoginActivity;
import teabar.ph.com.teabar.activity.tkActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.language.LocalManageUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

//APP節本功能設置頁面
public class SettingActivity extends BaseActivity {

    MyApplication application;
    @BindView(R.id.iv_set_open)
    ImageView iv_set_open;
    @BindView(R.id.tv_set_id)
    TextView tv_set_id;
    @BindView(R.id.rl_set_password)
    RelativeLayout rl_set_password;
    @BindView(R.id.tv_now_verson)
     TextView tv_now_verson;
    boolean isOpen = true;
    SharedPreferences preferences;
    EquipmentImpl equipmentDao;
    FriendInforImpl friendInforDao;
    UserEntryImpl userEntryDao;
    int type1;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_setting;
    }
    SharedPreferences alermPreferences;
    String lang;
    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        alermPreferences=getSharedPreferences("alerm",MODE_PRIVATE);

        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        String id = preferences.getString("userId","");
        type1 = preferences.getInt("type1",0);
        boolean Isopen = preferences.getBoolean("open1",true);
        if (!Isopen){
            iv_set_open.setImageResource(R.mipmap.device_close);
        }else {
            iv_set_open.setImageResource(R.mipmap.device_open);
        }
        if (type1!=0){
            rl_set_password.setVisibility(View.GONE);
        }
        tv_set_id.setText(id+"");
        equipmentDao = new EquipmentImpl(getApplicationContext());
        friendInforDao = new FriendInforImpl(getApplicationContext());
        userEntryDao = new UserEntryImpl(getApplicationContext());
        lang = LocalManageUtil.getSelectLanguage(this);
        tv_now_verson.setText(Utils.getVerName(SettingActivity.this));
        Intent service=new Intent(this, MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
    }

    MQService mqService;
    boolean bind;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
    @OnClick({R.id.iv_set_fh,R.id.rl_set_password,R.id.rl_set_mess,R.id.rl_set_user,R.id.bt_set_exsit,
            R.id.rl_set_update,R.id.rl_set_tk,R.id.rl_set_clean,R.id.rl_set_yy
    })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_set_fh:
                finish();
                break;

            case R.id.rl_set_password:
                startActivity(ChangePassActivity.class);
                break;

            case R.id.rl_set_mess:
                if (isOpen){
                    iv_set_open.setImageResource(R.mipmap.device_close);
                    isOpen=false;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open1",false);
                    editor.commit();
                }else {
                    iv_set_open.setImageResource(R.mipmap.device_open);
                    isOpen=true;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open1",true);
                    editor.commit();
                }
                break;
            case R.id.rl_set_user:
                startActivity(FeedbackActivity.class);
                break;

            case R.id.bt_set_exsit:
                if (mqService!=null){
                    mqService.clearAllDevice();
                }
                SharedPreferences.Editor editor=alermPreferences.edit();
                editor.clear();
                friendInforDao.deleteAll();
                userEntryDao.deleteAll();
                application.removeAllActivity();
                startActivity(LoginActivity.class);
                break;
            case R.id.rl_set_update:
                Map<String,Object>  params = new HashMap<>();
                params.put("appType",2);
                new upDateAppAsyncTask().execute(params);

                break;

            case R.id.rl_set_tk:
                startActivity(tkActivity.class);
                break;

            case R.id.rl_set_clean:
                customDialog1();

                break;
            case R.id.rl_set_yy:
                customDialog2();
                break;

        }
    }
    String appVersion = "";
    class upDateAppAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code ="";
            Map <String,Object> params = maps[0];

            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/getAPPVersion",params);
            if (!TextUtils.isEmpty(result)){
                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    code = jsonObject.getString("state");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    appVersion = jsonObject1.getString("appVersion");

                } catch ( Exception e) {
                    e.printStackTrace();
                }


            }else {
                code = "4000";
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "4000":
                    toast(getText(R.string.toast_all_cs).toString());

                    break;
                case "200":
                    String name = Utils.getVerName(SettingActivity.this);
                    if (name.equals(appVersion)){
                        toast(getText(R.string.toast_update_zx).toString());
                    }else {
                        customDialog3();

                    }

                    break;
            }
        }
    }


     /*選擇語言*/
     private void customDialog2(  ) {
         dialog  = new Dialog(this, R.style.MyDialog);
         View view = View.inflate(this, R.layout.popview_languages, null);
         RelativeLayout rl_language_c =   view.findViewById(R.id.rl_language_c);
         RelativeLayout rl_language_e =  view.findViewById(R.id.rl_language_e);
         ImageView iv_mode_3 = view.findViewById(R.id.iv_mode_3);
         ImageView iv_mode_1 = view.findViewById(R.id.iv_mode_1);
        if (!"ENGLISH".equals(lang)){
            iv_mode_3.setVisibility(View.VISIBLE);
            iv_mode_1.setVisibility(View.INVISIBLE);
        }else {
            iv_mode_3.setVisibility(View.INVISIBLE);
            iv_mode_1.setVisibility(View.VISIBLE);
        }

         dialog.setContentView(view);
         //使得点击对话框外部不消失对话框
         dialog.setCanceledOnTouchOutside(true);
         //设置对话框的大小
         view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
         Window dialogWindow = dialog.getWindow();
         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
         lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
         lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);
         lp.gravity = Gravity.CENTER;
         dialogWindow.setAttributes(lp);
         rl_language_c.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 iv_mode_3.setVisibility(View.VISIBLE);
                 iv_mode_1.setVisibility(View.INVISIBLE);
                 selectLanguage(2);
                 dialog.dismiss();
             }

         });
         rl_language_e.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 iv_mode_3.setVisibility(View.INVISIBLE);
                 iv_mode_1.setVisibility(View.VISIBLE);
                 selectLanguage(3);
                 dialog.dismiss();

             }
         });
         dialog.show();

     }


    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(this, select);
        MainActivity.reStart(this);
    }
    /**
     * 自定义对话清理缓存
     */
    Dialog dialog;
    private void customDialog1(  ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setVisibility(View.INVISIBLE);
        et_dia_name.setText(this.getText(R.string.set_hk).toString());
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);
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
                toast(getText(R.string.toast_set_cg).toString());
                dialog.dismiss();

            }
        });
        dialog.show();

    }
    /*
    * 升级
    * */

    private void customDialog3(  ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(this.getText(R.string.set_upapp).toString());
        et_dia_name.setText(this.getText(R.string.set_upapp1).toString());
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);
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
                Uri uri = Uri.parse("https://www.pgyer.com/9obk");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                dialog.dismiss();

            }
        });
        dialog.show();

    }
}
