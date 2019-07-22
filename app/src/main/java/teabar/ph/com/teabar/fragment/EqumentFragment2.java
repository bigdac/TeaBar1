package teabar.ph.com.teabar.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddDeviceActivity;
import teabar.ph.com.teabar.activity.device.EquipmentDetailsActivity;
import teabar.ph.com.teabar.adpter.EqupmentAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.MyDecoration;
import teabar.ph.com.teabar.util.NetWorkUtil;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.WaveProgress;

public class EqumentFragment2 extends BaseFragment {
//    @BindView(R.id.rv_equment)
//    RecyclerView rv_equment;
protected static final int RESULT_SPEECH = 1;
    List<Equpment> equpments;
    EqupmentAdapter equpmentAdapter;
    public static boolean isRunning = false;
    MessageReceiver receiver;

    @BindView(R.id.tv_main_online)
    TextView tv_main_online;
    @BindView(R.id.tv_main_hot)
    TextView tv_main_hot;
    @BindView(R.id. li_main_title)
    RelativeLayout li_main_title ;
    @BindView(R.id.iv_main_error)
    ImageView iv_main_error;
    @BindView(R.id.tv_main_error)
    TextView tv_main_error;
    @BindView(R.id.iv_main_online)
    ImageView iv_main_online;
    @BindView(R.id.refreshLayout_xq)
    RefreshLayout refreshLayou;
    EquipmentImpl equipmentDao;
    Equpment FirEqupment;
    String firstMac;/*需要处理 */

