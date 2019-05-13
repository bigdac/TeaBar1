package teabar.ph.com.teabar.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddMethodActivity1;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.fragment.EqumentFragment;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.util.HttpUtils;

public class MQService extends Service {

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
   EquipmentImpl equipmentDao;
   SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

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
     * @param
     */
    public void sendFindEqu( String mac) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x01;
            int length = 0;
            int checkCode = (headCode + ctrlCode+length ) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/status/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"+ success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送开关机命令
     *1、type:0、1、2、3 。
     * 0: 待机
     * 1: 休眠
     *
     * @param
     */
    public void sendOpenEqu(int type ,String mac) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x04;
            int length = 1;
            int checkCode = (headCode + ctrlCode+length+type) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,type);
            jsonArray.put(4,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"+type+">>>>>>"+mac+"...."+success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送z制作命令
     *
     *
     * @param
     */
    public void sendMakeMess(int size,int time,int temp, String mac) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x02;
            int length = 4;
            int checkCode = (headCode + ctrlCode+length+time+temp+size) % 256;
            int height = size/256;
            int low = size%256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,height);
            jsonArray.put(4,low);
            jsonArray.put(5,time);
            jsonArray.put(6,temp);
            jsonArray.put(7,checkCode);
            jsonObject.put("Coffee",jsonArray);

            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"  +success );
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
            int length = 0;
            int checkCode = (headCode + ctrlCode+length ) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"  +success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送燈光顏色命令
     *选择设置
     * 0:  灯光设置
     * 1：灯光模式设置
     * @param
     */
    public void sendLightColor(String mac,int choose,int r,int g, int b ,int mode) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x06;
            int length = 5;
            int checkCode = (headCode + ctrlCode+length+choose+r+g+b+mode ) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,choose);
            jsonArray.put(4,r);
            jsonArray.put(5,g);
            jsonArray.put(6,b);
            jsonArray.put(7,mode);
            jsonArray.put(8,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"  +success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送沖洗杯數
     *选择设置
     * 0:  灯光设置
     * 1：灯光模式设置
     * @param
     */
    public void sendWashNum(String mac, int number ) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x08;
            int length = 1;
            int checkCode = (headCode + ctrlCode+length+number ) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,number);
            jsonArray.put(4,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"  +success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送燈光開關
     *选择设置
     * 0:  燈光開
     * 1：燈光關
     * @param
     */
    public void sendLightOpen(String mac, int number ) {


        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int ctrlCode = 0x09;
            int length = 1;
            int checkCode = (headCode + ctrlCode+length+number ) % 256;
            jsonArray.put(0,headCode);
            jsonArray.put(1,ctrlCode);
            jsonArray.put(2,length);
            jsonArray.put(3,number);
            jsonArray.put(4,checkCode);
            jsonObject.put("Coffee",jsonArray);
            String topicName = "tea/"+mac+"/operate/set";
            String payLoad =jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            Log.e("GGGGGTTTTTT", "open: --------------->"  +success );
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                if (aa.length>2){
                    macAddress = aa[1];
                    topic = aa[2];
                }
            }
            JSONArray messageJsonArray = null;
            JSONObject messageJsonObject = null;
            Equpment equipment = equipmentDao.findDeviceByMacAddress2(macAddress);
            try {
                if (!TextUtils.isEmpty(message) && message.startsWith("{") && message.endsWith("}")) {
                    messageJsonObject = new JSONObject(message);
                }
                if (messageJsonObject != null && messageJsonObject.has("Coffee")) {
                    messageJsonArray = messageJsonObject.getJSONArray("Coffee");
                }
                if (topicName.contains("transfer")){
                    if ( messageJsonArray != null && messageJsonArray.getInt(1) == 0xA1) {
                        Log.e("hasData", "getDate: -->");
                        if (equipment!=null){
                        boolean isFirst = equipment.getIsFirst()  ;//是否是默认设备

                        int mStage;//机器状态
                        String lightColor;//灯光颜色
                        String washTime;//清洗周期
                        String errorCode;
                        int mode; // 燈光模式
                        int lightOpen;
                        errorCode = messageJsonArray.getString(3);
                        mStage = messageJsonArray.getInt(4);
                        lightColor = messageJsonArray.getString(5)+"/"+messageJsonArray.getString(6)+"/"+messageJsonArray.getString(7);
                        mode = messageJsonArray.getInt(8);
                        lightOpen = messageJsonArray.getInt(9);
                        equipment.setErrorCode(errorCode);
                        equipment.setMStage(mStage);
                        equipment.setLightColor(lightColor);
                        equipment.setMacAdress(macAddress);
                        equipment.setMode(mode);
                        equipment.setOnLine(true);
                        equipment.setLightOpen(lightOpen);
                        if (isFirst){
                            if (MainActivity.isRunning){
                                Intent mqttIntent = new Intent("MainActivity");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                            if (MakeActivity.isRunning){
                                Intent mqttIntent = new Intent("MakeActivity");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                            if (AddMethodActivity1.isRunning){
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
                    }else if (messageJsonArray != null && messageJsonArray.getInt(1) == 0xA2){
                            /*制作茶返回*/
                            int nowStage = messageJsonArray.getInt(3);
                            int height = messageJsonArray.getInt(4);
                            int low = messageJsonArray.getInt(5);
                        if (MakeActivity.isRunning) {
                            Intent mqttIntent = new Intent("MakeActivity");
                            mqttIntent.putExtra("nowStage", nowStage);
                            mqttIntent.putExtra("height", height);
                            mqttIntent.putExtra("low", low);
                            sendBroadcast(mqttIntent);
                        }
                        if (AddMethodActivity1.isRunning) {
                            Intent mqttIntent = new Intent("AddMethodActivity1");
                            mqttIntent.putExtra("nowStage", nowStage);
                            mqttIntent.putExtra("height", height);
                            mqttIntent.putExtra("low", low);
                            sendBroadcast(mqttIntent);
                        }

                    }else if (messageJsonArray != null && messageJsonArray.getInt(1) == 0xA3){
                        /*终止操作返回*/
                    }else if (messageJsonArray != null && messageJsonArray.getInt(1) == 0xA4){
                        /*开关机返回*/
                        boolean isFirst = equipment.getIsFirst()  ;//是否是默认设备
                        int mStage  = messageJsonArray.getInt(3);
                        equipment.setMStage(mStage);
                        if (isFirst){
                            if (MainActivity.isRunning){
                                Intent mqttIntent = new Intent("MainActivity");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                        }
                        if (EqumentFragment.isRunning) {
                            Intent mqttIntent = new Intent("EqumentFragment");
                            mqttIntent.putExtra("msg", macAddress);
                            mqttIntent.putExtra("msg1", equipment);
                            sendBroadcast(mqttIntent);
                        }


                    }else if (messageJsonArray != null && messageJsonArray.getInt(1) == 0xA6){
                        /*机器灯光返回*/
                    }
                    if ("reset".equals(topic)){
                        long  userId = preferences.getLong("userId",0) ;
                        Map<String,Object> params = new HashMap<>();
                        long id = -1;
                        if (equipment!=null){
                             id = equipment.getEqupmentId();
                             delEqupment = equipment;
                        }
                        params.put("id", id);
                        params.put("userId",userId);
                        new DeleteDeviceAsyncTask().execute(params);
                    }
                    if ("lwt".equals(topic)){
                        /*離綫主題*/
                        boolean isFirst = equipment.getIsFirst()  ;//是否是默认设备
                        equipment.setOnLine(false);
                        if (isFirst){
                            if (MainActivity.isRunning){
                                Intent mqttIntent = new Intent("MainActivity");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                            if (MakeActivity.isRunning){
                                Intent mqttIntent = new Intent("MakeActivity");
                                mqttIntent.putExtra("msg", macAddress);
                                mqttIntent.putExtra("msg1", equipment);
                                sendBroadcast(mqttIntent);
                            }
                            if (AddMethodActivity1.isRunning){
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



            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /*
     *
     *删除设备
     * */
    Equpment delEqupment = null;
    class DeleteDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/deleteDevice",params);
            Log.e("GGGG", "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");

                    } catch (JSONException e) {

                    }

                }else{
                    code="4000";
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case  "200":

                    equipmentDao.delete(delEqupment);
                    if (delEqupment.getIsFirst()){
                        delEqupment = null;
                        if (MainActivity.isRunning){
                            Intent mqttIntent = new Intent("MainActivity");
                            Equpment equpment = new Equpment();
                            mqttIntent.putExtra("msg", "");
                            mqttIntent.putExtra("msg1",delEqupment);
                            sendBroadcast(mqttIntent);
                        }
                    }

                    break;

                case  "4000":

                    break;

                default:

                    break;
            }
        }
    }

    public List<String> getTopicNames() {
        List<String> list = new ArrayList<>();
        List<Equpment> equpments = equipmentDao.findAll();

        for (Equpment equpment : equpments){
            String macAddress = equpment.getMacAdress();
            String onlineTopicName = "";
            String offlineTopicName = "";
            onlineTopicName = "tea/" + macAddress + "/status/transfer";
            offlineTopicName = "tea/" + macAddress + "/lwt";
            String s3 = "tea/" + macAddress + "/operate/transfer";
            String s4 = "tea/" + macAddress + "/extra/transfer";
            String s5 ="tea/" + macAddress + "/reset/transfer";
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
