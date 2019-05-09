package teabar.ph.com.teabar.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.QusetionActivity;
import teabar.ph.com.teabar.activity.SearchActivity;
import teabar.ph.com.teabar.adpter.MyViewPagerAdapter;
import teabar.ph.com.teabar.adpter.MyplanAdapter;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.adpter.TeaListAdapter;
import teabar.ph.com.teabar.adpter.WetherAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.TabItemBean;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.zxing.android.CaptureActivity;
import teabar.ph.com.teabar.view.WeatherLayoutManager;


public class MainFragment2 extends BaseFragment  {

    private ArrayList<View> views,views1;
    /**保存的选项卡的下标值*/
    private int savdCheckedIndex = 0;
    /**当前的选项卡的下标值*/
    private int mCurrentIndex = -1;
    /**保存的选项卡的下标值*/
    private int savdCheckedIndex1 = 0;
    /**当前的选项卡的下标值*/
    private int mCurrentIndex1 = -1;

    ViewPager view_pager_weather;
    private RecyclerView tv_tab_bz,rv_main_tealist;
    private ScrollView scrollView;
    RecyclerView rv_main_jh;
    RecyclerViewAdapter  recyclerViewAdapter2;
    private MyViewPagerAdapter pagerAdapter1;
    private ArrayList<TabItemBean> mTabItemBeanArrayList1;
    private WetherAdapter mWetherAdapter;
    private List<String> twoDataList;
    LinearLayout li_main_title ;
    TeaListAdapter teaListAdapter ;
    MyplanAdapter myplanAdapter ;
    List<Plan> planList = new ArrayList<>();
    List<Tea> list1 = new ArrayList<>();
    List<Tea> list2 = new ArrayList<>();
    List<Tea> list3 = new ArrayList<>();
    List<String> list = new ArrayList<>();
    boolean isOpen = false;
    ImageView iv_main_search,iv_main_ask,iv_main_sm;
    private static final int REQUEST_CODE_SCAN = 0x0000;
    public static boolean isRunning = false;

    private boolean MQBound;
    @BindView(R.id.tv_main_water)
    TextView tv_main_water;
    @BindView(R.id.tv_main_online)
    TextView tv_main_online;
    @BindView(R.id.tv_main_hot)
    TextView tv_main_hot;
    @BindView(R.id.tv_main_name)
    TextView tv_main_name;
    @BindView(R.id.tv_main_know)
    TextView tv_main_know;
    String firstMac;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    SharedPreferences preferences;
    long userId;
    @Override
    public int bindLayout() {
        return R.layout.fragment_main2;

    }