    private boolean MQBound;
    @Override
    public int bindLayout() {
        return R.layout.fragment_equpment2;
    }
    private float getDimen(int dimenId) {
        return getResources().getDimension(dimenId);
    }
    @Override
    public void initView(View view) {
        isRunning =true;
        RecyclerView rv_equment = view.findViewById(R.id.rv_equment);
        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
        equpments = equipmentDao.findAll();
        FirEqupment = ((MainActivity)getActivity()).getFirstEqument() ;
        if (FirEqupment!=null){
            firstMac = FirEqupment.getMacAdress();
        }
        equpmentAdapter = new EqupmentAdapter(getActivity(),equpments);
        rv_equment.setLayoutManager( new GridLayoutManager(getActivity(),2));
        rv_equment.addItemDecoration(new MyDecoration().setMargin((int)getDimen(R.dimen.Devicespace)));
//        rv_equment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rv_equment.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new EqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position<equpmentAdapter.getItemCount()-1){
                    Equpment equpment = equpmentAdapter.getmData().get(position);
                    Intent intent = new Intent(getActivity(),EquipmentDetailsActivity.class);
                    intent.putExtra("equment",equpment);
                    startActivityForResult(intent,6000);
                 }else {
                    Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                    startActivityForResult(intent, 4000);
                }
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
        equpmentAdapter.SetopenItemClick(new EqupmentAdapter.OnopenClickListener() {
            @Override
            public void onItemClick(View view, int position, boolean b) {
                if (equpmentAdapter.getmData().get(position).getOnLine()){
                        String mac = equpmentAdapter.getmData().get(position).getMacAdress();
                        int mStage = equpmentAdapter.getmData().get(position).getMStage();
                        if (mStage!=0xb6&&mStage!=0xb7) {
                            int type;
                            if (equipmentCtrl != null) {
                                if (b) {
                                    type = 0xc1;
                                } else {
                                    type = 0xc0;
                                }
                                /*開關機 0開 1 關*/
                                equipmentCtrl.open1(type, mac);
                            }
                            if (equpmentAdapter.getmData().get(position).getIsFirst()) {
                                /*正逻辑 0操作是关闭的*/
                                if (equpmentAdapter.getmData().get(position).getMStage() == 0xb0) {
                                    li_main_title.setBackgroundResource(R.drawable.main_title);
                                } else {
                                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                                }
                            }
                        }else {
                            ToastUtil.showShort(getActivity(), getText(R.string.toast_updata_no).toString());
                        }
                }
            }
        });
        /*灯的开关*/
//        equpmentAdapter.SetlightItemClick(new EqupmentAdapter.OnlightClickListener() {
//            @Override
//            public void onItemClick(View view, int position, int b) {
////                    MQservice.sendLightOpen(equpmentAdapter.getmData().get(position).getMacAdress(),b);
//            }
//        });

        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
                    new  FirstAsynctask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {

                }

            }

        });
        //刷新默认设备
        RefrashFirstEqu1();
        IntentFilter intentFilter = new IntentFilter("EqumentFragment");
        receiver = new MessageReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
        MQintent = new Intent(getActivity(), MQService.class);
        MQBound = getActivity().bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
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
    public void onStart() {
        super.onStart();
        isRunning =true;

    }
    @SuppressLint("StaticFieldLeak")
    class  FirstAsynctask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MQservice.loadAlltopic();
            for (Equpment equpment:equpmentAdapter.getmData()){
                try {
                    Thread.sleep(500);
                    if (!TextUtils.isEmpty(equpment.getMacAdress())){
                 if (equpment.getMStage()==0xb6||equpment.getMStage()==0xb7){

                        }else{
                            MQservice.sendFindEqu(equpment.getMacAdress());
                        }


                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            refreshLayou.finishRefresh(true);
            return null;
        }
    }
    /*兩個默認設備同步*/
    public void  Synchronization(int type){
        //0是开机 1是关机 这里是显示 0 为绿色
        if (type==0xb2){

            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            FirEqupment.setMStage(0xb0);
            RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
        }else {
            FirEqupment.setMStage(0xb2);
            RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
            li_main_title.setBackgroundResource(R.drawable.main_title);

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        equipmentCtrl= (EquipmentCtrl) getActivity();
    }
    EquipmentCtrl equipmentCtrl;
    public  interface EquipmentCtrl{
        void open1(int type, String mac);
    }
    boolean Open = true;
    //刷新全部设备
    public void RefrashChooseEqu(){
//        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
        List<Equpment> equpments = equipmentDao.findAll();

        for (int i = 0;i<equpments.size();i++){
            if (  equpments.get(i).getIsFirst()){
                ((MainActivity)getActivity()).setFirstEqument(equpments.get(i));
                FirEqupment = equpments.get(i);
                firstMac = FirEqupment.getMacAdress();
                switch (FirEqupment.getMStage()){
                    case 0xb2:
                        Open = false;

                        break;
                    case -1:
                        Open = false;
                        break;
                    default:
                        Open=true;
                        break;

                }
                if (!Open ){
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));

                }else {
                    li_main_title.setBackgroundResource(R.drawable.main_title);
                }
            }
        }
        equpmentAdapter.setEqumentData1(equpments);
    }
    String errorMes;
    public void  RefrashFirstEqu1(){
        tv_main_error.setVisibility(View.GONE);
        iv_main_error.setVisibility(View.GONE);
        tv_main_hot.setVisibility(View.GONE);
        if ( ((MainActivity)Objects.requireNonNull(getActivity())).getFirstEqu()!=null) {
            Equpment equpment = ((MainActivity) getActivity()).getFirstEqu();
            if (equpment != null) {
                String error = equpment.getErrorCode();
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
                if (equpment.getOnLine()) {
                    tv_main_online.setText(R.string.equ_xq_online);
                    iv_main_online.setImageResource(R.mipmap.main_online3);
                    tv_main_hot.setVisibility(View.VISIBLE);
                }
                else {
                    iv_main_online.setImageResource(R.mipmap.main_outline3);
                    tv_main_online.setText(R.string.equ_xq_outline);
                }
                if (equpment.getMStage() == 0xb0) {
                    if (equpment.getHotFinish()==0)
                    tv_main_hot.setText(R.string.equ_xq_nohot);
                    else
                    tv_main_hot.setText(R.string.equ_xq_ishot);
                }else if (equpment.getMStage() == 0xb2) {
                    tv_main_hot.setText(R.string.equ_xq_dg);
                }else if (equpment.getMStage() == 0xb3) {
                    tv_main_hot.setText(R.string.equ_xq_jpz);
                }else if (equpment.getMStage() == 0xb4) {
                    tv_main_hot.setText(R.string.equ_xq_cpz);
                }else if (equpment.getMStage() == 0xb5) {
                    tv_main_hot.setText(R.string.equ_xq_cpwc);
                }else if (equpment.getMStage() == 0xc6) {
                    tv_main_hot.setText(R.string.equ_xq_qx);
                }else if (equpment.getMStage() == 0xb6||equpment.getMStage() == 0xb7) {
                    tv_main_hot.setText(R.string.equ_xq_sj);
                }
                else if (equpment.getMStage() == 0xc7) {
                    tv_main_hot.setText(R.string.equ_xq_jb);
                }
                if (equpment.getMStage() == 0xb2 || equpment.getMStage() == -1) {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));

                } else {
                    li_main_title.setBackgroundResource(R.drawable.main_title);

                }

            }
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            iv_main_online.setImageResource(R.mipmap.main_outline3);
            equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
            List<Equpment> equpments = equipmentDao.findAll();
            equpmentAdapter.setEqumentData1(equpments);
            FirEqupment = null;
            firstMac = "";

        }

    }


    @OnClick({ R.id.li_main_title,R.id.iv_equ_yy})
    public  void onClick(View view){
        switch (view.getId()) {

            case R.id.li_main_title:
                if (((MainActivity) getActivity()).getFirstEqu()!=null) {
                    if (((MainActivity) getActivity()).getFirstEqument().getOnLine()) {
                        if (!Utils.isFastClick()) {
                            FirEqupment = ((MainActivity) getActivity()).getFirstEqu();
                            if (FirEqupment.getMStage()!=0xb6&&FirEqupment.getMStage()!=0xb7) {
                                if (FirEqupment.getMStage() != 0xb2) {
                                     li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                                    equipmentCtrl.open1(0Xc0, firstMac);
                                    FirEqupment.setMStage(0xb2);
                                    RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
                                } else {
                                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                                    equipmentCtrl.open1(0Xc1, firstMac);
                                    FirEqupment.setMStage(0xb0);
                                    RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
                                }
                            }else {
                                ToastUtil.showShort(getActivity(), getText(R.string.toast_updata_no).toString());
                            }
                        }else {
                            ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_fast).toString());
                        }
                    }else {
                        ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_online).toString());
                    }

                }else {
                    ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_add).toString());
                }

                break;

            case R.id.iv_equ_yy:
                Intent intent1 = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent1, RESULT_SPEECH);
