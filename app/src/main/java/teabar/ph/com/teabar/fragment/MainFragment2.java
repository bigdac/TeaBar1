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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.QusetionActivity;
import teabar.ph.com.teabar.activity.SearchActivity;
import teabar.ph.com.teabar.adpter.BaseRecyclerAdapter;
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
import teabar.ph.com.teabar.pojo.Weather;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
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
    RecyclerViewAdapter  recyclerViewAdapter1;
    private MyViewPagerAdapter pagerAdapter1;
    private ArrayList<TabItemBean> mTabItemBeanArrayList1;
    private WetherAdapter mWetherAdapter;
    private List<Weather> twoDataList;
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
    @BindView(R.id.tv_main_bz1)
    TextView tv_main_bz1;
    String firstMac;
    EquipmentImpl equipmentDao;
    List<Equpment> equpments;
    SharedPreferences preferences;
     Equpment firstEqu;
    String userId;
    int language;
    int type1;
    @Override
    public int bindLayout() {
        return R.layout.fragment_main2;

    }

    @Override
    public void initView(View view) {
        if (TextUtils.isEmpty(tips)){
            new getTipsAsynTask().execute();//获取健康小知识
        }
        language= ((MainActivity)getActivity()).getLanguage();
        equipmentDao = new EquipmentImpl(getActivity().getApplicationContext());
         firstEqu = ((MainActivity)getActivity()).getFirstEqument();
        if (firstEqu!=null){
            firstMac = firstEqu.getMacAdress();
        }
        preferences = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
        String name = preferences.getString("userName","");
        userId = preferences.getString("userId","");
        type1 = preferences.getInt("type1",0);
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
        if (type1==1){
            iv_main_ask.setVisibility(View.INVISIBLE);
        }
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
                if (((MainActivity) getActivity()).getFirstEqument()!=null) {
                    if (((MainActivity) getActivity()).getFirstEqument().getOnLine()) {
                        if (!Utils.isFastClick()) {
                               firstEqu = ((MainActivity) getActivity()).getFirstEqument();
                            if (firstEqu.getMStage()!=0xb6||firstEqu.getMStage()!=0xb7) {
                                if (firstEqu.getMStage() != 0xb2) {
                                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                                    firstEquipmentCtrl.open(0Xc0, firstMac);
                                    firstEqu.setMStage(0xb2);
                                } else {
                                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                                    firstEquipmentCtrl.open(0Xc1, firstMac);
                                    firstEqu.setMStage(0xb0);
                                }
                            }else {
                                ToastUtil.showShort(getActivity(), getText(R.string.toast_updata_no).toString());
                            }
                        } else {
                            ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_fast).toString());
                        }
                    }else {
                        ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_online).toString());
                    }

                }else {
                    ToastUtil.showShort(getActivity(), getText(R.string.toast_equ_add).toString());

                }
            }
        });

        tv_tab_bz = view.findViewById(R.id.tv_tab_bz);
        //刷新默认设备
        RefrashFirstEqu1();

        //获取数据
        twoDataList = new ArrayList<>();
        twoDataList.add(new Weather());
        initView1();
        initView2();
        //绑定services
        MQintent = new Intent(getActivity(), MQService.class);
        MQBound = getActivity().bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);
        long time=System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int  mHour=mCalendar.get(Calendar.HOUR);
        if (mHour>3&&mHour<12){
            tv_main_bz1.setText(getText(R.string.main_home_zs).toString());
        }else if (mHour>12&&mHour<18){
            tv_main_bz1.setText(getText(R.string.main_home_zw).toString());
        }else if (mHour>18||mHour<3){
            tv_main_bz1.setText(getText(R.string.main_home_ws).toString());
        }
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

    public void  RefrashFirstEqu1(){
        if ( ((MainActivity)Objects.requireNonNull(getActivity())).getFirstEqu()!=null) {
            Equpment equpment = ((MainActivity) getActivity()).getFirstEqu();
            if (equpment != null) {
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
                if (equpment.getMStage() == 0xb1) {
                    tv_main_hot.setText(R.string.equ_xq_ishot);
                } else {
                    tv_main_hot.setText(R.string.equ_xq_nohot);
                }
                if (equpment.getMStage() == 0xb2 || equpment.getMStage() == -1) {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                } else {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
                }

            }
        }else {
            li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
            firstEqu = null;
            firstMac = "";

        }

    }

    /*兩個默認設備同步*/
    public void  Synchronization(int type){
        //0是开机 1是关机 这里是显示 0 为绿色
        if (firstEqu==null){
            firstEqu = ((MainActivity)getActivity()).getFirstEqument();
        }
        if (firstEqu!=null) {
            if (type == 0xb2) {
                firstEqu.setMStage(0xb0);
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));

            } else {
                firstEqu.setMStage(0xb2);
                li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.nomal_green));
            }
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
    RecyclerViewAdapter recyclerViewAdapter2;
    RecyclerViewAdapter recyclerViewAdapter3;
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

        baseLists.clear();
        //初始化ViewPager项布局
        views1 = new ArrayList<>();
