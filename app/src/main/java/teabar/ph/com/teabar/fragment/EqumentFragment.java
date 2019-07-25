package teabar.ph.com.teabar.fragment;

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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

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
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.WaveProgress;

/*弃用请看EqumentFragment2*/
public class EqumentFragment extends BaseFragment {
//    @BindView(R.id.rv_equment)
//    RecyclerView rv_equment;
protected static final int RESULT_SPEECH = 1;
    List<Equpment> equpments;
    EqupmentAdapter equpmentAdapter;
    public static boolean isRunning = false;
    MessageReceiver receiver;

    @BindView(R.id.tv_main_water)
    TextView tv_main_water;
    @BindView(R.id.tv_main_online)
    TextView tv_main_online;
    @BindView(R.id.tv_main_hot)
    TextView tv_main_hot;
    @BindView(R.id. li_main_title)
    LinearLayout li_main_title ;
    EquipmentImpl equipmentDao;
    Equpment FirEqupment;
    String firstMac;/*需要处理 */

    private boolean MQBound;
    @Override
    public int bindLayout() {
        return R.layout.fragment_equpment;
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
        equpmentAdapter.setHasStableIds(true);
        rv_equment.setLayoutManager( new GridLayoutManager(getActivity(),2));
//        rv_equment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rv_equment.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new EqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    Equpment equpment = equpmentAdapter.getmData().get(position);
                    Intent intent = new Intent(getActivity(),EquipmentDetailsActivity.class);
                    intent.putExtra("equment",equpment);
                    startActivityForResult(intent,6000);
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
                                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                            } else {
                                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                            }
                        }

                }
            }
        });
        equpmentAdapter.SetlightItemClick(new EqupmentAdapter.OnlightClickListener() {
            @Override
            public void onItemClick(View view, int position, int b) {
                    MQservice.sendLightOpen(equpmentAdapter.getmData().get(position).getMacAdress(),b);
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
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        equipmentCtrl= (EquipmentCtrl) getActivity();
    }
    EquipmentCtrl equipmentCtrl;
    public  interface EquipmentCtrl{
        void open1(int type,String mac);
    }
    boolean Open = true;
    //刷新全部设备
    public void RefrashChooseEqu(){
        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
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
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                }
            }
        }
        equpmentAdapter.setEqumentData1(equpments);
    }

    public void  RefrashFirstEqu1(){
        Equpment equpment =  ((MainActivity)getActivity()).getFirstEqu();
        if (equpment!=null){
            String error = equpment.getErrorCode();
            if (!TextUtils.isEmpty(error)) {
                String[] aa = error.split(",");
                if ("1".equals(aa[6])) {
                    tv_main_water.setText(R.string.equ_xq_waters);
                } else {
                    tv_main_water.setText(R.string.equ_xq_waterf);
                }
            }
            if (equpment.getOnLine())
                tv_main_online.setText(R.string.equ_xq_online);
            else
                tv_main_online.setText(R.string.equ_xq_outline);
            if (equpment.getMStage()==1){
                tv_main_hot.setText(R.string.equ_xq_ishot);
            }else {
                tv_main_hot.setText(R.string.equ_xq_nohot);
            }
            if (equpment.getMStage()==0xb2||equpment.getMStage()==-1){
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            }else {
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
            }

        }

    }

    public void  RefrashFirstEqu(Equpment equpment){
        if (equpment!=null) {
            String error = equpment.getErrorCode();
            if (error.length() > 5) {
                String[] aa = error.split(",");
                if ("1".equals(aa[6])) {
                    tv_main_water.setText(R.string.equ_xq_waters);
                } else {
                    tv_main_water.setText(R.string.equ_xq_waterf);
                }
                if (equpment.getOnLine())
                tv_main_online.setText(R.string.equ_xq_online);
                else
                    tv_main_online.setText(R.string.equ_xq_outline);
                if (equpment.getMStage() == 0xb1) {
                    tv_main_hot.setText(R.string.equ_xq_ishot);
                } else {
                    tv_main_hot.setText(R.string.equ_xq_nohot);
                }
                if (equpment.getMStage() == 0xb2) {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                } else {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                }
            } else {
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
                List<Equpment> equpments = equipmentDao.findAll();
                equpmentAdapter.setEqumentData1(equpments);
            }
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
            List<Equpment> equpments = equipmentDao.findAll();
            equpmentAdapter.setEqumentData1(equpments);
            Log.e(TAG, "RefrashFirstEqu: ---》"+equpments.size()+"...." );
        }
    }


    @OnClick({R.id.iv_equ_add ,R.id.li_main_title,R.id.iv_equ_yy})
    public  void onClick(View view){
        switch (view.getId()) {
            case R.id.iv_equ_add:
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                startActivityForResult(intent, 4000);
                break;

            case R.id.li_main_title:
                if (((MainActivity) getActivity()).getFirstEqu()!=null) {
                    if (((MainActivity) getActivity()).getFirstEqument().getOnLine()) {
                        if (!Utils.isFastClick()) {
                            FirEqupment = ((MainActivity) getActivity()).getFirstEqu();
                            if (FirEqupment.getMStage()!=0xb6||FirEqupment.getMStage()!=0xb7) {
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
            int size = height*256 +low;
            if (reset==2){
                equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
                equpments = equipmentDao.findAll();
                equpmentAdapter.setEqumentData1(equpments);
            }
            if (FirEqupment!=null&&msg1!=null) {
                if (msg1.getMacAdress().equals(FirEqupment.getMacAdress())) {
                    FirEqupment = msg1;
                }
                RefrashAllEqu(msg, msg1);
            }
            Log.e(TAG, "onReceive: -->"+nowStage );
            if (nowStage==0xb4||nowStage==0xb3||nowStage==0xb5) {
                if (IsMakeing == 1) {
                    if (waterView != null) {
                        float value = 100 * size / makeNum;
                        if ((int) value != 0) {
                            waterView.setValue(value);
                            tv_number.setText((int) value + "");
                        }
                        if (nowStage == 0xb5) {
                            searchThread.stopThread();
                            waterView.setValue(110f);
                            tv_make_title.setText(R.string.equ_xq_cpwc);

                            tv_number.setText(100 + "");
                            li_make_finish.setVisibility(View.VISIBLE);
                            bt_view_stop.setVisibility(View.GONE);
                            dialog1.setCanceledOnTouchOutside(true);
                        }
                    }
                }
            }

        }
    }
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
            if (result!=null&&result.size()>0){
                for (int i = 0 ;i<result.size();i++) {
                    String text = result.get(i).trim();
                    if (!Utils.checkcountname(text)){
                        text = Utils.convertString(text,false);
                    }
                    if ((text.contains(getActivity().getText(R.string.drink_makebig)))&&(text.contains(getActivity().getText(R.string.drink_cup3)))) {
                        if (FirEqupment!=null) {
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
                                if (FirEqupment.getMStage() == 0xb1) {
                                    customDialog(220);
                                    makeNum = 220;
                                } else {
                                    ToastUtil.showShort(getActivity(), getResources().getText(R.string.toast_make_make).toString());
                                }
                            }{
                                ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_error).toString());
                            }
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    } else if ((text.contains(getActivity().getText(R.string.drink_makebig)))&&(text.contains(getActivity().getText(R.string.drink_cup2)))) {
                        if (FirEqupment!=null) {
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
                                if (FirEqupment.getMStage() == 0xb1) {
                                    customDialog(140);
                                    makeNum = 140;
                                } else {
                                    ToastUtil.showShort(getActivity(), getResources().getText(R.string.toast_make_make).toString());
                                }
                            }{
                                ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_error).toString());
                            }
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    } else if (text.contains(getActivity().getText(R.string.drink_sm))) {
                        if (FirEqupment!=null) {
                            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                            equipmentCtrl.open1(0Xc0, firstMac);
                            FirEqupment.setMStage(0xb2);
                            RefrashAllEqu(FirEqupment.getMacAdress(), FirEqupment);
                        }else {
                            ToastUtil.showShort(getActivity(),getText(R.string.toast_equ_add).toString());
                        }
                        break;
                    } else {
                        ToastUtil.showShort(getActivity(), Objects.requireNonNull(getActivity()).getText(R.string.drink_no).toString());
                    }
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
    CountDownTimer countDownTimer;
    int IsMakeing = 0;
    TextView tv_make_title;
    TextView tv_number;
    LinearLayout li_make_finish;
    Button bt_view_stop,bt_view_sc,bt_view_fx;

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
        tv_make_title.setText(R.string.equ_xq_jpz);

        waterView = (WaveProgress) view.findViewById(R.id.waterView);
        waterView.setWaveColor(Color.parseColor("#37dbc2"), Color.parseColor("#81fbe6"));
        waterView.setMaxValue(100f);
//        waterView.setValue(100f );
//        MQservice.sendMakeMess(num,15,90,firstMac);
        waterView.setValue(50f);
        long time =15;
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
            getActivity().unbindService(MQconnection);
        }
        if (receiver != null) {
            getActivity(). unregisterReceiver(receiver);
        }

    }


    //
