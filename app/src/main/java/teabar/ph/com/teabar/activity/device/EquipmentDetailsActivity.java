package teabar.ph.com.teabar.activity.device;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EqupmentXqAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
//設備詳情頁面
public class EquipmentDetailsActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.rv_equmentxq)
    RecyclerView rv_equmentxq;
    @BindView(R.id.iv_equxq_xz)
    ImageView iv_equxq_xz;
    List<String> stringList;
    EqupmentXqAdapter equpmentXqAdapter;
    EquipmentImpl equipmentDao;
    Equpment equpment;
    String userId;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    int resultCode = 0;
    List<Equpment> listEqu = new ArrayList<>();
    Equpment FirstEqu;
    public static boolean isRunning = false;
    MessageReceiver receiver;
    @Override
    public void initParms(Bundle parms) {
        equpment = (Equpment) parms.getSerializable("equment");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_equipmentdetails;
    }

    @Override
    public void initView(View view) {
        isRunning=true;
        resultCode=0;
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","");
        stringList = new ArrayList<>();
        equipmentDao = new EquipmentImpl(getApplicationContext());
        listEqu= equipmentDao.findAll();
        for (int j=0;j<listEqu.size();j++){
            if (listEqu.get(j).getIsFirst()){
                FirstEqu = listEqu.get(j);
            }
        }
        if (equpment.getIsFirst()){
            iv_equxq_xz.setImageResource(R.mipmap.equ_choose);
        }
        for (int i = 0;i<7;i++){
            stringList.add(i+"");
        }
        equpmentXqAdapter = new EqupmentXqAdapter(this,stringList,equpment);
        rv_equmentxq.setLayoutManager(new LinearLayoutManager(this));
        rv_equmentxq.setAdapter(equpmentXqAdapter);
        equpmentXqAdapter.SetOnItemClick(new EqupmentXqAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0:
                        customDialog();//修改設備名稱
                        break;
                    case 1://設備通知頁面
                        Intent intent2  = new Intent(EquipmentDetailsActivity.this,EqupmentInformActivity.class);
                        intent2.putExtra("mac",equpment.getMacAdress());
                        startActivity(intent2);
                        break;
                    case 3://設備燈光頁面
                        Intent intent =new Intent(EquipmentDetailsActivity.this,EqupmentLightActivity.class);
                        intent.putExtra("equpment",equpment);
                        startActivity(intent);
                        resultCode=2000;
                        break;
                    case 4://清洗設備
                        customDialogqs();
                        break;
                    case 5://設置設備清洗週期
                        Intent intent1 =new Intent(EquipmentDetailsActivity.this,EqupmentWashActivity.class);
                        intent1.putExtra("equpment",equpment);
                        startActivity(intent1);
                        break;
                    case 6:
                        customDialog1();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("EquipmentDetailsActivity");
        receiver = new  MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            int mStage = intent.getIntExtra("mStage",-1);
            Equpment  msg1 = (Equpment) intent.getSerializableExtra("msg1");
            if (msg.equals(equpment.getMacAdress())){
                equpment = msg1;
            }
            if (mStage==0xb9){
                if (dialog1!=null&&dialog1.isShowing()){
                    dialog1.dismiss();
                }
            }

        }
    }
    Intent MQintent;
    private boolean MQBound;
    MQService MQservice;
    boolean boundservice;
    ServiceConnection MQconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            MQservice = binder.getService();
            boundservice = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_equ_fh,R.id.li_equ_choose})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_equ_fh:
                setResult(resultCode);
                finish();
                break;

            case R.id.li_equ_choose:
                if (equpment.getIsFirst()){
                   toast(getText(R.string.toast_equ_mr).toString());
                }else
                customDialog2();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
        if (MQBound) {
            unbindService(MQconnection);
        }
        if (receiver!=null)
            unregisterReceiver(receiver);
    }

    /**
     * 自定义对话框修改名称
     */
    Dialog dialog;
    int position ;
    String equName="";
    int Flag=-1; //g更新  0 更新名字 1 更新默认设备
    private void customDialog( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_rename, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        final EditText et_dia_name = view.findViewById(R.id.et_dia_name);
        et_dia_name.setText(equpment.getName());
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
                if (!TextUtils.isEmpty(et_dia_name.getText().toString().trim())){
                    showProgressDialog();
                    Map<String,Object> params = new HashMap<>();
                    params.put("id",equpment.getEqupmentId());
                    params.put("deviceName",et_dia_name.getText().toString().trim());
                    params.put("userId",userId);
                    equName = et_dia_name.getText().toString().trim();
                    Flag=0;
                    new UpdateDeviceAsyncTask(EquipmentDetailsActivity.this).execute(params);
                    resultCode=2000;
                    dialog.dismiss();
                }else {
                    toast(getText(R.string.toast_add_equname).toString());
                }

            }
        });
        dialog.show();

    }
    /**
     * 自定义对话框删除设备
     */
    private void customDialog1( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
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
                showProgressDialog();
                Map<String,Object> params = new HashMap<>();
                params.put("id",equpment.getEqupmentId());
                params.put("userId",userId);
                new DeleteDeviceAsyncTask(EquipmentDetailsActivity.this).execute(params);
                resultCode=2000;
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    /**
     * 自定义对话框清洗設備
     */
    private void customDialogqs( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(getText(R.string.equ_xq_clean).toString());
        et_dia_name.setText(getText(R.string.equ_xq_cleanxq).toString());
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
                MQservice.sendCleanEqu(equpment.getMacAdress(),equpment.getMStage());
                dialog.dismiss();
                customDialogwash();

            }
        });
        dialog.show();

    }


    Dialog dialog1;
    CountDownTimer countDownTimer;

    TextView tv_make_title;
    TextView tv_number;
    LinearLayout li_make_finish;

    /*清洗设备*/
    private void customDialogwash( ) {

        dialog1  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.view_wash, null);
        tv_number = view.findViewById(R.id.tv_number);
        tv_make_title = view.findViewById(R.id.tv_make_title);
        li_make_finish = view.findViewById(R.id.li_make_finish);
        ImageView iv_dialog_move = view.findViewById(R.id.iv_dialog_move);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            iv_dialog_move.startAnimation(operatingAnim);
        }  else {
            iv_dialog_move.setAnimation(operatingAnim);
            iv_dialog_move.startAnimation(operatingAnim);
        }
        tv_make_title.setText(R.string.equ_xq_qx);


        countDownTimer = new CountDownTimer(50*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_number.setText(millisUntilFinished/1000+"");
                Log.e("DDDDDD", "onTick: -->"+ (millisUntilFinished /1000));

            }

            @Override
            public void onFinish() {

                iv_dialog_move.clearAnimation();
                iv_dialog_move.setVisibility(View.INVISIBLE);
                toast(getText(R.string.equ_xq_qxwc).toString());
                if (dialog1!=null&&dialog1.isShowing())
                dialog1.dismiss();


            }
        } ;
        countDownTimer.start();
        dialog1.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog1.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog1!=null){
                    dialog1.dismiss();
                    if (countDownTimer!=null){
                        countDownTimer.cancel();
                    }
                }
            }
        });
        dialog1.show();

    }

    /**
     * 自定义对话框修改为默认设备
     */
    private void customDialog2( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(getText(R.string.equ_xq_mrequ).toString());
        et_dia_name.setText(getText(R.string.equ_xq_mrequ1).toString());
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
                showProgressDialog();
                Map<String,Object> params = new HashMap<>();
                params.put("id",equpment.getEqupmentId());
                params.put("flag",1);
                params.put("userId",userId);
                new UpdateDeviceAsyncTask(EquipmentDetailsActivity.this).execute(params);
                Flag=1;
                dialog.dismiss();
                resultCode=2000;
            }
        });
        dialog.show();

    }
    /*
    *
    *更新设备，设置为默认设备
    * */
    String returnMsg1,returnMsg2;
    class UpdateDeviceAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public UpdateDeviceAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/updateDevice",params);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1 = jsonObject.getString("message2");
                        returnMsg2 = jsonObject.getString("message3");
                    } catch (JSONException e) {

                    }

                }else{
                    code="4000";
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity, String s) {

            switch (s){
                case  "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    iv_equxq_xz.setImageResource(R.mipmap.equ_choose);
                    MQservice.sendFindEqu(equpment.getMacAdress());
                    if (Flag==0){
                        if (!TextUtils.isEmpty(equName)){
                            equpment.setName(equName);
                            equipmentDao.update(equpment);
                            equName="";
                            equpmentXqAdapter.RefrashName(equpment);
                        }
                    }else {
                        equpment.setIsFirst(true);
                        if (FirstEqu!=null) {
                            FirstEqu.setIsFirst(false);
                            equipmentDao.update(FirstEqu);
                        }
                        equipmentDao.update(equpment);

            }
                    Flag=-1;
                    if (application.IsEnglish()==0) {
                        toast(returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
                    break;

                case  "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());
                    break;

                    default:
                        if (tipDialog!=null&&tipDialog.isShowing()){
                            tipDialog.dismiss();
                        }
                        if (application.IsEnglish()==0) {
                            toast(returnMsg1);
                        }else {
                            toast(returnMsg2);
                        }
                        break;
            }
        }
    }

    /*
     *
     *删除设备
     * */
    class DeleteDeviceAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {
        public DeleteDeviceAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }
        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/deleteDevice",params);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1 = jsonObject.getString("message2");
                        returnMsg2 = jsonObject.getString("message3");
                    } catch (JSONException e) {

                    }

                }else{
                    code="4000";
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity, String s) {

            switch (s){
                case  "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    equipmentDao.delete(equpment);
                    toast( returnMsg1);
                    setResult(2000);
                    finish();
                    break;

                case  "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast(getText(R.string.toast_all_cs).toString());
                    break;

                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    if (application.IsEnglish()==0){
                        toast(returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
