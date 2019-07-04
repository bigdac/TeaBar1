package teabar.ph.com.teabar.activity.device;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.EqumentFragment2;
import teabar.ph.com.teabar.fragment.MainFragment3;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class EqupmentLightActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.iv_equ_fh)
    ImageView iv_equ_fh;
    @BindView(R.id.tv_light_mode)
    TextView tv_light_mode;
    Equpment equpment;
    EquipmentImpl equipmentDao;
    public static boolean isRunning = false;
    MessageReceiver receiver;
    private boolean MQBound;
    /*
    *   0: 呼吸
        1：随机
        2：常在
        3：恢复默认
        4：忽略
    * */
    @Override
    public void initParms(Bundle parms) {
        equpment = (Equpment) parms.getSerializable("equpment");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_equipmentlight;
    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 360, true);//如果有自定义需求就用这个方法
        return super.getResources();

    }
    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        isRunning = true;
        equipmentDao = new EquipmentImpl(getApplicationContext());
        refrashMode();
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("EqupmentLightActivity");
        receiver = new  MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }
    public void refrashMode(){
        if (equpment!=null){
            String mode = "";
            switch (equpment.getMode()){
                case 6:
                    mode= getText(R.string.equ_mode_1).toString() ;
                    break;
                case 5:
                    mode= getText(R.string.equ_mode_2).toString() ;
                    break;
                case 4:
                    mode= getText(R.string.equ_mode_3).toString() ;
                    break;
                default:

                    break;
            }
            tv_light_mode.setText(mode);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (MQservice!=null)
            unbindService(MQconnection);
        if (receiver!=null)
            unregisterReceiver(receiver);

        isRunning= false;
    }

    Equpment msg1;
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            msg1 = (Equpment) intent.getSerializableExtra("msg1");
            equpment = msg1;
            refrashMode();
        }
    }

    /**
     * 自定义对话框清洗設備
     */
    Dialog dialog;
    private void customDialogqs( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(getText(R.string.equ_xq_mr1).toString());
        et_dia_name.setText(getText(R.string.equ_xq_mrxq).toString());
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
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
                if (equpment.getMStage()!=0xb6&&equpment.getMStage()!=0xb7){
                    MQservice.sendBack(equpment.getMacAdress());
                    toast(getText(R.string.toast_equ_cz).toString());
                    dialog.dismiss();
                }else {
                    ToastUtil.showShort(EqupmentLightActivity.this, getText(R.string.toast_updata_no).toString());
                }


            }
        });
        dialog.show();

    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_equ_fh ,R.id.rl_light_1,R.id.rl_light_2,R.id.rl_light_3})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;

            case R.id.rl_light_1:
                Intent intent = new Intent(this,ChooseColorActvity.class);
                intent.putExtra("equpment",equpment);
                startActivityForResult(intent,3100);
                break;

            case R.id.rl_light_2:
                popupmenuWindow();
                break;

            case R.id.rl_light_3:
                customDialogqs();

            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==3100){
            equpment = equipmentDao.findDeviceByMacAddress2(equpment.getMacAdress());
        }

    }

    int modeChoose = 0;
    private PopupWindow popupWindow1;
    public void popupmenuWindow() {
        if (popupWindow1 != null && popupWindow1.isShowing()) {
            return;
        }

        View view = View.inflate(this, R.layout.popview_mode, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        RelativeLayout rl_mode = (RelativeLayout) view.findViewById(R.id.rl_mode);
        RelativeLayout rl_mode_1 = (RelativeLayout) view.findViewById(R.id.rl_mode_1);
        RelativeLayout rl_mode_2 = (RelativeLayout) view.findViewById(R.id.rl_mode_2);
        RelativeLayout rl_mode_3 = (RelativeLayout) view.findViewById(R.id.rl_mode_3);
        TextView tv_del = (TextView) view.findViewById(R.id.tv_del);
        TextView tv_esure = (TextView) view.findViewById(R.id.tv_esure);
        final ImageView iv_mode_1 = (ImageView) view.findViewById(R.id.iv_mode_1);
        final ImageView iv_mode_2 = (ImageView) view.findViewById(R.id.iv_mode_2);
        final ImageView iv_mode_3 = (ImageView) view.findViewById(R.id.iv_mode_3);
        if (equpment!=null){

            switch (equpment.getMode()){
                case 6:
                    iv_mode_3.setVisibility(View.INVISIBLE);
                    iv_mode_1.setVisibility(View.VISIBLE);
                    iv_mode_2.setVisibility(View.INVISIBLE);
                    modeChoose = 64;
                    break;
                case 5:
                    iv_mode_3.setVisibility(View.INVISIBLE);
                    iv_mode_1.setVisibility(View.INVISIBLE);
                    iv_mode_2.setVisibility(View.VISIBLE);
                    modeChoose =32;
                    break;
                case 4:
                    iv_mode_3.setVisibility(View.VISIBLE);
                    iv_mode_1.setVisibility(View.INVISIBLE);
                    iv_mode_2.setVisibility(View.INVISIBLE);
                    modeChoose =16;
                    break;
                default:

                    break;
            }

        }
        if (popupWindow1==null)
            popupWindow1 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow1.setFocusable(true);
        popupWindow1.setOutsideTouchable(true);
        //添加弹出、弹入的动画
        popupWindow1.setAnimationStyle(R.style.Popupwindow);
        popupWindow1.showAtLocation(view, Gravity.BOTTOM, 0,0);
//        WindowManager.LayoutParams lp=getWindow().getAttributes();
//            lp.alpha=0.3f;
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getWindow().setAttributes(lp);
        backgroundAlpha(0.5f);
        //添加按键事件监听

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_del:
                        popupWindow1.dismiss();

                        break;
                    case R.id.tv_esure:
                        if (equpment!=null){
                            if (equpment.getMStage()!=0xb6&&equpment.getMStage()!=0xb7){
                                equpment.setMode(modeChoose);
                                equipmentDao.update(equpment);
                                MQservice.sendLightColor(equpment.getMacAdress(),1,0,0,0,modeChoose);
                            }else {
                                ToastUtil.showShort(EqupmentLightActivity.this, getText(R.string.toast_updata_no).toString());
                            }

                        }

                        popupWindow1.dismiss();
                        break;

                    case R.id.rl_mode_1:
                        iv_mode_1.setVisibility(View.VISIBLE);
                        iv_mode_2.setVisibility(View.INVISIBLE);
                        iv_mode_3.setVisibility(View.INVISIBLE);
                        modeChoose = 64;
                        tv_light_mode.setText(getText(R.string.equ_mode_1).toString());
                        break;
                    case R.id.rl_mode_2:
                        iv_mode_1.setVisibility(View.INVISIBLE);
                        iv_mode_2.setVisibility(View.VISIBLE);
                        iv_mode_3.setVisibility(View.INVISIBLE);
                        modeChoose =32;
                        tv_light_mode.setText(getText(R.string.equ_mode_2).toString());

                        break;
                    case R.id.rl_mode_3:
                        iv_mode_1.setVisibility(View.INVISIBLE);
                        iv_mode_2.setVisibility(View.INVISIBLE);
                        iv_mode_3.setVisibility(View.VISIBLE);
                        modeChoose =16;
                        tv_light_mode.setText(getText(R.string.equ_mode_3).toString());
                        break;
                }
            }
        };

        rl_mode_1.setOnClickListener(listener);
        rl_mode_2.setOnClickListener(listener);
        rl_mode_3.setOnClickListener(listener);
        tv_del.setOnClickListener(listener);
        tv_esure.setOnClickListener(listener);
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });

    }
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
}