    @Override
    public void initView(View view) {
        if (TextUtils.isEmpty(tips)){
            new getTipsAsynTask().execute();//获取健康小知识
        }
        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
        equpments= equipmentDao.findAll();
        preferences = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
        String name = preferences.getString("userName","");
        userId = preferences.getLong("userId",0);
        tv_main_name.setText(name);
        scrollView =view. findViewById(R.id.scrollView);
        rv_main_jh = view.findViewById(R.id.rv_main_jh);
        rv_main_tealist = view.findViewById(R.id.rv_main_tealist);
        iv_main_sm = view.findViewById(R.id.iv_main_sm);
        iv_main_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //动态权限申请
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    goScan();
                }
            }
        });
        iv_main_ask = view.findViewById(R.id.iv_main_ask);
        iv_main_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),QusetionActivity.class));
            }
        });

        iv_main_search = view.findViewById(R.id.iv_main_search);
        iv_main_search .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SearchActivity.class));
            }
        });

        view_pager_weather = view.findViewById(R.id.view_pager_weather);
        li_main_title = view.findViewById(R.id.li_main_title);
        li_main_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen){
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                    firstEquipmentCtrl.open(1, firstMac );

                    isOpen=false;
                }else {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                    firstEquipmentCtrl.open(0, firstMac );

                    isOpen= true;
                }

            }
        });

        tv_tab_bz = view.findViewById(R.id.tv_tab_bz);
        //刷新默认设备
        RefrashFirstEqu1();

        //获取数据
        getData();
        initView1();
        initView2();
        //绑定services
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
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            Equpment msg1 =(Equpment)intent.getSerializableExtra("msg1");
            if (msg1.getIsFirst()){
                RefrashFirstEqu(msg1);
            }



        }
    }
    public void  RefrashFirstEqu1(){
       Equpment equpment =  ((MainActivity)getActivity()).getFirstEqu();
       if (equpment!=null){
           if ("2".equals(equpment.getErrorCode()) ){
               tv_main_water.setText(R.string.equ_xq_waters);
           }else {
               tv_main_water.setText(R.string.equ_xq_waterf);
           }
           tv_main_online.setText(R.string.equ_xq_online);
           if (equpment.getMStage()==1){
               tv_main_hot.setText(R.string.equ_xq_ishot);
           }else {
               tv_main_hot.setText(R.string.equ_xq_nohot);
           }
           if (equpment.getMStage()==5){
               li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
           }else {
               li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
           }

       }

    }

    public void  RefrashFirstEqu(Equpment equpment){

         if ("2".equals(equpment.getErrorCode()) ){
             tv_main_water.setText(R.string.equ_xq_waters);
         }else {
             tv_main_water.setText(R.string.equ_xq_waterf);
         }
        tv_main_online.setText(R.string.equ_xq_online);
         if (equpment.getMStage()==1){
             tv_main_hot.setText(R.string.equ_xq_ishot);
         }else {
             tv_main_hot.setText(R.string.equ_xq_nohot);
         }
        if (equpment.getMStage()==5){
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));

        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        firstEquipmentCtrl= ( FirstEquipmentCtrl) getActivity();
    }
    FirstEquipmentCtrl firstEquipmentCtrl;
    public  interface FirstEquipmentCtrl{
        void open(int type,String mac);
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
        if (!TextUtils.isEmpty(tips)){
            tv_main_know.setText(tips);//获取健康小知识
        }
    }

    private void initView1() {

        if (list.size()<3){
            for (int i= 0 ;i<3;i++){
                list.add(i+"");
            }
        }
        teaListAdapter = new TeaListAdapter(getActivity(),list,list1,list2,list3);
        teaListAdapter.update(list3,list2,list1);
        myplanAdapter = new MyplanAdapter(getActivity(),planList);
        if (planList.size()==0){
            new getAllPlanAsynTask().execute();
        }
        Log.e(TAG, "initView1: -->"+planList.size()+"...."+list1.size()+"...."+tips );
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(getActivity());
        rv_main_tealist.setLayoutManager(linearLayoutManager);
        rv_main_tealist.setAdapter(teaListAdapter);
        //解决滑动冲突、滑动不流畅
        rv_main_tealist.setHasFixedSize(true);
        rv_main_tealist.setNestedScrollingEnabled(false);
        rv_main_jh.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_main_jh.setAdapter(myplanAdapter);
        rv_main_jh.setHasFixedSize(true);
        rv_main_jh.setNestedScrollingEnabled(false);

    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning =false;
        Log.e(TAG, "onStop: -->" );
//        list.clear();
//        if (mWetherAdapter!=null)
//            mWetherAdapter=null;
//        recyclerViewAdapter2=null;

  }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MQBound) {
            getActivity().unbindService(MQconnection);
        }


    }

    //添加天气预报图
    private void initView2() {
        mTabItemBeanArrayList1 = new ArrayList<TabItemBean>();
        mTabItemBeanArrayList1.add(new TabItemBean(""," "));
        mTabItemBeanArrayList1.add(new TabItemBean(""," "));
        mTabItemBeanArrayList1.add(new TabItemBean(""," "));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        tv_tab_bz.setLayoutManager(linearLayoutManager);
        //设置适配器
//        if(mWetherAdapter == null){
            //设置适配器
            mWetherAdapter = new WetherAdapter(getActivity(), mTabItemBeanArrayList1);
            tv_tab_bz.setAdapter(mWetherAdapter);
            //添加分割线
            //设置添加删除动画

            tv_tab_bz.setSelected(true);
//


        //初始化ViewPager项布局
        views1 = new ArrayList<>();
        for (int i = 0; i < mTabItemBeanArrayList1.size(); i++) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weatherlist, null);
            final RecyclerView recycler_view2 = view1.findViewById(R.id.rv_weatherlist);
            recyclerViewAdapter2 = new RecyclerViewAdapter(getActivity(), R.layout.item_weather, twoDataList);
            final WeatherLayoutManager weatherLayoutManager=new WeatherLayoutManager(getActivity());
            recycler_view2.setLayoutManager(weatherLayoutManager);
            recycler_view2.setAdapter(recyclerViewAdapter2);
            views1.add(view1);
        }
        pagerAdapter1 = new MyViewPagerAdapter(views1);
        view_pager_weather.setAdapter(pagerAdapter1);
