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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.internal.CustomAdapt;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.MakeMethod;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.MySeekBar;
import teabar.ph.com.teabar.view.MyView;
import teabar.ph.com.teabar.view.MyView1;
import teabar.ph.com.teabar.view.WaveProgress;


public class AddMethodActivity1 extends BaseActivity implements SeekBar.OnSeekBarChangeListener,CustomAdapt {


    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.arcprogressBar)
    MyView arcProgressbar;
    @BindView(R.id.arcprogressBar1)
    MyView1 arcProgressbar1;
    @BindView(R.id.tv_add_temp)
    TextView tv_add_temp;
    @BindView(R.id.tv_add_time)
    TextView tv_add_time;
    @BindView(R.id.beautySeekBar4)
    MySeekBar beautySeekBar;
    @BindView(R.id.tv_power)
    TextView tv_power;
    @BindView(R.id.li_main_title)
    RelativeLayout li_main_title;
    @BindView(R.id.iv_main_online)
    ImageView iv_main_online;
    @BindView(R.id.iv_main_error)
    ImageView iv_main_error;
    @BindView(R.id.tv_main_error)
    TextView tv_main_error;
    @BindView(R.id.tv_main_hot)
    TextView tv_main_hot;
    @BindView(R.id.tv_main_online)
    TextView tv_main_online;
    MyApplication application;
    List<String> mList = new ArrayList<>();
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    String firstMac;
    QMUITipDialog tipDialog;
    private boolean MQBound;
    MakeMethod makeMethod;
    SharedPreferences preferences;
    Equpment Firstequpment;
    public static boolean isRunning = false;
    int type =-1;
    String userId;
    Tea tea;
    long id = 0;
    MessageReceiver receiver;
    CallbackManager callbackManager;
    @Override
    public void initParms(Bundle parms) {
        type = parms.getInt("type");
        tea = (Tea) parms.getSerializable("tea");
        if (type==1){
            /*已有冲泡列表进入*/
            makeMethod = (MakeMethod) parms.getSerializable("method");
        }else {
            /*新添加的冲泡方法*/
            makeMethod = new MakeMethod();
            makeMethod.setName(getText(R.string.drink_makemethod).toString());
            makeMethod.setTime(tea.getSeconds());
            makeMethod.setTemp(tea.getTemperature());
            makeMethod.setCapacity(tea.getWaterYield());
        }
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_addmethod1;
    }
