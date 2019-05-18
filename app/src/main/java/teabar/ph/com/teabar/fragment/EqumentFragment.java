package teabar.ph.com.teabar.fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.EquipmentDetailsActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddDeviceActivity;
import teabar.ph.com.teabar.adpter.EqupmentAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;

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
                                type = 0xb0;
                            } else {
                                type = 0xb2;
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
            new  FirstAsynctask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    @SuppressLint("StaticFieldLeak")
    class  FirstAsynctask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            for (Equpment equpment:equpments){
                try {
                    Thread.sleep(500);
                    if (!ToastUtil.isEmpty(equpment.getMacAdress()))
                        MQservice.sendFindEqu(equpment.getMacAdress());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
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
            tv_main_online.setText(R.string.equ_xq_online);
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
        if (equpment!=null){
            String error = equpment.getErrorCode();
            String[] aa = error.split(",");
            if ("1".equals(aa[6])) {
            tv_main_water.setText(R.string.equ_xq_waters);
              }else {
            tv_main_water.setText(R.string.equ_xq_waterf);
              }
        tv_main_online.setText(R.string.equ_xq_online);
        if (equpment.getMStage()==0xb1){
            tv_main_hot.setText(R.string.equ_xq_ishot);
        }else {
            tv_main_hot.setText(R.string.equ_xq_nohot);
        }
        if (equpment.getMStage()==0xb2){
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
        }
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            FirEqupment =null;

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
                            if (FirEqupment.getMStage()!=0xb2) {
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
            if (reset==2){
                equpments = equipmentDao.findAll();
                equpmentAdapter.setEqumentData1(equpments);
            }
            if (msg1.getMacAdress().equals(FirEqupment.getMacAdress())){
                FirEqupment = msg1;
            }
            RefrashAllEqu(msg,msg1);


        }
    }
     /*  刷新全部设备*/
    public void  RefrashAllEqu(String macAdress, Equpment equpment){
        equpmentAdapter.setEqumentData(macAdress,equpment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (     requestCode== RESULT_SPEECH&& data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ToastUtil.showShort(getActivity(),result.get(0));
            Log.e(TAG, "onActivityResult: zzz-->"+result.get(0) );
        }
        if (resultCode==2000){
            RefrashChooseEqu();
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
