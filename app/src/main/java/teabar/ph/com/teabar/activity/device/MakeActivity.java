package teabar.ph.com.teabar.activity.device;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MethodActivity;
import teabar.ph.com.teabar.adpter.MethodAdapter;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.DoubleWaveView;
import teabar.ph.com.teabar.view.WaveProgress;

public class MakeActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    private boolean MQBound;
    MyApplication application;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    String firstMac;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_make;
    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        return super.getResources();

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
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equpments= equipmentDao.findAll();
        if (equpments.size()==0){

        }else {
            for (int i = 0;i<equpments.size();i++){
                if (equpments.get(i).getIsFirst())
                    firstMac = equpments.get(i).getMacAdress();
            }
        }
        //绑定services
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
    }

    Intent MQintent;
    MQService MQservice;
    boolean boundservice;
    ServiceConnection MQconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            MQservice = binder.getService();
            boundservice = true;
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    WaveProgress waterView;
    Dialog dialog;
    int choose;
  CountDownTimer countDownTimer;
    private void customDialog( ) {
        choose=0;
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.view_make, null);
        final Button bt_view_stop = view.findViewById(R.id.bt_view_stop);
        final TextView tv_number = view.findViewById(R.id.tv_number);
        final TextView tv_units = view.findViewById(R.id.tv_units);
        final TextView tv_make_title = view.findViewById(R.id.tv_make_title);
        final LinearLayout li_make_finish = view.findViewById(R.id.li_make_finish);
        tv_make_title.setText("浸泡中");
        waterView = (WaveProgress) view.findViewById(R.id.waterView);
        waterView.setWaveColor(Color.parseColor("#37dbc2"), Color.parseColor("#81fbe6"));
        waterView.setMaxValue(100f);
        waterView.setValue(100f );
        MQservice.sendMakeMess(300,30,80,firstMac);
         countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (choose==0){
                    waterView.setValue(100*millisUntilFinished /30000 );
                    tv_number.setText(millisUntilFinished/1000+"");
                    Log.e("DDDDDD", "onTick: -->"+ (millisUntilFinished /1000));

                }else {
                    waterView.setValue(100f-100*millisUntilFinished /30000 );
                    tv_number.setText((int)(100f-100*millisUntilFinished /30000)+"");
                    Log.e("DDDDDD", "onTick: -->"+ (int)(100f-100*millisUntilFinished /30000));
                }

            }

            @Override
            public void onFinish() {
                if (choose==0){
                    waterView.setValue(-10f );
                    tv_units.setText("％");
                    countDownTimer.start();
                    tv_make_title.setText("冲泡中");
                    choose=1;
                }else {
                    waterView.setValue(110f );
                    tv_make_title.setText("冲泡完成");
                    waterView.setValue(100f);
                    tv_number.setText(100+"");
                    li_make_finish.setVisibility(View.VISIBLE);
                    bt_view_stop.setVisibility(View.GONE);
                    dialog.setCanceledOnTouchOutside(true);
                }

            }
        } ;
        countDownTimer.start();
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
        bt_view_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MQservice.sendStop(firstMac);
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @OnClick({R.id.iv_equ_fh,R.id.iv_make_choose,R.id.bt_make_make})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;

            case R.id.iv_make_choose:
                startActivity(MethodActivity.class);
                break;

            case R.id.bt_make_make:
                customDialog();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MQBound) {
             unbindService(MQconnection);
        }
    }
}