//                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }

                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }


    @Override
    public void widgetClick(View v) {

    }


    @Override
    public void onStop() {
        super.onStop();
        isRunning = false;
    }



    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            Equpment msg1 =(Equpment)intent.getSerializableExtra("msg1");
            int reset =  intent.getIntExtra("reset",0);
            int nowStage = intent.getIntExtra("nowStage",-1);
            int height = intent.getIntExtra("height",0);
            int low = intent.getIntExtra("low",0);
            String errorCode = intent.getStringExtra("errorCode");
            int size = height*256 +low;
            if (reset==2){
//                equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
                equpments = equipmentDao.findAll();
                equpmentAdapter.setEqumentData1(equpments);
            }
            if (FirEqupment!=null &&msg1!=null ) {
                if (msg1.getMacAdress().equals(FirEqupment.getMacAdress())) {
                    FirEqupment = msg1;
                    RefrashAllEqu(msg, msg1);
                }

            }
            if (msg1!=null){
                RefrashAllEqu(msg, msg1);
            }
            if (!TextUtils.isEmpty(errorCode)) {

                    if (errorCode.contains("1")) {
                        if (dialog1 != null && dialog1.isShowing()) {
                            dialog1.dismiss();
                        }
                        if (searchThread!=null){
                            searchThread.stopThread();

                        }
                }
            }
            Log.e(TAG, "onReceive: -->"+nowStage );
            if (nowStage!=-1) {
                if (IsMakeing == 1 || nowStage == 0xb8  ) {

                    Message message = new Message();
                    message.arg1 = size;
                    message.arg2 = nowStage;
                    handler.sendMessage(message);
                }
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
            if (waterView != null) {
                float value = 100 * size / makeNum;
                if ((int) value != 0) {
                    waterView.setValue(value);
                    tv_number.setText((int) value + "");
                }
                if (nowStage == 0xb5) {
                    searchThread.stopThread();
                    waterView.setValue(150f);
                    tv_make_title.setText(R.string.equ_xq_cpwc);
                    tv_number.setText(100 + "");
                    tv_full.setVisibility(View.VISIBLE);
                    li_make_finish.setVisibility(View.VISIBLE);
                    bt_view_stop.setVisibility(View.GONE);

                }

            }
            if (nowStage==0xb8){
                if (searchThread!=null){
                    searchThread.stopThread();

                }
                if (dialog1!=null&&dialog1.isShowing()){
                    dialog1.dismiss();
                }
            }
            if (nowStage!=-1){
                if (nowStage != 0xb3 && nowStage != 0xb4 && nowStage != 0xb5) {
                    if (tv_number!=null ) {
                        if (!"100".equals(tv_number.getText().toString().trim())) {
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

        }
    };

     /*  刷新全部设备*/
    public void  RefrashAllEqu(String macAdress, Equpment equpment){
        equpmentAdapter.setEqumentData(macAdress,equpment);
    }
    int makeNum;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== RESULT_SPEECH&& data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            boolean isRight = false;
            if (result!=null&&result.size()>0){
                for (int i = 0 ;i<result.size();i++) {
                    String text = result.get(i).trim();
                    if (!Utils.checkcountname(text)){
                        text = Utils.convertString(text,false);
                    }
                    if ((text.contains("大杯"))&&(text.contains("冲泡"))||(text.contains("brew"))&&(text.contains("big"))) {
                        if (FirEqupment!=null) {
                            isRight = true;
                            String wrongCode = FirEqupment.getErrorCode();
                            boolean errorcode = false;
                            if (!TextUtils.isEmpty(wrongCode)) {
                                String[] aa = wrongCode.split(",");
                                for (int j = 0; j < aa.length; j++) {
                                    if ("1".equals(aa[j])) {
                                        errorcode = true;
                                    }
                                }
                            }
                            if (!errorcode) {
                                if (FirEqupment.getHotFinish() == 1) {
                                    customDialog(220);
                                    makeNum = 220;
                                } else {
                                    ToastUtil.showShort(getActivity(), getResources().getText(R.string.toast_make_make).toString());
                                }
                            }else {
                                ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_error).toString());
                            }
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    } else if ((text.contains("冲泡"))&&(text.contains("小杯"))||(text.contains("brew"))&&(text.contains("small"))) {
                        if (FirEqupment!=null) {
                            isRight = true;
                            String wrongCode = FirEqupment.getErrorCode();
                            boolean errorcode = false;
                            if (!TextUtils.isEmpty(wrongCode)) {
                                String[] aa = wrongCode.split(",");
                                for (int j = 0; j < aa.length; j++) {
                                    if ("1".equals(aa[j])) {
                                        errorcode = true;
                                    }
                                }
                            }
                            if (!errorcode) {
                                if (FirEqupment.getHotFinish() == 1) {
                                    customDialog(140);
                                    makeNum = 140;
                                } else {
                                    ToastUtil.showShort(getActivity(), getResources().getText(R.string.toast_make_make).toString());
                                }
                            }else{
                                ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_error).toString());
                            }
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    } else if (text.contains("睡眠")||text.contains("sleep")) {
                        if (FirEqupment!=null) {
                            isRight =true;
                            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                            equipmentCtrl.open1(0Xc0, firstMac);
                            FirEqupment.setMStage(0xb2);
                            RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    }
                }
                if (!isRight){
                    ToastUtil.showShort(getActivity(),getText(R.string.drink_no).toString());
                }
            }
//            ToastUtil.showShort(getActivity(),result.get(0));
//            Log.e(TAG, "onActivityResult: zzz-->"+result.get(0) );
        }
        if (resultCode==2000){
            RefrashChooseEqu();
        }
    }





    WaveProgress waterView;
    Dialog dialog1;
    CountDownTimer countDownTimer,countDownTimer1;
    int IsMakeing = 0;
    TextView tv_make_title,tv_full,tv_brew_zb;
    TextView tv_number;
    LinearLayout li_make_finish;
    Button bt_view_stop,bt_view_sc,bt_view_fx;
    RelativeLayout rl_brew_time;
    private void customDialog(int num) {
        IsMakeing =0;
        dialog1  = new Dialog(getActivity(), R.style.MyDialog);
        View view = View.inflate(getActivity(), R.layout.view_make, null);
        bt_view_stop = view.findViewById(R.id.bt_view_stop);
        tv_number = view.findViewById(R.id.tv_number);
        final TextView tv_units = view.findViewById(R.id.tv_units);
        tv_make_title = view.findViewById(R.id.tv_make_title);
        li_make_finish = view.findViewById(R.id.li_make_finish);
        bt_view_sc = view.findViewById(R.id.bt_view_sc);
        bt_view_fx = view.findViewById(R.id.bt_view_fx);
        bt_view_fx.setVisibility(View.GONE);
        tv_brew_zb = view.findViewById(R.id.tv_brew_zb);
        rl_brew_time = view.findViewById(R.id.rl_brew_time);
        tv_full = view .findViewById(R.id.tv_full);
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
        Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            iv_dialog_move.startAnimation(operatingAnim);
        }  else {
            iv_dialog_move.setAnimation(operatingAnim);
            iv_dialog_move.startAnimation(operatingAnim);
        }


        tv_make_title.setText(R.string.equ_xq_jpz);
        countDownTimer1 =  new CountDownTimer(3000,1000) {
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


//        waterView.setValue(100f );
        MQservice.sendMakeMess(num,15,90,firstMac,255,66,11);
//        waterView.setValue(50f);
        long time =15;
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

        dialog1.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog1.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.75f);
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
                        MQservice.sendSearchML(FirEqupment.getMacAdress());
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
    public void onDestroyView() {
        super.onDestroyView();
        if (MQBound) {
            Objects.requireNonNull(getActivity()).unbindService(MQconnection);
        }
        if (receiver != null) {
            Objects.requireNonNull(getActivity()). unregisterReceiver(receiver);
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距


            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %2==0) {
                outRect.set(space,0,space,0);
            }else {
                outRect.set(0,0,space,0);
            }

        }

    }


}