//    @Override
//    public Resources getResources() {
//        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
////        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
//        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
//        return super.getResources();
//
//    }
    @Override
    public void initView(View view) {


        if (    application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","")+"";
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equpments= equipmentDao.findAll();
        if (tea!=null){
            id= tea.getTeaId();
        }
        if (equpments.size()==0){

        }else {
            for (int i = 0;i<equpments.size();i++){
                if (equpments.get(i).getIsFirst()){
                    firstMac = equpments.get(i).getMacAdress();
                    Firstequpment = equpments.get(i);
                }

            }
        }
        if (makeMethod!=null){
            tv_power.setText(makeMethod.getName());
            arcProgressbar.setCurProgress(makeMethod.getTemp());
            arcProgressbar1.setCurProgress(makeMethod.getTime()-5);
            beautySeekBar.setProgress(makeMethod.getCapacity());
            tv_add_temp.setText(makeMethod.getTemp()+"℃");
            tv_add_time.setText(makeMethod.getTime()+"S");
        }else {
            arcProgressbar.setCurProgress(75);
            tv_add_temp.setText("75℃");
            arcProgressbar1.setCurProgress(30);
            tv_add_time.setText("30S");
            beautySeekBar.setProgress(300);
        }

        arcProgressbar.setOnProgressListener(new MyView.OnProgressListener() {
            @Override
            public void onScrollingListener(Integer progress) {
                Log.e(TAG, "onScrollingListener: -->"+progress );
                tv_add_temp.setText(progress+"℃");
            }
        });
        arcProgressbar1.setOnProgressListener(new MyView1.OnProgressListener() {
            @Override
            public void onScrollingListener(Integer progress) {
                Log.e(TAG, "onScrollingListener: -->"+progress );
                tv_add_time.setText(progress+5+"S");
            }
        });

        beautySeekBar.setOnSeekBarChangeListener(this);
        //绑定services
        IntentFilter intentFilter = new IntentFilter("AddMethodActivity1");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        shareDialog = new ShareDialog(this);
        // this part is optional
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//
//
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                toast("分享成功");
//            }
//
//            @Override
//            public void onCancel() {
//                toast("取消分享");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                toast("分享失败");
//            }
//        });

        RefrashFirstEqu1();
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
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }

    @OnClick({ R.id.iv_power_fh ,R.id.btn_make,R.id.tv_add_save,R.id.tv_method_change,R.id.li_main_title,R.id.btn_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_back:
                if (makeMethod!=null){
                    arcProgressbar.setCurProgress(makeMethod.getTemp());
                    arcProgressbar1.setCurProgress(makeMethod.getTime()-5);
                    beautySeekBar.setProgress(makeMethod.getCapacity());
                    tv_add_temp.setText(makeMethod.getTemp()+"℃");
                    tv_add_time.setText(makeMethod.getTime()+"S");
                }else {
                    arcProgressbar.setCurProgress(75);
                    tv_add_temp.setText("75℃");
                    arcProgressbar1.setCurProgress(30);
                    tv_add_time.setText("30S");
                    beautySeekBar.setProgress(300);
                }
                break;
            case R.id.iv_power_fh:
                finish();
                break;
            case R.id.btn_make:
                /*制作*/
//                if (Firstequpment!=null&&Firstequpment.getMStage()==0xb1){
//                    customDialog();
//                    Map<String,Object> params = new HashMap<>();
//                    params.put("teaName",tea.getTeaNameEn());
//                    params.put("userId",userId);
//                    params.put("teaId",tea.getTeaId());
//                    params.put("deviceId",Firstequpment.getEqupmentId());
//                    new AddTeaAsyncTask().execute(params);
                    Intent intent = new Intent(this,ChooseDeviceActivity.class);
                    StringBuffer stringBuffer3  = new StringBuffer(tv_add_temp.getText().toString());
                    stringBuffer3.deleteCharAt(stringBuffer3.length()-1);
                    String temp1= stringBuffer3.toString();
                    StringBuffer stringBuffer4  = new StringBuffer(tv_add_time.getText().toString());
                    stringBuffer4.deleteCharAt(stringBuffer4.length()-1);
                    String time1= stringBuffer4.toString();
                    intent.putExtra("temp", Integer.valueOf(temp1));
                    intent.putExtra("time",Integer.valueOf(time1));
                    intent.putExtra("water",beautySeekBar.getProgress());
                    intent.putExtra("teaId",tea.getId());
                    startActivity(intent);

//                }else {
//                    toast(getResources().getText(R.string.toast_make_make).toString());
//                }

                break;
            case R.id.tv_add_save:
                /*保存*/
                StringBuffer stringBuffer  = new StringBuffer(tv_add_temp.getText().toString());
                stringBuffer.deleteCharAt(stringBuffer.length()-1);
                String temp= stringBuffer.toString();
                StringBuffer stringBuffer1  = new StringBuffer(tv_add_time.getText().toString());
                stringBuffer1.deleteCharAt(stringBuffer1.length()-1);
                String time= stringBuffer1.toString();
                if (type==0){

                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId);
                params.put("brewName",tv_power.getText().toString());
                params.put("temperature",temp);
                params.put("waterYield",beautySeekBar.getProgress());
                params.put("seconds",time);
                new AddMethordAsynTask(this).execute(params);
                }else if (type==1){
                    Map<String,Object> params = new HashMap<>();
                    params.put("id",makeMethod.getId());
                    params.put("userId",userId);
                    params.put("brewName",tv_power.getText().toString());
                    params.put("temperature",temp);
                    params.put("waterYield",beautySeekBar.getProgress());
                    params.put("seconds",time);
                    new UpdataMethordAsynTask(this).execute(params);
                 }
                break;
            case R.id.tv_method_change:
                customDialog1();
                break;
            case R.id.li_main_title:
                if ( Firstequpment!=null) {
                    if (Firstequpment.getOnLine()) {
                        if (!Utils.isFastClick()) {

                            if (Firstequpment.getMStage()!=0xb6&&Firstequpment.getMStage()!=0xb7) {
                                if (Firstequpment.getMStage() != 0xb2) {
                                    li_main_title.setBackgroundColor( getResources().getColor(R.color.main_title1));
                                    MQservice.sendOpenEqu(0Xc0, firstMac);
                                    Firstequpment.setMStage(0xb2);
                                } else {
                                    li_main_title.setBackgroundResource(R.drawable.main_title);
                                    MQservice.sendOpenEqu(0Xc1, firstMac);
                                    Firstequpment.setMStage(0xb0);
                                }
                            }else {
                                ToastUtil.showShort(this, getText(R.string.toast_updata_no).toString());
                            }
                        } else {
                            ToastUtil.showShort(this, getText(R.string.toast_equ_fast).toString());
                        }
                    }else {
                        ToastUtil.showShort(this, getText(R.string.toast_equ_online).toString());
                    }

                }else {
                    ToastUtil.showShort(this, getText(R.string.toast_equ_add).toString());

                }

        }
    }

    /**
     * 自定义对话框修改名称
     */
    Dialog dialog;

    String equName="";

    private void customDialog1( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_rename, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        final EditText et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(getText(R.string.dialog_method_name).toString());
        et_dia_name.setHint(getText(R.string.dialog_method_namexq).toString());
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
                    tv_power.setText(et_dia_name.getText());
                    dialog.dismiss();
                }else {
                    toast(getText(R.string.toast_add_equname).toString());
                }

            }
        });
        dialog.show();

    }


    String returnMsg1,returnMsg2;

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 667;
    }

    class UpdataMethordAsynTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity  > {

        public UpdataMethordAsynTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/updateBrew",prarms);

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
        protected void onPostExecute(BaseActivity baseActivity, String s) {

            switch (s) {

                case "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    setResult(2000);
                    finish();
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;
                case "4000":
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
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }
                    break;

            }
        }
    }


    class AddMethordAsynTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity > {

        public AddMethordAsynTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/addBrew",prarms);

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
        protected void onPostExecute(BaseActivity baseActivity, String s) {
            switch (s) {

                case "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    setResult(2000);
                    finish();
                    if (application.IsEnglish()==0){
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }

                    break;
                case "4000":
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
                        toast( returnMsg1);
                    }else {
                        toast( returnMsg2);
                    }
                    break;

            }
        }
    }


    /**
     *  添加到喝茶記錄
     *
     */

    class AddTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/addTeaDrink",prarms);
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
            switch (s) {
                case "200":

                    break;

                case "4000":
//                    toast(  "连接超时，请重试");
                    break;
                default:
//                   toast( returnMsg1);
                    break;

            }
        }
    }

        /*廣播*/
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            int nowStage = intent.getIntExtra("nowStage",-1);
            int height = intent.getIntExtra("height",0);
            int low = intent.getIntExtra("low",0);
            int size = height*256 +low;
            if (IsMakeing==1){
                if (waterView!=null){
                    float value = 100* size/beautySeekBar.getProgress();
                    if ((int) value!=0){
                        waterView.setValue(value);
                        tv_number.setText( (int) value+"");
                    }
                    if (nowStage ==0xb5){
                        searchThread.stopThread();
                        waterView.setValue(110f );
                        tv_make_title.setText(R.string.equ_xq_cpwc);
                        tv_number.setText(100+"");
                        li_make_finish.setVisibility(View.VISIBLE);
                        bt_view_stop.setVisibility(View.GONE);

                    }
                }
            }
            Equpment msg1 =(Equpment)intent.getSerializableExtra("msg1");
            if (msg1!=null){
                Firstequpment = msg1;
                RefrashFirstEqu1();
            }

        }
    }

    String errorMes;
    /*需要更改3個故障*/
    public void  RefrashFirstEqu1(){
        tv_main_error.setVisibility(View.GONE);
        iv_main_error.setVisibility(View.GONE);
        tv_main_hot.setVisibility(View.GONE);
        if ( Firstequpment!=null) {

                String error = Firstequpment.getErrorCode();
                boolean hasError =false;
                errorMes="";
                if (!TextUtils.isEmpty(error)) {
                    String[] aa = error.split(",");
                    for (int i=0;i<aa.length;i++){
                        if ("1".equals(aa[i])){
                            hasError=true;
                        }
                    }
                    if (hasError) {
                        tv_main_error.setVisibility(View.VISIBLE);
                        iv_main_error.setVisibility(View.VISIBLE);
                        if ("1".equals(aa[6])) {
                            /*水位过低*/
                            if (TextUtils.isEmpty(errorMes)) {
                                errorMes = getText(R.string.main_home_error1).toString();
                            } else {
                                errorMes = errorMes + "," + getText(R.string.main_home_error1).toString();
                            }
                        }
                        if ("1".equals(aa[2])) {
                            /*垃圾盒*/
                            if (TextUtils.isEmpty(errorMes)) {
                                errorMes = getText(R.string.main_home_error2).toString();
                            } else {
                                errorMes = errorMes + "," + getText(R.string.main_home_error2).toString();
                            }
                        }
                        if ("1".equals(aa[3])) {
                            /*清洗周期*/
                            if (TextUtils.isEmpty(errorMes)) {
                                errorMes = getText(R.string.main_home_error3).toString();
                            } else {
                                errorMes = errorMes + "," + getText(R.string.main_home_error3).toString();
                            }
                        }
                        tv_main_error.setText(errorMes);
                    }
                }
                if (Firstequpment.getOnLine()) {
                    tv_main_online.setText(R.string.equ_xq_online);
                    iv_main_online.setImageResource(R.mipmap.main_online3);
                    tv_main_hot.setVisibility(View.VISIBLE );
                }
                else {
                    iv_main_online.setImageResource(R.mipmap.main_outline3);
                    tv_main_online.setText(R.string.equ_xq_outline);
                }
                if (Firstequpment.getMStage() == 0xb0) {
                    if (Firstequpment.getHotFinish()==0)
                        tv_main_hot.setText(R.string.equ_xq_nohot);
                    else
                        tv_main_hot.setText(R.string.equ_xq_ishot);
                }else if (Firstequpment.getMStage() == 0xb2) {
                    tv_main_hot.setText(R.string.equ_xq_dg);
                }else if (Firstequpment.getMStage() == 0xb3) {
                    tv_main_hot.setText(R.string.equ_xq_jpz);
                }else if (Firstequpment.getMStage() == 0xb4) {
                    tv_main_hot.setText(R.string.equ_xq_cpz);
                }else if (Firstequpment.getMStage() == 0xb5) {
                    tv_main_hot.setText(R.string.equ_xq_cpwc);
                }else if (Firstequpment.getMStage() == 0xc6) {
                    tv_main_hot.setText(R.string.equ_xq_qx);
                }
                else if (Firstequpment.getMStage() == 0xb6||Firstequpment.getMStage() == 0xb7) {
                    tv_main_hot.setText(R.string.equ_xq_sj);
                }
                else if (Firstequpment.getMStage() == 0xc7) {
                    tv_main_hot.setText(R.string.equ_xq_jb);
                }
                if (Firstequpment.getMStage() == 0xb2 || Firstequpment.getMStage() == -1) {
                    li_main_title.setBackgroundColor( getResources().getColor(R.color.main_title1));

                } else {
                    li_main_title.setBackgroundResource(R.drawable.main_title);

                }


        }else {
            li_main_title.setBackgroundColor( getResources().getColor(R.color.main_title1));
            iv_main_online.setImageResource(R.mipmap.main_outline3);
            Firstequpment = null;
            firstMac = "";

        }

    }







    WaveProgress waterView;
    int choose;
    CountDownTimer countDownTimer;
    Dialog dialog1;
    int IsMakeing = 0;
    TextView tv_make_title;
    TextView tv_number;
    LinearLayout li_make_finish;
    Button bt_view_stop,bt_view_sc,bt_view_fx;

    ShareDialog shareDialog;
    boolean which;
    private void customDialog( ) {
        IsMakeing =0;
        dialog1  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.view_make, null);
        bt_view_stop = view.findViewById(R.id.bt_view_stop);
        tv_number = view.findViewById(R.id.tv_number);
        final TextView tv_units = view.findViewById(R.id.tv_units);
        tv_make_title = view.findViewById(R.id.tv_make_title);
        li_make_finish = view.findViewById(R.id.li_make_finish);
        bt_view_sc = view.findViewById(R.id.bt_view_sc);
        bt_view_fx = view.findViewById(R.id.bt_view_fx);
        TextView tv_dialog_bj = view.findViewById(R.id.tv_dialog_bj);
        ImageView iv_dialog_move = view.findViewById(R.id.iv_dialog_move);
        ImageView bt_dialog_bj = view.findViewById(R.id.bt_dialog_bj);
        tv_dialog_bj.setVisibility(View.INVISIBLE);
        bt_dialog_bj.setVisibility(View.INVISIBLE);
        waterView = (WaveProgress) view.findViewById(R.id.waterView);
        waterView.setWaveColor(Color.parseColor("#37dbc2"), Color.parseColor("#81fbe6"));
        waterView.setMaxValue(100f);
        waterView.setVisibility(View.INVISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            iv_dialog_move.startAnimation(operatingAnim);
        }  else {
            iv_dialog_move.setAnimation(operatingAnim);
            iv_dialog_move.startAnimation(operatingAnim);
        }

        tv_make_title.setText(R.string.equ_xq_jpz);