//    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
//
//
//        private final int[] ATTRS = new int[]{
//                android.R.attr.listDivider
//        };
//
//        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
//
//        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
//
//        private Drawable mDivider;
//
//        private int mOrientation;
//
//        Paint paint ;
//        public DividerItemDecoration(Context context, int orientation) {
//            final TypedArray a = context.obtainStyledAttributes(ATTRS);
//            mDivider = a.getDrawable(0);
//            a.recycle();
//            setOrientation(orientation);
//
//        }
//
//        public void setOrientation(int orientation) {
//            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
//                throw new IllegalArgumentException("invalid orientation");
//            }
//            mOrientation = orientation;
//        }
//
//        @Override
//        public void onDraw(Canvas c, RecyclerView parent) {
////        Log.v("recyclerview - itemdecoration", "onDraw()");
//
//            if (mOrientation == VERTICAL_LIST) {
////                drawVertical(c, parent);
//            } else {
////                drawHorizontal(c, parent);
//            }
//        }
//
//
//        public void drawVertical(Canvas c, RecyclerView parent) {
//            final int left = parent.getPaddingLeft();
//            final int right = parent.getWidth() - parent.getPaddingRight();
//
//            final int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                final View child = parent.getChildAt(i);
//                RecyclerView v = new RecyclerView(parent.getContext());
//                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                        .getLayoutParams();
//                final int top = child.getBottom() + params.bottomMargin;
//                final int bottom = top + mDivider.getIntrinsicHeight();
//
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);
//
//
//            }
//        }
//
//        public void drawHorizontal(Canvas c, RecyclerView parent) {
//            final int top = parent.getPaddingTop();
//            final int bottom = parent.getHeight() - parent.getPaddingBottom();
//
//            final int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                final View child = parent.getChildAt(i);
//                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                        .getLayoutParams();
//                final int left = child.getRight() + params.rightMargin;
//                final int right = left + mDivider.getIntrinsicHeight();
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);
//            }
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
//            int itemCount = parent.getLayoutManager().getItemCount();
//            if (mOrientation == VERTICAL_LIST) {
//
//                if (itemPosition==itemCount-1){
//                    outRect.set(0, 0, 0, 240);
//                }else {
//                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
//                }
//            } else {
//                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//            }
//        }
//    }
}
