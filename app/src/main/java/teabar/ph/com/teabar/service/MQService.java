package teabar.ph.com.teabar.service;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.peihou.daemonservice.AbsHeartBeatService;
import com.peihou.daemonservice.DaemonHolder;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddMethodActivity1;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.activity.login.LoginActivity;
import teabar.ph.com.teabar.fragment.EqumentFragment;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.TenTwoUtil;
import teabar.ph.com.teabar.view.AlermDialog4;

public class MQService extends AbsHeartBeatService {

    private String host = "tcp://47.98.131.11:1883";
    /**
     * 主机名称
     */
    private String userName = "admin";
    /**
     * 用户名
     */
    private String passWord = "Xr7891122";
    /**
     * 密码
     */
    private Context mContext = this;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private MqttClient client;
    private MqttConnectOptions options;
    String clientId = "";
    LocalBinder binder = new LocalBinder();
    int headCode = 0x32;
    int ctrlCode2 = 0xF0;
    int length = 27;
    EquipmentImpl equipmentDao;
    SharedPreferences preferences;
    public static int reset = 0;
    SharedPreferences alermPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        alermPreferences=getSharedPreferences("alerm",MODE_PRIVATE);

        equipmentDao = new EquipmentImpl(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        Log.i("MQService", "-->onCreate");
        init();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MQService", "-->onStartCommand");
        connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            scheduler.shutdown();
            client.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onStartService() {
        Log.i("MQService", "onStartService()");
    }

    @Override
    public void onStopService() {
        Log.i("MQService", "onStopService()");
    }

    @Override
    public long getDelayExecutedMillis() {
        return 0;
    }

    @Override
    public long getHeartBeatMillis() {
        return 30 * 1000;
    }

    String TAG="MQService";
    @Override
    public void onHeartBeat() {

        Log.i(TAG,"-->onHeartBeat");
        if (alermPreferences!=null && alermPreferences.contains("time")){
            boolean isOpen = alermPreferences.getBoolean("open", false);

            String time = alermPreferences.getString("time", "");
            String[] times = time.split(":");
            int hour = Integer.parseInt(times[0]);
            int min = Integer.parseInt(times[1]);
            Log.i("MQService", "onHeartBeat()"+hour+":"+min+","+isOpen);
            long setTime = (hour * 60 + min);
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMin = calendar.get(Calendar.MINUTE);
            long currentTime=(currentHour*60+currentMin);
            Log.i("MQService","setTime="+setTime+",currentTime="+currentTime);

            if (setTime==currentTime) {
                showNotifaction();
            }
        }


    }
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
    public void showNotifaction(){
        Bitmap btm = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), PUSH_CHANNEL_ID);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TarBar")
                .setContentText("亲，你喝茶的时间到啦");
//                         mBuilder.setTicker("亲，你喝茶的时间到啦");//第一次提示消息的时候显示在通知栏上
//                         mBuilder.setNumber(12);
//                         mBuilder.setLargeIcon(btm);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);//自己维护通知的消失
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

//                         //构建一个Intent
//                         Intent resultIntent = new Intent(this,
//                                         SetDrinkActivity.class);
//                         //封装一个Intent
//                         PendingIntent resultPendingIntent = PendingIntent.getActivity(
//                                 SetDrinkActivity.this, 0, resultIntent,
//                                         PendingIntent.FLAG_UPDATE_CURRENT);
//                         // 设置通知主题的意图
//                         mBuilder.setContentIntent(resultPendingIntent);
//                         获取通知管理器对象