//        waterView.setValue(100f );
//        MQservice.sendMakeMess(tea.getWaterYield(),tea.getSeconds(),tea.getTemperature(),firstMac);
//        waterView.setValue(50f);
        long time =tea.getSeconds();
        countDownTimer = new CountDownTimer(time*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_number.setText(millisUntilFinished/1000+"");
                Log.e("DDDDDD", "onTick: -->"+ (millisUntilFinished /1000));

            }

            @Override
            public void onFinish() {
                waterView.setVisibility(View.VISIBLE);
                tv_dialog_bj.setVisibility(View.VISIBLE);
                bt_dialog_bj.setVisibility(View.VISIBLE);
                iv_dialog_move.clearAnimation();
                iv_dialog_move.setVisibility(View.INVISIBLE);
                tv_number.setTextColor(getResources().getColor(R.color.white));
                waterView.setValue(-10f );
                tv_units.setText("％");
                tv_make_title.setText(R.string.equ_xq_cpz);
                IsMakeing =1;
                if (searchThread==null){
                    searchThread = new SearchThread();
                    searchThread.start();
                }else {
                    searchThread.starThread();
                }

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
        bt_view_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MQservice.sendStop(firstMac);
                if (searchThread!=null&&Running){
                    searchThread.stopThread();
                }
                dialog1.dismiss();
                if (countDownTimer!=null){
                    countDownTimer.cancel();
                }
            }
        });
        bt_view_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    if (dialog1!=null&&dialog1.isShowing()){
                        dialog1.dismiss();
                    }
                }
            }
        });
        bt_view_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = true;
                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId );
                params.put("teaId",id );
                new CollectTeaAsyncTask().execute(params);
            }
        });
        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (searchThread!=null){
                    searchThread.stopThread();
                }

            }
        });
        dialog1.show();

    }
    SearchThread searchThread;
    boolean  Running = true;
    class SearchThread extends  Thread{
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    if (Running) {
                        sleep(5000);
                        Log.e(TAG, "run: -->+++++++++++");
                        MQservice.sendSearchML(Firstequpment.getMacAdress());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        public void stopThread(){
            Running=false;
        }
        public void starThread(){
            Running=true;
        }
    }

    /**
     *  添加喜愛
     *
     */

    class CollectTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
            if (which){
                ip ="/app/collectTea";
            }else {
                ip ="/app/cancelCollectTea";
            }
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+ip,prarms);
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
            switch (s) {
                case "200":
                    toast(returnMsg2);
                    break;

                case "4000":
                    toast(  getText(R.string.toast_all_cs).toString());
                    break;
                default:

                    break;

            }
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int seek = beautySeekBar.getProgress();
        if (100<seek&&seek<=150){
            beautySeekBar.setProgress(150);
        }else if (150<seek&&seek<=200){
            beautySeekBar.setProgress(200);
        }else if (200<seek&&seek<=250){
            beautySeekBar.setProgress(250);
        }  else if (250<seek&&seek<=300){
            beautySeekBar.setProgress(300);
        }else if (300<seek&&seek<=350){
            beautySeekBar.setProgress(350);
        }else if ( seek<=100){
            beautySeekBar.setProgress(100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MQBound) {
            unbindService(MQconnection);
        }
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        if (receiver!=null)
            unregisterReceiver(receiver);
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning= true;
    }
}
