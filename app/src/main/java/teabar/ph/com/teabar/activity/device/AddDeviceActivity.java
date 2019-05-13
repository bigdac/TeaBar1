package teabar.ph.com.teabar.activity.device;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.esptouch.EspWifiAdminSimple;
import teabar.ph.com.teabar.esptouch.EsptouchTask;
import teabar.ph.com.teabar.esptouch.IEsptouchListener;
import teabar.ph.com.teabar.esptouch.IEsptouchResult;
import teabar.ph.com.teabar.esptouch.IEsptouchTask;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;


public class AddDeviceActivity extends BaseActivity {
    MyApplication application;
    Unbinder unbinder;
    @BindView(R.id.et_add_name)
    EditText et_add_name;
    @BindView(R.id.et_add_pass)
    EditText et_add_pass;
    @BindView(R.id.li_device_ask)
    LinearLayout li_device_ask;
    @BindView(R.id.bt_device_add)
    Button bt_device_add;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    private EspWifiAdminSimple mWifiAdmin;
    String deviceMac,userId;
    QMUITipDialog tipDialog;
    SharedPreferences preferences;
    private  boolean clockisBound;
    EquipmentImpl equipmentDao;
    private static final int MESSAGE_SUCCESS1 = 1;
    private static final int MESSAGE_SUCCESS2 = 2;
    List<Equpment> list ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_adddevice;
    }

    @Override
    public void initView(View view) {
        mWifiAdmin = new EspWifiAdminSimple(this);
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        equipmentDao = new EquipmentImpl(getApplicationContext());
        list = equipmentDao.findAll();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getLong("userId",0)+"";
        //绑定services
        clockintent = new Intent(AddDeviceActivity.this, MQService.class);
        clockisBound = bindService(clockintent, clockconnection, Context.BIND_AUTO_CREATE);
    }
    Intent clockintent;
    MQService clcokservice;
    boolean boundclock;
    ServiceConnection clockconnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            clcokservice = binder.getService();
            boundclock = true;
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->" );
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

    @Override
    protected void onStart() {
        super.onStart();
        String ssid=mWifiAdmin.getWifiConnectedSsid();
        et_add_name.setText(ssid);
        et_add_name.setEnabled(false);
    }

    @OnClick({R.id.bt_device_add,R.id.li_device_ask,R.id.iv_power_fh })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_device_add:
                String ssid=et_add_name.getText().toString().trim();
                String apBssid=mWifiAdmin.getWifiConnectedBssid();
                String apPassword=et_add_pass.getText().toString();
                String taskResultCountStr = "1";
                if (apPassword.length()>0){
                    new EsptouchAsyncTask3().execute(ssid, apBssid, apPassword, taskResultCountStr);
//                    Map<String,Object> params = new HashMap<>();
//                    params.put("userId",userId);
//                    params.put("mac","123456789");
//                    showProgressDialog("请稍后。。。");
//                    new addDeviceAsyncTask().execute(params);
                }else {
                    toast("请输入密码");
                }
            break;

            case R.id.li_device_ask:
                customDialog();
                break;

            case R.id.iv_power_fh:
                finish();
                break;

        }
    }

    /**
     * 自定义对话框
     */
    Dialog dialog;
    int position ;
    ImageView imageView;
    private void customDialog() {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_playgif, null);
        imageView = view.findViewById(R.id.iv_dia_gif);
        playGif1(0);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.43f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        dialog.show();

    }

    /**
     * 播放Gif动画
     * */
    int duration = 0;
    int gif[] = {R.drawable.adddevice1,R.drawable.adddevice2};
    public void playGif1(final int i){
        duration=0;
        Glide.with(this)
                .load(gif[i])
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Integer, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception arg0, Integer arg1,
                                               Target<GlideDrawable> arg2, boolean arg3) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   Integer model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        // 计算动画时长
                        GifDrawable drawable = (GifDrawable) resource;
                        GifDecoder decoder = drawable.getDecoder();
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }
                        //发送延时消息，通知动画结束
                        if (i==0){
                            Log.e(TAG, "onResourceReady1: --》"+duration );
                            handler.sendEmptyMessageDelayed(MESSAGE_SUCCESS1,
                                    duration);
                        }else {
                            Log.e(TAG, "onResourceReady2: --》"+duration );
                            handler.sendEmptyMessageDelayed(MESSAGE_SUCCESS2,
                                    duration);
                        }

                        return false;
                    }
                }) //仅仅加载一次gif动画
                .into(new GlideDrawableImageViewTarget(imageView, 1));

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==MESSAGE_SUCCESS1){
                playGif1(1);
            }
            if (msg.what==MESSAGE_SUCCESS2){
                playGif1(0);
            }
        }
    };


    /**
     * 添加设备
     * */
    String returnMsg1,returnMsg2;

    class addDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            deviceMac= "123456789";
            String code = "";
            Map<String ,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/addDevice",prarms);
            Log.e("back", "--->"+result);
            if (!ToastUtil.isEmpty(result)){
                if (!"4000".equals(result)){
                try {
                    JSONObject jsonObject= new JSONObject(result);
                    code = jsonObject.getString("state");
                    returnMsg1=jsonObject.getString("message1");
//                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                    if ("200".equals(code)){

                        Equpment equpment =new Equpment();
                        if (list.size()==0){
                            equpment.setIsFirst(true);
                        }else {
                            equpment.setIsFirst(false);
                        }
                        equpment.setMacAdress(deviceMac);
                        equipmentDao.insert(equpment);
                    }
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
                case "4000":
                    toast("请求超时，请重试");
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                break;

                case "200":
                    toast(returnMsg1);
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId);
                    new  FindDeviceAsynTask().execute(params);

                    break;
                    default:
                        if (tipDialog.isShowing()){
                            tipDialog.dismiss();
                        }
                        toast(returnMsg1);
                        break;

            }
        }
    }


    class FindDeviceAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/showUserDevice",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)) {
                            equipmentDao.deleteAll();
                            JSONArray returnData = jsonObject.getJSONArray("data");
                            if (returnData.length()>0){
                                for ( int i =0;i<returnData.length();i++){
                                    JSONObject jsonObject1 = returnData.getJSONObject(i);
                                    Equpment equpment = new Equpment();
                                    equpment.setMacAdress(jsonObject1.getString("mac"));
                                    equpment.setEqupmentId(jsonObject1.getLong("id"));
                                    equpment.setName(jsonObject1.getString("deviceName"));
                                    int flag = jsonObject1.getInt("flag");
                                    if (flag==1){
                                        equpment.setIsFirst(true);

                                    }else {
                                        equpment.setIsFirst(false);
                                    }
                                    equipmentDao.insert(equpment);
                                }
                            }
                        }
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




    //显示dialog
    public void showProgressDialog(String message) {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(message)
                .create();
        tipDialog.show();

    }

    /**
     * WIFI模块配置
     * */

    private IEsptouchTask mEsptouchTask;

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {


        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {

            showProgressDialog("正在配置, 请耐心等待...");


        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, AddDeviceActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    try {
                        Thread.sleep(300);
                        Log.i("IEsptouchResult", "-->" + result.size());
                        for (IEsptouchResult resultInList : result) {
                            //                String ssid=et_ssid.getText().toString();
                            String ssid = resultInList.getBssid();
                            sb.append("配置成功" + ssid);

                            String onlineTopicName = "tea/" + ssid +"/status/transfer";
                            String offlineTopicName = "tea/" + ssid + "/lwt";
                            String operate = "tea/"+ssid+"/operate/transfer";
                            String extra = "tea/"+ssid+"/extra/transfer";
                            String reset = "tea/"+ssid+"/reset/transfer";
                            clcokservice.subscribe(onlineTopicName,1);
                            clcokservice.subscribe(offlineTopicName,1);
                            clcokservice.subscribe(operate,1);
                            clcokservice.subscribe(extra,1);
                            clcokservice.subscribe(reset,1);
                            if (!TextUtils.isEmpty(ssid)) {
                                Map<String,Object> params = new HashMap<>();
                                 params.put("userId",userId);
                                 params.put("mac",et_add_name.getText().toString().trim()+ssid);
                                new addDeviceAsyncTask().execute(params);
                                Log.e(TAG, "onPostExecute: -->ssid"+ et_add_name.getText().toString().trim()+ssid );
                                break;
                            }
                            count++;
                            if (count >= maxDisplayCount) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                } else {
                    if (tipDialog!=null&&tipDialog.isShowing())
                        tipDialog.dismiss();
                    Toast.makeText(AddDeviceActivity.this, "配置失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
//                Toast.makeText(AddDeviceActivity.this, text,
//                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder!=null)
            unbinder.unbind();//解绑注解
        if (handler!=null)
            handler.removeCallbacksAndMessages(null);
        if (clockisBound)
            unbindService(clockconnection);
    }

}