        mNotificationManager.notify(0, notification);
    }

    /**
     *
     */
    /**
     * 初始化MQTT
     */
    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            clientId = UUID.randomUUID().toString();
            client = new MqttClient(host, clientId,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(15);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(60);


            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    try {
                        Log.i("topicName", "topicName:" + topicName);
                        String msg = message.toString();
                        new LoadAsyncTask().execute(topicName, message.toString());
                        Log.i("message", "message:" + msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送查询机器
     *
     * @param
     */
    public void sendFindEqu(String mac) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0xA1;
            int length = 0;
            int checkCode = (headCode + ctrlCode + length) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/status/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送开关机命令
     * 1、type:0、1、2、3 。
     * 0: 待机
     * 1: 休眠
     *
     * @param
     */
    public void sendOpenEqu(int type, String mac) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x04;
            int checkCode = (headCode + ctrlCode + length + type + ctrlCode2) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, type);
            jsonArray.put(4, ctrlCode2);
            for (int i = 5; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送z制作命令
     *
     * @param
     */
    public void sendMakeMess(int size, int time, int temp, String mac) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x01;
            int stage = 0xc2;
            int height = size / 256;
            int low = size % 256;
            int checkCode = (headCode + ctrlCode + length + stage + ctrlCode2 + height + length + time + temp) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, stage);
            jsonArray.put(4, ctrlCode2);
            for (int i = 5; i < 15; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(15, height);
            jsonArray.put(16, low);
            jsonArray.put(17, 0);
            jsonArray.put(18, temp);
            jsonArray.put(19, time);
            for (int j = 20; j < 34; j++) {
                jsonArray.put(j, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);

            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送停止冲泡功能
     *
     * @param
     */
    public void sendStop(String mac) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x03;
            int stage = 0xc1;
            int checkCode = (headCode + ctrlCode + length + stage + ctrlCode2) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, stage);
            jsonArray.put(4, ctrlCode2);
            for (int i = 5; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送燈光顏色命令
     * 选择设置
     *
     * @param
     */
    public void sendLightColor(String mac, int choose, int r, int g, int b, int mode) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode;
            if (choose == 0) {
                ctrlCode = 0x02;
            } else {
                ctrlCode = 0x05;
            }
            int checkCode = (headCode + ctrlCode + length + ctrlCode2 + r + g + b + mode) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, 0);
            jsonArray.put(4, ctrlCode2);
            jsonArray.put(5, mode);
            jsonArray.put(6, 0);
            jsonArray.put(7, 0);
            jsonArray.put(8, 0);
            jsonArray.put(9, r);
            jsonArray.put(10, g);
            jsonArray.put(11, b);
            for (int i = 12; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送沖洗杯數
     * 选择设置
     *
     * @param
     */
    public void sendWashNum(String mac, int number) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x08;
            int checkCode = (headCode + ctrlCode + length + ctrlCode2 + number) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, 0);
            jsonArray.put(4, ctrlCode2);
            jsonArray.put(5, 0);
            for (int i = 6; i < 25; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(25, number);
            for (int i = 26; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送燈光開關
     * 选择设置
     * 128:  燈光開
     * 0：燈光關
     *
     * @param
     */
    public void sendLightOpen(String mac, int number) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x06;
            int checkCode = (headCode + ctrlCode + length + ctrlCode2 + number) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, 0);
            jsonArray.put(4, ctrlCode2);
            jsonArray.put(5, number);
            for (int i = 6; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送製作查詢命令
     *
     * @param
     */
    public void sendSearchML(String mac) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x0C;
            int checkCode = (headCode + ctrlCode + length + ctrlCode2) % 256;
            jsonArray.put(0, headCode);
            jsonArray.put(1, ctrlCode);
            jsonArray.put(2, length);
            jsonArray.put(3, 0);
            jsonArray.put(4, ctrlCode2);
            for (int i = 5; i < 34; i++) {
                jsonArray.put(i, 0);
            }
            jsonArray.put(34, checkCode);
            jsonObject.put("Coffee", jsonArray);
            String topicName = "tea/" + mac + "/operate/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: -------->" + success + jsonArray.toString() + "...." + jsonArray.length());
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String ArrayTransformString(int[] SafetyMeasure) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SafetyMeasure.length; i++) {
            sb.append(SafetyMeasure[i] + ",");
        }
        return sb.toString();
    }

    static boolean hasError = false;
    String errorCode = "";

    @SuppressLint("StaticFieldLeak")
    class LoadAsyncTask extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... strings) {

            String topicName = strings[0];/**收到的主题*/
            String message = strings[1];/**收到的消息*/
            Log.i("topicName", "-->:" + topicName);
            String macAddress = null;
            String topic = null;
            if (topicName.startsWith("tea")) {
                String[] aa = topicName.split("/");
                if (aa.length > 2) {
                    macAddress = aa[1];
                    topic = aa[2];
                }
            }
            JSONArray messageJsonArray = null;
            JSONObject messageJsonObject = null;
            Equpment equipment = equipmentDao.findDeviceByMacAddress2(macAddress);
//            errorCode = "1,1,1,1,1,1,1";
//            /*方法是倒叙的 可直bit0 是数字第一位*/
//            hasError=true;
//            if (hasError){
//                hasError =false;
//                Message message1= handler.obtainMessage();
//                message1.obj=errorCode;
//                message1.what=1;
//                handler.sendMessage(message1);
//            }
            try {
                if (!TextUtils.isEmpty(message) && message.startsWith("{") && message.endsWith("}")) {
                    messageJsonObject = new JSONObject(message);
                }
                if (messageJsonObject != null && messageJsonObject.has("Coffee")) {
                    messageJsonArray = messageJsonObject.getJSONArray("Coffee");
                }
                if (topicName.contains("transfer")) {
                    if (messageJsonArray != null) {
                        Log.e("hasData", "getDate: -->");
                        if (equipment != null) {
                            boolean isFirst = equipment.getIsFirst();//是否是默认设备

                            int mStage;//机器状态
                            String lightColor;//灯光颜色
                            String washTime;//清洗周期

                            int mode; // 燈光模式
                            int lightOpen1;
                            mStage = messageJsonArray.getInt(2);
                            int code = messageJsonArray.getInt(3);
                            int wrongCode[] = TenTwoUtil.changeToTwo(code);
                            errorCode = ArrayTransformString(wrongCode);
//                        errorCode = "1,1,1,1,1,1,1";
                            /*方法是倒叙的 可直bit0 是数字第一位*/
                            for (int i = 0; i < wrongCode.length; i++) {
                                if (wrongCode[i] == 1) {
                                    hasError = true;
                                }
                            }
                            if (hasError) {
                                hasError = false;
                                Message message1 = handler.obtainMessage();
                                message1.obj = errorCode;
                                message1.what = 1;
                                handler.sendMessage(message1);
                            }
                            int lightMes = messageJsonArray.getInt(4);
                            lightOpen1 = TenTwoUtil.changeToTwo(lightMes)[7];
                            lightColor = messageJsonArray.getString(8) + "/" + messageJsonArray.getString(9) + "/" + messageJsonArray.getString(10);
                            int height = messageJsonArray.getInt(21);/*水位查看制作水位*/
                            int low = messageJsonArray.getInt(22);
                            washTime = messageJsonArray.getString(27);
//                        mode = messageJsonArray.getInt(8);
                            equipment.setErrorCode(errorCode);
                            equipment.setMStage(mStage);
                            equipment.setWashTime(washTime);
                            equipment.setLightColor(lightColor);
//                        equipment.setMode(mode);
                            equipment.setLightOpen(lightOpen1);
                            equipment.setOnLine(true);
                            errorCode = "";
                            if (isFirst) {
                                if (MainActivity.isRunning) {
                                    Intent mqttIntent = new Intent("MainActivity");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                if (MakeActivity.isRunning) {
                                    Intent mqttIntent = new Intent("MakeActivity");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                if (AddMethodActivity1.isRunning) {
                                    Intent mqttIntent = new Intent("AddMethodActivity1");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                equipmentDao.update(equipment);
                            }

                            if (EqumentFragment.isRunning) {
                                Intent mqttIntent = new Intent("EqumentFragment");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }

                            if (mStage == 0xb4 || mStage == 0xb3 || mStage == 0xb5) {
                                if (MakeActivity.isRunning) {
                                    Intent mqttIntent = new Intent("MakeActivity");
                                    mqttIntent.putExtra("nowStage", mStage);
                                    mqttIntent.putExtra("height", height);
                                    mqttIntent.putExtra("low", low);
                                    sendBroadcast(mqttIntent);
                                }
                                if (AddMethodActivity1.isRunning) {
                                    Intent mqttIntent = new Intent("AddMethodActivity1");
                                    mqttIntent.putExtra("nowStage", mStage);
                                    mqttIntent.putExtra("height", height);
                                    mqttIntent.putExtra("low", low);
                                    sendBroadcast(mqttIntent);
                                }
                            }


                        }

                        if ("reset".equals(topic)) {
                            if (equipment != null) {
                                if (equipment.getIsFirst()) {
                                    reset = 1;
                                    if (MainActivity.isRunning) {
                                        Intent mqttIntent = new Intent("MainActivity");
                                        mqttIntent.putExtra("reset", 1);
                                    }
                                } else {
                                    reset = 2;
                                    if (EqumentFragment.isRunning) {
                                        Intent mqttIntent = new Intent("EqumentFragment");
                                        mqttIntent.putExtra("reset", 2);
                                    }
                                }
                                equipmentDao.delete(equipment);
                            }

                        }
                        if ("lwt".equals(topic)) {
                            /*離綫主題*/
                            boolean isFirst = equipment.getIsFirst();//是否是默认设备
                            equipment.setOnLine(false);
                            if (isFirst) {
                                if (MainActivity.isRunning) {
                                    Intent mqttIntent = new Intent("MainActivity");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                if (MakeActivity.isRunning) {
                                    Intent mqttIntent = new Intent("MakeActivity");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                if (AddMethodActivity1.isRunning) {
                                    Intent mqttIntent = new Intent("AddMethodActivity1");
                                    mqttIntent.putExtra("msg", macAddress);
                                    mqttIntent.putExtra("msg1", equipment);
                                    sendBroadcast(mqttIntent);
                                }
                                equipmentDao.update(equipment);
                            }
                            if (EqumentFragment.isRunning) {
                                Intent mqttIntent = new Intent("EqumentFragment");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                        }

                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String errorCode = (String) msg.obj;
                if (alermDialog4 != null && alermDialog4.isShowing()) {
                    alermDialog4.dismiss();
                }
                setAlermDialog(0, errorCode);
            }
        }
    };
    AlermDialog4 alermDialog4;

    private void setAlermDialog(int mode, String line) {
        try {
            alermDialog4 = new AlermDialog4(MQService.this);
            alermDialog4.setLine(line);
            alermDialog4.setMode(mode);
            if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.screenOrientation = Configuration.ORIENTATION_PORTRAIT;
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

                alermDialog4.getWindow().setAttributes(params);
            } else {
//                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//                params.screenOrientation = Configuration.ORIENTATION_PORTRAIT;
//                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
                alermDialog4.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            alermDialog4.setCanceledOnTouchOutside(false);
            alermDialog4.setOnNegativeClickListener(new AlermDialog4.OnNegativeClickListener() {
                @Override
                public void onNegativeClick() {
                    alermDialog4.dismiss();
                }
            });
            alermDialog4.setOnPositiveClickListener(new AlermDialog4.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    alermDialog4.dismiss();

                }
            });

            if (alermDialog4 != null) {
                alermDialog4.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
            }
            alermDialog4.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getTopicNames() {
        List<String> list = new ArrayList<>();
        List<Equpment> equpments = equipmentDao.findAll();

        for (Equpment equpment : equpments) {
            String macAddress = equpment.getMacAdress();
            String onlineTopicName = "";
            String offlineTopicName = "";
            onlineTopicName = "tea/" + macAddress + "/status/transfer";
            offlineTopicName = "tea/" + macAddress + "/lwt";
            String s3 = "tea/" + macAddress + "/operate/transfer";
            String s4 = "tea/" + macAddress + "/extra/transfer";
            String s5 = "tea/" + macAddress + "/reset/transfer";
            list.add(onlineTopicName);
            list.add(offlineTopicName);
            list.add(s3);
            list.add(s4);
            list.add(s5);
        }

        return list;
    }

    /***
     * 连接MQTT
     */
    public void connect() {
        try {
            if (client != null && !client.isConnected()) {
                client.connect(options);
            }
            List<String> topicNames = getTopicNames();
            new ConAsync().execute(topicNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新连接MQTT
     */
    private void startReconnect() {

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送MQTT主题
     */

    public boolean publish(String topicName, int qos, String payload) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
                qos = 1;
                //设置保留消息
                if (topicName.contains("friend")) {
                    message.setRetained(true);
                }
                if (topicName.contains("clockuniversal")) {
                    message.setRetained(true);
                }
                message.setQos(qos);
                client.publish(topicName, message);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 订阅MQTT主题
     *
     * @param topicName
     * @param qos
     * @return
     */
    public boolean subscribe(String topicName, int qos) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {

                client.subscribe(topicName, 1);
                flag = true;
                Log.e("SSXCCCCCCC", "subscribe: -->" + topicName);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public class LocalBinder extends Binder {

        public MQService getService() {
            Log.i("Binder", "Binder");
            return MQService.this;
        }
    }


    /**
     * @param topicName
     */
    public void unsubscribe(String topicName) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topicName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class ConAsync extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected Void doInBackground(List<String>... lists) {
            try {

                List<String> topicNames = getTopicNames();
                boolean sss = client.isConnected();
                Log.i("sss", "-->" + sss);
                if (client.isConnected() && !topicNames.isEmpty()) {
                    for (String topicName : topicNames) {
                        if (!TextUtils.isEmpty(topicName)) {
                            client.subscribe(topicName, 1);
                            Log.i("client", "-->" + topicName);
                            Log.e("FFFDDDD", "doInBackground: 订阅-->" + topicName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
