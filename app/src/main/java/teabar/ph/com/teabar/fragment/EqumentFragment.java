package teabar.ph.com.teabar.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

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

public class EqumentFragment extends BaseFragment {
//    @BindView(R.id.rv_equment)
//    RecyclerView rv_equment;
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
    String firstMac;
    boolean isOpen = false;
    @Override
    public int bindLayout() {
        return R.layout.fragment_equpment;
    }

    @Override
    public void initView(View view) {

        RecyclerView rv_equment = view.findViewById(R.id.rv_equment);
        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
        equpments= equipmentDao.findAll();
        for (int i = 0;i<equpments.size();i++){
            if (equpments.get(i).getIsFirst()){
                firstMac = equpments.get(i).getMacAdress();
                FirEqupment = equpments.get(i);
            }
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
                String mac = equpmentAdapter.getmData().get(position).getMacAdress();
                int type;
                 if (equipmentCtrl!=null){
                     if (b){
                         type=0;
                     }else {
                         type=1;
                     }
                     equipmentCtrl.open(type,mac);
                 }
                 if (equpmentAdapter.getmData().get(position).getIsFirst()){
                     if (equpmentAdapter.getmData().get(position).getMStage()==0){
                         li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                     }else {
                         li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                     }
                 }
            }
        });

        //刷新默认设备
        RefrashFirstEqu1();
        IntentFilter intentFilter = new IntentFilter("EqumentFragment");
        receiver = new MessageReceiver();
        getActivity().registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning =true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        equipmentCtrl= (EquipmentCtrl) getActivity();
    }
    EquipmentCtrl equipmentCtrl;
    public  interface EquipmentCtrl{
        void open(int type,String mac);
    }

    //刷新全部设备
    public void RefrashChooseEqu(){
        equpments = equipmentDao.findAll();
        equpmentAdapter.setEqumentData1(equpments);
    }

    public void  RefrashFirstEqu1(){
        Equpment equpment =  ((MainActivity)getActivity()).getFirstEqu();
        if (equpment!=null){
            if ("2".equals(equpment.getErrorCode()) ){
                tv_main_water.setText("不足");
            }else {
                tv_main_water.setText("充足");
            }
            tv_main_online.setText("在线");
            if (equpment.getMStage()==1){
                tv_main_hot.setText("已经预热");
            }else {
                tv_main_hot.setText("未预热");
            }
            if (equpment.getMStage()==0){
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            }else {
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
            }

        }

    }

    public void  RefrashFirstEqu(Equpment equpment){

        if ("2".equals(equpment.getErrorCode()) ){
            tv_main_water.setText("不足");
        }else {
            tv_main_water.setText("充足");
        }
        tv_main_online.setText("在线");
        if (equpment.getMStage()==1){
            tv_main_hot.setText("已经预热");
        }else {
            tv_main_hot.setText("未预热");
        }
        if (equpment.getMStage()==0){
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
        }

    }


    @OnClick({R.id.iv_equ_add ,R.id.li_main_title})
    public  void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_add:
                Intent intent = new Intent(getActivity(),AddDeviceActivity.class);
                startActivityForResult(intent,4000);
                break;

            case R.id.li_main_title:
                if (isOpen){
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                    equipmentCtrl.open(1, firstMac );

                    FirEqupment.setMStage(0);
                    RefrashAllEqu(FirEqupment.getMacAdress(),FirEqupment);
                    isOpen=false;
                }else {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                    equipmentCtrl.open(0,firstMac );

                    FirEqupment.setMStage(2);
                    RefrashAllEqu(FirEqupment.getMacAdress(),FirEqupment);
                    isOpen= true;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (receiver != null) {
            getActivity(). unregisterReceiver(receiver);
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            Equpment msg1 =(Equpment)intent.getSerializableExtra("msg1");
            RefrashAllEqu(msg,msg1);


        }
    }
     /*  刷新全部设备*/
    public void  RefrashAllEqu(String macAdress, Equpment equpment){
        equpmentAdapter.setEqumentData(macAdress,equpment);
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
