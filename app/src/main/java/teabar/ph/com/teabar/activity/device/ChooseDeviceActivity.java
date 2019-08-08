package teabar.ph.com.teabar.activity.device;

import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.ChooseDeviceAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.NetWorkUtil;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.WaveProgress;

//選擇一個設備來進行沖泡
public class ChooseDeviceActivity extends BaseActivity {
    @BindView(R.id.rv_alldevice)
    RecyclerView rv_alldevice;
    @BindView(R.id.refreshLayout_xq)
    RefreshLayout refreshLayou;
    public static boolean isRunning = false;
    private boolean MQBound;
    MessageReceiver receiver;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    MyApplication application;
    SharedPreferences preferences;
    long teaId= -1;
    String id ;
    String userId;

    int water, temp,time;
    ChooseDeviceAdapter chooseDeviceAdapter ;
    ShareDialog shareDialog;
    @Override
    public void initParms(Bundle parms) {
        teaId = parms.getLong("teaId");
        temp = parms.getInt("temp");
        time = parms.getInt("time");
        water= parms.getInt("water");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_choosedevice;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        if (teaId!=-1){

            new getTeaAsyncTask(this).execute();
            id = teaId+"";
        }
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","");
        equipmentDao = new EquipmentImpl( getApplicationContext());
        equpments= equipmentDao.findAll();
        if(equpments.size()==1){
            equpments.get(0).setInform_isHot(true);
        }
        chooseDeviceAdapter = new ChooseDeviceAdapter(this,equpments);
        rv_alldevice.setLayoutManager(new LinearLayoutManager(this));
        rv_alldevice.setAdapter(chooseDeviceAdapter);
        chooseDeviceAdapter.SetOnItemClick(new ChooseDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                choosePosition = position;
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
                    new  FirstAsynctask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
//                    toast(   "无网络可用，请检查网络");
                }

            }

        });
        shareDialog = new ShareDialog(this);
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("ChooseDeviceActivity");
        receiver = new  MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning= true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MQBound) {
            unbindService(MQconnection);
        }
        if (receiver!=null)
            unregisterReceiver(receiver);
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        if (countDownTimer1!=null){
            countDownTimer1.cancel();
        }
        isRunning=false;
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
            new  FirstAsynctask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    Equpment msg1;
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nowStage = intent.getIntExtra("nowStage",-1);
            int height = intent.getIntExtra("height",0);
            int low = intent.getIntExtra("low",0);
            String errorCode = intent.getStringExtra("errorCode");
            int size = height*256 +low;
            Log.e(TAG, "onReceive: -->"+nowStage );
            if (!TextUtils.isEmpty(errorCode)) {
                /*判断有错误直接停止*/
                if (errorCode.contains("1")) {
                    if (dialog1 != null && dialog1.isShowing()) {
                        dialog1.dismiss();
                    }
                    if (searchThread!=null){
                        searchThread.stopThread();

                    }
                }
            }

            if (IsMakeing==1 || nowStage==0xb8){
                /*正在制作或者状态为0xb8才发送*/
                Message message = new Message();
                message.arg1 = size;
                message.arg2 = nowStage;
                handler.sendMessage(message);
            }
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            msg1 = (Equpment) intent.getSerializableExtra("msg1");
            if (!TextUtils.isEmpty(msg)&&msg1!=null){
                chooseDeviceAdapter.setEqumentData(msg,msg1);
            }


        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                int size = msg.arg1;
                int nowStage = msg.arg2;
                if (waterView!=null){
                    float value;
                    if (water==0 ){
                         value = 100* size/tea.getWaterYield();
                    }else {
                         value = 100* size/water;
                    }
                    if ((int) value!=0){
                        waterView.setValue(value);
                        tv_number.setText( (int) value+"");
                    }
                    if (nowStage ==0xb5){
                        searchThread.stopThread();
                        waterView.setValue(150f );
                        tv_make_title.setText(R.string.equ_xq_cpwc);
                        tv_number.setText(100+"");
                        tv_full.setVisibility(View.VISIBLE);
                        li_make_finish.setVisibility(View.VISIBLE);
                        bt_view_stop.setVisibility(View.GONE);
                    }
                    /*看协议查看机器状态*/
                    if (nowStage!=-1){
                        if (nowStage != 0xb3 && nowStage != 0xb4 && nowStage != 0xb5) {
                            if (!"100".equals(tv_number.getText().toString().trim())){
                                if (dialog1 != null && dialog1.isShowing()) {
                                    dialog1.dismiss();
                                    if (searchThread != null) {
                                        searchThread.stopThread();

                                    }
                                }
                            }
                        }
                    }



                }
                 Log.e(TAG, "handleMessage: -->"+nowStage );
                 if (nowStage==0xb8){
                    if (dialog1!=null&&dialog1.isShowing()){
                    dialog1.dismiss();
                      }

                     if (searchThread!=null){
                         searchThread.stopThread();
                     }
                     if (countDownTimer!=null){
                         countDownTimer.cancel();
                     }
                     if (countDownTimer1!=null){
                         countDownTimer1.cancel();
                     }
                 }

        }
    };

    /*发送查询指令*/
    @SuppressLint("StaticFieldLeak")
    class  FirstAsynctask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MQservice.loadAlltopic();
            for (Equpment equpment:chooseDeviceAdapter.getmData()){
                try {
                    Thread.sleep(500);
                    if (!TextUtils.isEmpty(equpment.getMacAdress()))
                        if (equpment.getMStage()==0xb6||equpment.getMStage()==0xb7){

                        }else{
                            MQservice.sendFindEqu(equpment.getMacAdress());
                        }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            refreshLayou.finishRefresh(true);
            return null;
        }
    }

    /*
    * 冲泡的dialog
    * */

    WaveProgress waterView;
    Dialog dialog1;
    CountDownTimer countDownTimer, countDownTimer1;
    int IsMakeing = 0;
    TextView tv_make_title,tv_full,tv_brew_zb;
    TextView tv_number;
    LinearLayout li_make_finish;
    Button bt_view_stop,bt_view_sc,bt_view_fx;
    RelativeLayout rl_brew_time;
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
        tv_full = view .findViewById(R.id.tv_full);
        tv_brew_zb = view.findViewById(R.id.tv_brew_zb);
        rl_brew_time = view.findViewById(R.id.rl_brew_time);
        TextView tv_dialog_bj = view.findViewById(R.id.tv_dialog_bj);
        ImageView iv_dialog_move = view.findViewById(R.id.iv_dialog_move);
        ImageView bt_dialog_bj = view.findViewById(R.id.bt_dialog_bj);
        tv_dialog_bj.setVisibility(View.INVISIBLE);
        bt_dialog_bj.setVisibility(View.INVISIBLE);
        tv_full.setVisibility(View.INVISIBLE);
        rl_brew_time.setVisibility(View.INVISIBLE);
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
        /* 每种茶有特定 的颜色冲后台获得发送个设备*/
        int r =0;
        int g = 0;
        int b =0;
        if (tea!=null){
            String rgb = tea.getRgb();
            String[] aa = rgb.split(",");
            if (aa.length>2){
                r = Integer.valueOf(aa[0]);
                g = Integer.valueOf(aa[1]);
                b = Integer.valueOf(aa[2]);
            }
        }