//列表适配器的点击监听事件
        mWetherAdapter.setOnItemClickLitener(new  WetherAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mCurrentIndex1 == position) {
                    return;
                }
                setFragmentDisplay2(position);//独立出来，用于OnResume的时候初始化展现相应的Fragment

                savdCheckedIndex1 = position;
                mCurrentIndex1 = position;
            }
        });
        view_pager_weather.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTabsDisplay2(position);
                savdCheckedIndex1 = position;
                mCurrentIndex1 = position;
            }

            @Override
            public void onPageSelected(int position) {
//                tv_tab.setText("tab" + (position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(list1.size()==0){
            new getTeaListAsynTask().execute();
        }

    }

    String returnMsg1,returnMsg2;
    /*  获取茶列表*/
    class getTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getTea?userId="+userId );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        list1.clear();
                        list2.clear();
                        list3.clear();
                        JSONObject jsonObject = new JSONObject(result);
//                        JsonObject content = new JsonParser().parse(result).getAsJsonObject();
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
//                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            JSONArray healingTea = jsonObject1.getJSONArray("healingTea");
                            Gson gson = new Gson();
                            for (int i = 0; i < healingTea.length(); i++) {
                                //功能茶
                                Tea teaList = gson.fromJson(healingTea.get(i).toString(),Tea.class);
                                list1.add(teaList);
                            }
                            JSONArray fruitTea = jsonObject1.getJSONArray("fruitTea");
                            for (int i = 0; i < fruitTea.length(); i++) {
                                //水果
                                Tea teaList = gson.fromJson(fruitTea.get(i).toString() ,Tea.class);
                                list2.add(teaList);
                            }
                            JSONArray herbalWellness = jsonObject1.getJSONArray("herbalWellness");
                            for (int i = 0; i < herbalWellness.length(); i++) {
                                // 草本
                                Tea teaList = gson.fromJson(herbalWellness.get(i).toString(),Tea.class);
                                list3.add(teaList);
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
                    teaListAdapter.update(list3,list2,list1);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:

                    ToastUtil.showShort(getActivity(), returnMsg1);
                    break;

            }
        }
    }

    /*  获取小知识*/
    String tips ;
    class getTipsAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getTips?type=1" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                      if ("200".equals(code)) {
                           tips = jsonObject1.getString("tips");

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
                    tv_main_know.setText(tips);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:
                    ToastUtil.showShort(getActivity(), returnMsg1);
                    break;

            }
        }
    }
/*  获取全部计划*/
    class getAllPlanAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getPlan" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONArray jsonArray =  jsonObject.getJSONArray("data");
                        if ("200".equals(code)) {
                            planList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //功能茶
                                Plan plan = gson.fromJson(jsonArray.get(i).toString(),Plan.class);
                                planList.add(plan);
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
                    myplanAdapter.update(planList);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:
                    ToastUtil.showShort(getActivity(), returnMsg1);
                    break;

            }
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    /**
     * 设置导航栏中选项卡之间的切换
     */
    private void setTabsDisplay(int index){
//        if(mTabAdapter != null){
//            mTabAdapter.setSelected(index);
//            tv_tab.smoothScrollToPosition(index);//平移滑动到指定position
//        }
    }



    /**
     * 设置天气的切换
     */
    private void setTabsDisplay2(int index){
        if(mWetherAdapter != null){
            mWetherAdapter.setSelected(index);
            tv_tab_bz.smoothScrollToPosition(index);//平移滑动到指定position
        }
    }


    /**
     * 设置碎片之间的切换
     */
    private void setFragmentDisplay2(int index){
        view_pager_weather.setCurrentItem(index, false);//smoothScroll false表示切换的时候,不经过两个页面的中间页
    }


    private void getData() {

        twoDataList = new ArrayList<>();
        twoDataList.add("B项目:");
    }

//    @OnClick({R.id.iv_main_search})
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.iv_main_search:
//
//                //动态权限申请
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
//                } else {
//                    goScan();
//                }
//                break;
//        }
//    }

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan(){
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(getActivity(), "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
//        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
//            if (data != null) {
                //返回的文本内容
//                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
//                if (content.contains("P99")){
//                    String IMEI= content.substring(content.indexOf(":")+1);
//
////                ToastUtil.showShort(this,content);
//                }else {
//                    ToastUtil.showShort(getActivity(),"请扫描正确的二维码");
//                }


//            }
//        }
    }

}
