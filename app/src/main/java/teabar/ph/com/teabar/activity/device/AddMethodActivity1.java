package teabar.ph.com.teabar.activity.device;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.login.LoginActivity;
import teabar.ph.com.teabar.adpter.MethodAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.MakeMethod;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.DisplayUtil;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.ArcProgressBar;
import teabar.ph.com.teabar.view.MySeekBar;
import teabar.ph.com.teabar.view.MyView;
import teabar.ph.com.teabar.view.MyView1;
import teabar.ph.com.teabar.view.VerticalProgressBar1;
import teabar.ph.com.teabar.view.WaveProgress;


public class AddMethodActivity1 extends BaseActivity implements SeekBar.OnSeekBarChangeListener{

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
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
    @BindView(R.id.tv_method_change)
    TextView tv_method_change;
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
    MessageReceiver receiver;
    @Override
    public void initParms(Bundle parms) {
        type = parms.getInt("type");
        tea = (Tea) parms.getSerializable("tea");
        if (type==1){
            makeMethod = (MakeMethod) parms.getSerializable("method");
        }
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_addmethod1;
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);

        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","")+"";
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equpments= equipmentDao.findAll();
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
            tv_add_temp.setText(makeMethod.getTemp()+"");
            tv_add_time.setText(makeMethod.getTime()+"");
        }else {
            arcProgressbar.setCurProgress(75);
            tv_add_temp.setText("75");
            arcProgressbar1.setCurProgress(30);
            tv_add_time.setText("30");
            beautySeekBar.setProgress(300);
        }

        arcProgressbar.setOnProgressListener(new MyView.OnProgressListener() {
            @Override
            public void onScrollingListener(Integer progress) {
                Log.e(TAG, "onScrollingListener: -->"+progress );
                tv_add_temp.setText(progress+"");
            }
        });
        arcProgressbar1.setOnProgressListener(new MyView1.OnProgressListener() {
            @Override
            public void onScrollingListener(Integer progress) {
                Log.e(TAG, "onScrollingListener: -->"+progress );
                tv_add_time.setText(progress+5+"");
            }
        });

        beautySeekBar.setOnSeekBarChangeListener(this);
        //绑定services
        IntentFilter intentFilter = new IntentFilter("AddMethodActivity1");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
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
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }

    @OnClick({ R.id.iv_power_fh ,R.id.btn_make,R.id.tv_add_save,R.id.btn_back,R.id.tv_method_change})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;
            case R.id.btn_make:
                /*制作*/
                if (Firstequpment!=null&&Firstequpment.getMStage()==0xb1){
                customDialog();
                    Map<String,Object> params = new HashMap<>();
                    params.put("teaName",tea.getTeaNameEn());
                    params.put("userId",userId);
                    params.put("teaId",tea.getTeaId());
                    params.put("deviceId",Firstequpment.getEqupmentId());
                    new AddTeaAsyncTask().execute(params);
                }else {
                    toast(getResources().getText(R.string.toast_make_make).toString());
                }

                break;
            case R.id.tv_add_save:
                /*保存*/
                if (type==0){
                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId);
                params.put("brewName",tv_power.getText().toString());
                params.put("temperature",tv_add_temp.getText().toString().trim());
                params.put("waterYield",beautySeekBar.getProgress());
                params.put("seconds",tv_add_time.getText().toString().trim());
                new AddMethordAsynTask().execute(params);
                }else if (type==1){
                    Map<String,Object> params = new HashMap<>();
                    params.put("id",makeMethod.getId());
                    params.put("userId",userId);
                    params.put("brewName",tv_power.getText().toString());
                    params.put("temperature",tv_add_temp.getText().toString().trim());
                    params.put("waterYield",beautySeekBar.getProgress());
                    params.put("seconds",tv_add_time.getText().toString().trim());
                    new UpdataMethordAsynTask().execute(params);
                 }
                break;
            case R.id.btn_back:
                /*恢复默认*/

                break;

            case R.id.tv_method_change:
                customDialog1();
                break;
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
        tv_dia_title.setText("方法名称");
        et_dia_name.setHint("请输入要修改的名称");
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
                    toast("设备名不能为空");
                }

            }
        });
        dialog.show();

    }


    String returnMsg1,returnMsg2;
    class UpdataMethordAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/updateBrew",prarms);

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
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    setResult(2000);
                    finish();
                    toast( returnMsg1);

                    break;
                case "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
                    break;

            }
        }
    }


    class AddMethordAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/addBrew",prarms);

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
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    setResult(2000);
                    finish();
                    toast( returnMsg1);

                    break;
                case "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
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
            super.onPostExecute(s);

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
                    if (nowStage ==2){
                        searchThread.stopThread();
                        waterView.setValue(110f );
                        tv_make_title.setText(R.string.equ_xq_cpwc);
                        tv_number.setText(100+"");
                        li_make_finish.setVisibility(View.VISIBLE);
                        bt_view_stop.setVisibility(View.GONE);
                        dialog1.setCanceledOnTouchOutside(true);
                    }
                }
            }
            Equpment msg1 =(Equpment)intent.getSerializableExtra("msg1");
            if (msg1!=null){
                Firstequpment = msg1;
            }

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
    Button bt_view_stop;
    private void customDialog( ) {
        IsMakeing =0;
        dialog1  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.view_make, null);
        bt_view_stop = view.findViewById(R.id.bt_view_stop);
        tv_number = view.findViewById(R.id.tv_number);
        final TextView tv_units = view.findViewById(R.id.tv_units);
        tv_make_title = view.findViewById(R.id.tv_make_title);
        li_make_finish = view.findViewById(R.id.li_make_finish);
        tv_make_title.setText(R.string.equ_xq_jpz);
        waterView = (WaveProgress) view.findViewById(R.id.waterView);
        waterView.setWaveColor(Color.parseColor("#37dbc2"), Color.parseColor("#81fbe6"));
        waterView.setMaxValue(100f);
//        waterView.setValue(100f );

        MQservice.sendMakeMess(beautySeekBar.getProgress(),Integer.valueOf(tv_add_time.getText().toString().trim()),Integer.valueOf(tv_add_temp.getText().toString().trim()),firstMac);
        waterView.setValue(50f);
        final long time =Integer.valueOf(tv_add_time.getText().toString().trim());
        countDownTimer = new CountDownTimer(time*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_number.setText(millisUntilFinished/1000+"");
                Log.e("DDDDDD", "onTick: -->"+ (millisUntilFinished /1000));

            }

            @Override
            public void onFinish() {
                waterView.setValue(-10f );
                tv_units.setText("％");
                tv_make_title.setText(R.string.equ_xq_jpz);
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