//        waterView.setValue(100f );
        if ( temp ==0&& water==0&&time==0){
            if (tea!=null) {
                MQservice.sendMakeMess(tea.getWaterYield(), tea.getSeconds(), tea.getTemperature(), chooseDeviceAdapter.getmData().get(choosePosition).getMacAdress(), r, g, b);
            }
        }else {
            MQservice.sendMakeMess( water,Integer.valueOf(time),Integer.valueOf(temp),chooseDeviceAdapter.getmData().get(choosePosition).getMacAdress(),r,g,b);
        }
//        waterView.setValue(50f);
        long time1;
        if (time==0){
           time1 =tea.getSeconds();
        }else {
            time1 = time;
        }
        /*等待三秒进入倒计时浸泡*/
       countDownTimer1 = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                countDownTimer.start();
                rl_brew_time.setVisibility(View.VISIBLE);
                tv_brew_zb.setVisibility(View.INVISIBLE);
            }
        }.start()  ;
        /*
        * 倒计时浸泡
        * */
        countDownTimer = new CountDownTimer(time1*1000,1000) {
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
                MQservice.sendStop(chooseDeviceAdapter.getmData().get(choosePosition).getMacAdress());
                if (searchThread!=null&&Running){
                    searchThread.stopThread();
                }
                dialog1.dismiss();
                if (countDownTimer!=null){
                    countDownTimer.cancel();
                }
                if (countDownTimer1!=null){
                    countDownTimer1.cancel();
                }
            }
        });
        /*facebook 分享
        * */
        bt_view_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    "https://lify-wellness.myshopify.com/collections/all"
                            .setContentUrl(Uri.parse(tea.getTeaPhoto()))
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("#Lify").build())
                            .setQuote(tea.getTeaNameEn())
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });
        bt_view_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog1!=null&&dialog1.isShowing()){
                    dialog1.dismiss();
                }
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

    /*
    * 冲泡时每隔3s查询机器冲泡水位
    * */
    int choosePosition;
    SearchThread searchThread;
    boolean  Running = true;
    class SearchThread extends  Thread{
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    if (Running) {
                        sleep(3000);
                        Log.e(TAG, "run: -->+++++++++++");
                        MQservice.sendSearchML(chooseDeviceAdapter.getmData().get(choosePosition).getMacAdress());
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
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({ R.id.bt_equ_choose,R.id.iv_choose_fh})
    public  void onClick(View view){
        switch (view.getId()){
            case R.id.bt_equ_choose://選擇一個在綫的設備進行沖泡
                    if (chooseDeviceAdapter.getItemCount()>0) {
                        if (!Utils.isFastClick()) {
                            if (chooseDeviceAdapter.getmData().get(choosePosition).getOnLine()) {
                                String wrongCode = chooseDeviceAdapter.getmData().get(choosePosition).getErrorCode();
                                boolean errorcode = false;
                                if (!TextUtils.isEmpty(wrongCode)) {
                                    /*判断是否存在错误*/
                                    String[] aa = wrongCode.split(",");
                                    for (int i = 0; i < aa.length; i++) {
                                        if ("1".equals(aa[i])) {
                                            errorcode = true;
                                        }
                                    }
                                }
                                if (!errorcode) {
                                    if (chooseDeviceAdapter.getmData().get(choosePosition).getHotFinish() == 1) {
                                        customDialog();
                                        Map<String, Object> params = new HashMap<>();
                                        params.put("teaName", tea.getTeaNameEn());
                                        params.put("userId", userId);
                                        params.put("teaId", id);
                                        params.put("deviceId", chooseDeviceAdapter.getmData().get(choosePosition).getEqupmentId());
                                        new AddTeaAsyncTask(this).execute(params);
                                    } else {
                                        toast(getResources().getText(R.string.toast_make_make).toString());
                                        MQservice.sendFindEqu(chooseDeviceAdapter.getmData().get(choosePosition).getMacAdress());
                                    }
                                }
                            } else {

                                ToastUtil.showShort(this, getText(R.string.toast_equ_online).toString());
                            }
                        } else {
                            ToastUtil.showShort(this, getText(R.string.toast_equ_fast).toString());
                        }
                    }else {
                        ToastUtil.showShort(this,getText(R.string.toast_equ_add).toString());
                    }
                break;

            case R.id.iv_choose_fh:
                finish();
                break;
        }
    }

    /**
     *  添加到喝茶記錄
     *
     */

    class AddTeaAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public AddTeaAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
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
    /**
     *  獲取詳情信息
     *
     */
    String returnMsg1,returnMsg2;
    Tea tea;
    class getTeaAsyncTask extends BaseWeakAsyncTask<Void,Void,String,BaseActivity> {

        public getTeaAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Void... voids) {
            String code = "";
            String ip;
            ip ="/app/getTeaById?teaId="+teaId+"&userId="+userId;
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+ip );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){
                            JSONObject jsonObject1  = jsonObject.getJSONObject("data");
                            Gson gson = new Gson();
                            tea = gson.fromJson(jsonObject1.toString(),Tea.class);
                        }
                        returnMsg2 = jsonObject.getString("message2");
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


                    break;

                case "4000":
                    toast(  getText(R.string.toast_all_cs).toString());
                    break;
                default:

//                    if (application.IsEnglish()==0){
//                        toast(  returnMsg1);
//                    }else {
//                        toast(  returnMsg2);
//                    }

                    break;

            }
        }


    }
    //显示dialog
    QMUITipDialog tipDialog;
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }
}
