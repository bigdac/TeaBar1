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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

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
import teabar.ph.com.teabar.activity.MethodActivity;
import teabar.ph.com.teabar.activity.ShareActivity;
import teabar.ph.com.teabar.adpter.MethodAdapter;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.DoubleWaveView;
import teabar.ph.com.teabar.view.WaveProgress;

public class MakeActivity extends BaseActivity {

    @BindView(R.id.tv_make_name)
    TextView tv_make_name;
    @BindView(R.id.tv_make_style)
    TextView tv_make_style;
    @BindView(R.id.tv_make_mes)
    TextView tv_make_mes;
    @BindView(R.id.tv_make_plmes)
    TextView tv_make_plmes;
    @BindView(R.id.tv_make_temp)
    TextView tv_make_temp;
    @BindView(R.id.tv_make_sl)
    TextView tv_make_sl;
    @BindView(R.id.tv_make_time)
    TextView tv_make_time;

    @BindView(R.id.iv_make_pic)
    ImageView iv_make_pic;
    @BindView(R.id.bt_make_make)
    Button bt_make_make;
    @BindView(R.id.iv_make_choose)
    ImageView iv_make_choose;
    @BindView(R.id.iv_make_love)
    ImageView iv_make_love;
    private boolean MQBound;
    MyApplication application;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    String firstMac;
    Equpment Firstequpment;
    public static boolean isRunning = false;
    Tea tea;
    long teaId= -1;
    String userId;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    String id ;
    long FirstId;
    MessageReceiver receiver;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    public void initParms(Bundle parms) {

        teaId = parms.getLong("teaId");
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

        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","");
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equpments= equipmentDao.findAll();
        bt_make_make.setVisibility(View.VISIBLE);
        iv_make_choose.setVisibility(View.VISIBLE);
        if (equpments.size()==0){
            bt_make_make.setVisibility(View.INVISIBLE);
            iv_make_choose.setVisibility(View.INVISIBLE);
        }else {
            if ( teaId==-1){
                bt_make_make.setVisibility(View.INVISIBLE);
                iv_make_choose.setVisibility(View.INVISIBLE);
            }
            for (int i = 0;i<equpments.size();i++){
                if (equpments.get(i).getIsFirst()){
                    Firstequpment = equpments.get(i);
                    firstMac = equpments.get(i).getMacAdress();
                    FirstId = equpments.get(i).getEqupmentId();
                }

            }
        }
            if (teaId!=-1){
                showProgressDialog();
                new getTeaAsyncTask().execute();
                id = teaId+"";
            }
        IntentFilter intentFilter = new IntentFilter("MakeActivity");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
        //绑定services
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {


            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtil.show(MakeActivity.this,"分享成功",Toast.LENGTH_SHORT) ;
            }

            @Override
            public void onCancel() {
                toast("取消分享");
            }

            @Override
            public void onError(FacebookException error) {
                toast("分享失败");
            }
        });

    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
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



    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            int nowStage = intent.getIntExtra("nowStage",-1);
            int height = intent.getIntExtra("height",0);
            int low = intent.getIntExtra("low",0);
            int size = height*256 +low;
            Log.e(TAG, "onReceive: -->"+nowStage );
            if (IsMakeing==1){
                if (waterView!=null){
                    float value = 100* size/tea.getWaterYield();
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
    Dialog dialog;
    Dialog dialog1;
    CountDownTimer countDownTimer;
    int IsMakeing = 0;
    TextView tv_make_title;
    TextView tv_number;
    LinearLayout li_make_finish;
     Button bt_view_stop,bt_view_sc,bt_view_fx;
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
        tv_make_title.setText(R.string.equ_xq_jpz);
        waterView = (WaveProgress) view.findViewById(R.id.waterView);
        waterView.setWaveColor(Color.parseColor("#37dbc2"), Color.parseColor("#81fbe6"));
        waterView.setMaxValue(100f);
//        waterView.setValue(100f );
        MQservice.sendMakeMess(tea.getWaterYield(),tea.getSeconds(),tea.getTemperature(),firstMac);
        waterView.setValue(50f);
        long time =tea.getSeconds();
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
        bt_view_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("#ConnectTheWorld").build())
                            .setQuote("Connect on a global scale.")
                            .build();
                    shareDialog.show(linkContent);
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
                        sleep(3000);
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

    @OnClick({R.id.iv_equ_fh,R.id.iv_make_choose,R.id.bt_make_make,R.id.iv_make_love ,R.id.iv_make_share})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;

            case R.id.iv_make_choose:
                Intent intent = new Intent(this,MethodActivity.class);
                intent.putExtra("tea",tea);
                startActivity(intent);
                break;

            case R.id.bt_make_make:

                if (Firstequpment!=null&&Firstequpment.getMStage()==0xb1){
                    customDialog();
                    Map<String,Object> params = new HashMap<>();
                    params.put("teaName",tea.getTeaNameEn());
                    params.put("userId",userId);
                    params.put("teaId",id);
                    params.put("deviceId",FirstId);
                    new AddTeaAsyncTask().execute(params);
                }else {
                    toast(getResources().getText(R.string.toast_make_make).toString());
                }

                break;
            case R.id.iv_make_love:
                if (tea!=null){
                    if (tea.getIsCollection()==1){
                         customDialog1(false);
                         tea.setIsCollection(0);
                    }else {
                        customDialog1(true);
                        tea.setIsCollection(1);
                    }
                }

                break;

            case R.id.iv_make_share:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("#ConnectTheWorld").build())
                            .setQuote("Connect on a global scale.")
                            .build();
                    shareDialog.show(linkContent);
                }

                break;
        }
    }


    /**
     * 添加喜愛
     */

    boolean which;
    private void customDialog1( final boolean add) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        if (add){
            tv_dia_title.setText( R.string.dialog_love_title);
            et_dia_name.setText(R.string.dialog_love_title1);
        }else {
            tv_dia_title.setText(R.string.dialog_love_deltitle );
            et_dia_name.setText(R.string.dialog_love_deltitle1 );
        }

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

                if (add){
                    which = true;
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId );
                    params.put("teaId",id );
                    new CollectTeaAsyncTask().execute(params);
                    iv_make_love.setImageResource(R.mipmap.social_yes);

                }else {
                    which = false;
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId );
                    params.put("teaId", id);
                    new CollectTeaAsyncTask().execute(params);
                    iv_make_love.setImageResource(R.mipmap.social_no);

                }

                dialog.dismiss();

            }
        });
        dialog.show();

    }

    /**
     *  添加喜愛
     *
     */
    String returnMsg1,returnMsg2;
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
            super.onPostExecute(s);

            switch (s) {
                case "200":

                    break;

                case "4000":
                    toast(  getText(R.string.toast_all_cs).toString());
                    break;
                default:

                    break;

            }
        }
    }

    /**
     *  獲取詳情信息
     *
     */

    class getTeaAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {
                case "200":
                    if (tipDialog!=null&&tipDialog.isShowing()) {
                        tipDialog.dismiss();
                    }
                    String syno = tea.getSynopsisEn() ;
                    String str1 = "<font color='#101010'>"+getText(R.string.tea_kouwei_yc).toString()+" "+"</font>"+syno;
                    String str2 = "<font color='#101010'>"+getText(R.string.tea_kouwei_kw)+" "+"</font>"+tea.getTasteEn();
                    String str3 = "<font color='#101010'>"+getText(R.string.equ_xq_pl)+" "+"</font>"+tea.getIngredientEn();
                    if (TextUtils.isEmpty(syno)){
                        tv_make_mes.setText(Html.fromHtml(str2));
                    }else {
                        tv_make_mes.setText(Html.fromHtml(str1));
                    }

                    tv_make_name.setText(tea.getTeaNameEn());
                    tv_make_style.setText(tea.getTypeEn());
                    tv_make_plmes.setText (Html.fromHtml(str3));
                    tv_make_temp.setText(tea.getTemperature()+"℃");
                    tv_make_sl.setText(tea.getWaterYield()+"ml");
                    tv_make_time.setText(tea.getSeconds()+"");
                    if (tea.getIsCollection()==1){
                        iv_make_love.setImageResource(R.mipmap.social_yes);
                    }
                    Glide.with(MakeActivity.this).load(tea.getTeaPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.white).into(iv_make_pic);

                    break;

                case "4000":
                   toast(  getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()) {
                        tipDialog.dismiss();
                    }
                    if (application.IsEnglish()==0){
                        toast(  returnMsg1);
                    }else {
                        toast(  returnMsg2);
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
        isRunning = true;
    }
}