//        for (int i = 0; i < mTabItemBeanArrayList1.size(); i++) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weatherlist, null);
             RecyclerView recycler_view1 = view1.findViewById(R.id.rv_weatherlist);
            recyclerViewAdapter1 = new RecyclerViewAdapter(getActivity(), R.layout.item_weather, twoDataList,tea1);
             WeatherLayoutManager weatherLayoutManager1=new WeatherLayoutManager(getActivity());
            recycler_view1.setLayoutManager(weatherLayoutManager1);
            recycler_view1.setAdapter(recyclerViewAdapter1);
//
        View view2  = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weatherlist, null);
         RecyclerView recycler_view2  = view2.findViewById(R.id.rv_weatherlist);
         recyclerViewAdapter2  = new RecyclerViewAdapter(getActivity(), R.layout.item_weather, twoDataList,tea2);
        WeatherLayoutManager weatherLayoutManager2=new WeatherLayoutManager(getActivity());
        recycler_view2.setLayoutManager(weatherLayoutManager2);
        recycler_view2.setAdapter(recyclerViewAdapter2);

        View view3  = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weatherlist, null);
        RecyclerView recycler_view3  = view3.findViewById(R.id.rv_weatherlist);
        recyclerViewAdapter3  = new RecyclerViewAdapter(getActivity(), R.layout.item_weather, twoDataList,tea3);
        WeatherLayoutManager weatherLayoutManager3=new WeatherLayoutManager(getActivity());
        recycler_view3.setLayoutManager(weatherLayoutManager3);
        recycler_view3.setAdapter(recyclerViewAdapter3);
        baseLists.add(recyclerViewAdapter1);
        baseLists.add(recyclerViewAdapter2);
        baseLists.add(recyclerViewAdapter3);
            views1.add(view1);
            views1.add(view2);
            views1.add(view3);
//        }
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
//        if(weathers.size()==0) {
            new searchWeatherAsynTask().execute();
