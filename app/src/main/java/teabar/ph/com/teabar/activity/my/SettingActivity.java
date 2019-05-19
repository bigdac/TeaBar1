package teabar.ph.com.teabar.activity.my;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
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
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    MyApplication application;
    @BindView(R.id.iv_set_open)
    ImageView iv_set_open;
    @BindView(R.id.tv_set_id)
    TextView tv_set_id;
    boolean isOpen = true;
    SharedPreferences preferences;
    EquipmentImpl equipmentDao;
    FriendInforImpl friendInforDao;
    UserEntryImpl userEntryDao;

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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        String id = preferences.getString("userId","");
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
    @OnClick({R.id.iv_set_fh,R.id.rl_set_password,R.id.rl_set_mess,R.id.rl_set_user,R.id.bt_set_exsit })
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
                    iv_set_open.setImageResource(R.mipmap.equ_close);
                    isOpen=false;
                }else {
                    iv_set_open.setImageResource(R.mipmap.equ_open);
                    isOpen=true;
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

        }
    }
}
