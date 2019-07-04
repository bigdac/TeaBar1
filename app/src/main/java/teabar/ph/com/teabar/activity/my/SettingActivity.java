package teabar.ph.com.teabar.activity.my;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.ph.teabar.database.dao.DaoImp.FriendInforImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;
import com.ph.teabar.database.dao.UserEntryDao;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.ChangePassActivity;
import teabar.ph.com.teabar.activity.FeedbackActivity;
import teabar.ph.com.teabar.activity.login.LoginActivity;
import teabar.ph.com.teabar.activity.tkActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class SettingActivity extends BaseActivity {

    MyApplication application;
    @BindView(R.id.iv_set_open)
    ImageView iv_set_open;
    @BindView(R.id.tv_set_id)
    TextView tv_set_id;
    @BindView(R.id.rl_set_password)
    RelativeLayout rl_set_password;
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
    @OnClick({R.id.iv_set_fh,R.id.rl_set_password,R.id.rl_set_mess,R.id.rl_set_user,R.id.bt_set_exsit,
            R.id.rl_set_update,R.id.rl_set_tk,R.id.rl_set_clean
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
                SharedPreferences.Editor editor=alermPreferences.edit();
                editor.clear();
                equipmentDao.deleteAll();
                friendInforDao.deleteAll();
                userEntryDao.deleteAll();
                application.removeAllActivity();
                startActivity(LoginActivity.class);
                break;
            case R.id.rl_set_update:
                toast(getText(R.string.toast_update_zx).toString());
                break;

            case R.id.rl_set_tk:
                startActivity(tkActivity.class);
                break;

            case R.id.rl_set_clean:
                customDialog1();

                break;

        }
    }
    /**
     * 自定义对话
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
}