//        }
    }
    List<RecyclerViewAdapter> baseLists  = new ArrayList<>();
    List<Weather> weathers = new ArrayList<>();
    /*  获取天气预报*/
    class searchWeatherAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String type= "en";
            if (language==0){
                type= "ZH_TW";
            }
            String code = "";
            String result =   HttpUtils.getOkHpptRequest("http://api.openweathermap.org/data/2.5/forecast?id=1819730&APPID=64f17a2291526f4669269007d621d3b6&lang="+type+"&units=metric&cnt=24" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("cod");
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i =0;i<jsonArray.length();i++){
                            if (i==0||i==9||i==17) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Weather weather = new Weather();
                                long time = jsonObject1.getLong("dt");
                                String weak =  getWeek(time);
                                JSONObject jsonObject11 = jsonObject1.getJSONObject("main");
                                int temp = jsonObject11.getInt("temp");
                                String humidity = jsonObject11.getString("humidity");
                                JSONArray jsonObject12 = jsonObject1.getJSONArray("weather");
                                JSONObject Jweathers = jsonObject12.getJSONObject( 0);
                                String wea = Jweathers.getString("description");
                                JSONObject jsonObject13 = jsonObject1.getJSONObject("wind");
                                String air = jsonObject13.getString("speed");
                                weather.setTem(temp+"");
                                weather.setAir_level(air);
                                weather.setWea(wea);
                                weather.setHumidity(humidity);
                                weather.setWeek(weak);
                                weathers.add(weather);
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
                    new FindWeatherAsynTask().execute();
                    for (int i =0;i<weathers.size();i++){
                        List<Weather> list = new ArrayList<>();
                        list.add(weathers.get(i));
                        if (i==0)
                         recyclerViewAdapter1.refrashData(list);
                        if (i==1)
                            recyclerViewAdapter2.refrashData(list);
                            if (i==2)
                                recyclerViewAdapter3.refrashData(list);
                    }

                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:


                    break;

            }
        }
    }
    Tea tea1 = new Tea();
    Tea tea2 = new Tea();
    Tea tea3 = new Tea();
    public  String getWeek(long time) {

        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time*1000);

        int year  = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day   = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week  = cd.get(Calendar.DAY_OF_WEEK); //获取星期

        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = getText(R.string.weather_week_7).toString();
                break;
            case Calendar.MONDAY:
                weekString = getText(R.string.weather_week_1).toString();
                break;
            case Calendar.TUESDAY:
                weekString = getText(R.string.weather_week_2).toString();
                break;
            case Calendar.WEDNESDAY:
                weekString = getText(R.string.weather_week_3).toString();
                break;
            case Calendar.THURSDAY:
                weekString = getText(R.string.weather_week_4).toString();
                break;
            case Calendar.FRIDAY:
                weekString = getText(R.string.weather_week_5).toString();
                break;
            default:
                weekString = getText(R.string.weather_week_6).toString();
                break;

        }

        return weekString;
    }

    List<Tea> below = new ArrayList<>();
    List<Tea> above = new ArrayList<>();
    List<Tea> Humidity = new ArrayList<>();
    /* 查询天气茶*/

    class  FindWeatherAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String type= "en";
            if (language==0){
                type= "ZH_TW";
            }
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getWeatherTea" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject1.getJSONArray("below");
                        for (int i =0;i<jsonArray.length();i++){
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            Gson gson = new Gson();
                            Tea tea = gson.fromJson(jsonObject2.toString(),Tea.class);
                            below.add(tea);
                        }
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("Humidity");
                        for (int i =0;i<jsonArray1.length();i++){
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                            Gson gson = new Gson();
                            Tea tea = gson.fromJson(jsonObject2.toString(),Tea.class);
                            Humidity.add(tea);
                        }
                        JSONArray jsonArray2 = jsonObject1.getJSONArray("above");
                        for (int i =0;i<jsonArray2.length();i++){
                            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                            Gson gson = new Gson();
                            Tea tea = gson.fromJson(jsonObject2.toString(),Tea.class);
                            above.add(tea);
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
                    for (int i =0;i<weathers.size();i++){
                        Tea teas;
                        if (i==0){
                            if (Humidity.size()>0&&above.size()>0&&below.size()>0) {
                                if (Integer.valueOf(weathers.get(0).getHumidity()) > 70) {
                                    int num2 = (int) (Math.random() * Humidity.size());

                                    teas = Humidity.get(num2);
                                    tea1 = teas;
                                } else {
                                    if (Integer.valueOf(weathers.get(0).getTem()) > 21) {
                                        int num2 = (int) (Math.random() * above.size());

                                        teas = above.get(num2);
                                        tea2 = teas;
                                    } else {
                                        int num2 = (int) (Math.random() * below.size());

                                        teas = below.get(num2);
                                        tea3 = teas;
                                    }
                                }
                                recyclerViewAdapter1.refrashTeaData(teas);
                            }
                        }
                        if (i==1){
                            if (Humidity.size()>0&&above.size()>0&&below.size()>0) {
                                if (Integer.valueOf(weathers.get(1).getHumidity()) > 70) {
                                    int num2 = (int) (Math.random() * Humidity.size());
                                    teas = Humidity.get(num2);
                                } else {
                                    if (Integer.valueOf(weathers.get(1).getTem()) > 21) {
                                        int num2 = (int) (Math.random() * above.size());
                                        teas = above.get(num2);
                                    } else {
                                        int num2 = (int) (Math.random() * below.size());
                                        teas = below.get(num2);
                                    }
                                }
                                recyclerViewAdapter2.refrashTeaData(teas);
                            }
                        }

                        if (i==2){
                            if (Humidity.size()>0&&above.size()>0&&below.size()>0) {
                                if (Integer.valueOf(weathers.get(2).getHumidity()) > 70) {
                                    int num2 = (int) (Math.random() * Humidity.size());
                                    teas = Humidity.get(num2);
                                } else {
                                    if (Integer.valueOf(weathers.get(2).getTem()) > 21) {
                                        int num2 = (int) (Math.random() * above.size());
                                        teas = above.get(num2);
                                    } else {
                                        int num2 = (int) (Math.random() * below.size());
                                        teas = below.get(num2);
                                    }
                                }
                                recyclerViewAdapter3.refrashTeaData(teas);
                            }
                        }

                    }

                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:


                    break;

            }
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
