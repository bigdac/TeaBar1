package teabar.ph.com.teabar.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MakeActivity;
import teabar.ph.com.teabar.activity.QusetionActivity;
import teabar.ph.com.teabar.activity.SearchActivity;
import teabar.ph.com.teabar.adpter.BaseRecyclerAdapter;
import teabar.ph.com.teabar.adpter.BaseViewHolder;
import teabar.ph.com.teabar.adpter.MyViewPagerAdapter;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.adpter.TabAdapter;
import teabar.ph.com.teabar.adpter.WetherAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.TabItemBean;
import teabar.ph.com.teabar.util.DisplayUtil;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.zxing.android.CaptureActivity;
import teabar.ph.com.teabar.view.MyLayoutManager;
import teabar.ph.com.teabar.view.MyRecyclerView;
import teabar.ph.com.teabar.view.MyScrollView;
import teabar.ph.com.teabar.view.WeatherLayoutManager;


public class MainFragment extends BaseFragment  {

    List<String> mainMemu = new ArrayList<>();
    private List<MyRecyclerView> recyclerViewList=new ArrayList<>();

    private ArrayList<View> views,views1;
    /**保存的选项卡的下标值*/
    private int savdCheckedIndex = 0;
    /**当前的选项卡的下标值*/
    private int mCurrentIndex = -1;
    /**保存的选项卡的下标值*/
    private int savdCheckedIndex1 = 0;
    /**当前的选项卡的下标值*/
    private int mCurrentIndex1 = -1;

    ViewPager view_pager,view_pager_weather;
    private RecyclerView tv_tab,tv_tab_bz;
    private MyScrollView scrollView;
    RecyclerViewAdapter recyclerViewAdapter,recyclerViewAdapter2;
    private MyViewPagerAdapter pagerAdapter,pagerAdapter1;
    private ArrayList<TabItemBean> mTabItemBeanArrayList,mTabItemBeanArrayList1;
    private TabAdapter mTabAdapter;
    private WetherAdapter mWetherAdapter;
    private List<String> oneDataList;
    private List<String> twoDataList;
    LinearLayout li_main_title ;
    boolean isOpen = false;
    ImageView iv_main_search,iv_main_ask;
    private static final int REQUEST_CODE_SCAN = 0x0000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_main;

    }

    @Override
    public void initView(View view) {

        scrollView =view. findViewById(R.id.scrollView);
        view_pager =view. findViewById(R.id.view_pager);
        iv_main_search = view.findViewById(R.id.iv_main_search);
        iv_main_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(),SearchActivity.class));
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
        view_pager_weather = view.findViewById(R.id.view_pager_weather);
        li_main_title = view.findViewById(R.id.li_main_title);
        li_main_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen){
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.main_title1));
                    isOpen=false;
                }else {
                    li_main_title.setBackgroundColor(getActivity().getResources().getColor(R.color.tab_auto_selected_top));
                    isOpen= true;
                }

            }
        });
        tv_tab = view.findViewById(R.id.tv_tab);
        tv_tab_bz = view.findViewById(R.id.tv_tab_bz);

        //动态设置ViewPager高度使其显示在标题和TAB下面占满全部布局
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view_pager.getLayoutParams();
        params.height = DisplayUtil.getScreenHeight(getActivity()) - DisplayUtil.getStatusBarHeight(getActivity())
                - DisplayUtil.dip2px(getActivity(), 50 + 50);
        view_pager.setLayoutParams(params);
        //获取数据
        getData();
        initView1();
        initView2();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
     MyRecyclerView recycler_view;
    private void initView1() {
        mTabItemBeanArrayList = new ArrayList<TabItemBean>();
        mTabItemBeanArrayList.add(new TabItemBean("草本系列"," "));
        mTabItemBeanArrayList.add(new TabItemBean("水果系列"," "));
        mTabItemBeanArrayList.add(new TabItemBean("功能茶"," "));
        mTabItemBeanArrayList.add(new TabItemBean("个人计划"," "));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        tv_tab.setLayoutManager(linearLayoutManager);
        //设置适配器
//        if(mTabAdapter == null){
            //设置适配器
            mTabAdapter = new TabAdapter(getActivity(), mTabItemBeanArrayList);
            tv_tab.setAdapter(mTabAdapter);
            //添加分割线
            //设置添加删除动画
            //调用ListView的setSelected(!ListView.isSelected())方法，这样就能及时刷新布局
            tv_tab.setSelected(true);
//        }else{
//            mTabAdapter.notifyDataSetChanged();
//        }
//

        //初始化ViewPager项布局
        views = new ArrayList<>();
        for (int i = 0; i < mTabItemBeanArrayList.size(); i++) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tealist, null);
            recycler_view = view1.findViewById(R.id.rv_tealist);
            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), R.layout.item_mailpic, i % 2 == 0 ? oneDataList : twoDataList);
            final MyLayoutManager layoutManager=new MyLayoutManager(getActivity(),2);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setItemListener(new BaseRecyclerAdapter.ItemListener() {
                @Override
                public void onItemClick(BaseViewHolder holder, int position) {
                    startActivity(new Intent(getActivity(),MakeActivity.class));
                }
            });
            views.add(view1);
            recyclerViewList.add(recycler_view);
            recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //获取TEB的位置
                    int[] tabLocation = new int[2];
                    tv_tab.getLocationOnScreen(tabLocation);
                    Log.e(TAG, "RecyclerView-->"+tabLocation[1]+"...."+DisplayUtil.getStatusBarHeight(getActivity()) );
                    //判断TAB是否滑动到标题下面
                    if (tabLocation[1] > DisplayUtil.getStatusBarHeight(getActivity())
                        /*+ DisplayUtil.dip2px(getActivity(), 48)*/ ) {
                        scrollView.setEnableScroll(true);
                        recycler_view.setEnableScroll(false);
                        Log.e(TAG, "scrollView: true" );
                    } else {
                        scrollView.setEnableScroll(false);
                        recycler_view.setEnableScroll(true);
                        Log.e(TAG, "RecyclerView: true" );
                        //判断是否滑动到顶部
                        if(0==((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()){

                        }
                    }
                }
            });
        }
        pagerAdapter = new MyViewPagerAdapter(views);
        view_pager.setAdapter(pagerAdapter);
//列表适配器的点击监听事件
        mTabAdapter.setOnItemClickLitener(new TabAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mCurrentIndex == position) {
                    return;
                }
                setFragmentDisplay(position);//独立出来，用于OnResume的时候初始化展现相应的Fragment

                savdCheckedIndex = position;
                mCurrentIndex = position;
            }
        });
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTabsDisplay(position);
                savdCheckedIndex = position;
                mCurrentIndex = position;
            }

            @Override
            public void onPageSelected(int position) {
//                tv_tab.setText("tab" + (position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //获取TEB的位置
                int[] tabLocation = new int[2];
                tv_tab.getLocationOnScreen(tabLocation);
                //判断TAB是否滑动到标题下面
                if (tabLocation[1] > DisplayUtil.getStatusBarHeight(getActivity())
                        + DisplayUtil.dip2px(getActivity(), 48)) {
                    scrollView.setEnableScroll(true);
                    for (int i =0;i<recyclerViewList.size();i++){
                        recyclerViewList.get(i).setEnableScroll(true);

                    }

//                        recyclerViewList.get(0).setEnableScroll(true);
//                        recyclerViewList.get(1).setEnableScroll(true);

                } else {
                    scrollView.setEnableScroll(false);
                    for (int i =0;i<recyclerViewList.size();i++){
                        recyclerViewList.get(i).setEnableScroll(true);

                    }
//                        recyclerViewList.get(0).setEnableScroll(true);
//                        recyclerViewList.get(1).setEnableScroll(true);


                }
            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: -->" );
        if (mWetherAdapter!=null)
            mWetherAdapter=null;
        if (mTabAdapter!=null)
            mTabAdapter=null;
        recyclerViewAdapter=null;
        recyclerViewList.clear();
        recyclerViewAdapter2=null;
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
            //调用ListView的setSelected(!ListView.isSelected())方法，这样就能及时刷新布局
            tv_tab_bz.setSelected(true);
//        }else{
//            mWetherAdapter.notifyDataSetChanged();
//        }


        //初始化ViewPager项布局
        views1 = new ArrayList<>();
        for (int i = 0; i < mTabItemBeanArrayList1.size(); i++) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weatherlist, null);
            final MyRecyclerView recycler_view2 = view1.findViewById(R.id.rv_weatherlist);
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
        if(mTabAdapter != null){
            mTabAdapter.setSelected(index);
            tv_tab.smoothScrollToPosition(index);//平移滑动到指定position
        }
    }


    /**
     * 设置碎片之间的切换
     */
    private void setFragmentDisplay(int index){
        view_pager.setCurrentItem(index, false);//smoothScroll false表示切换的时候,不经过两个页面的中间页
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
        oneDataList = new ArrayList<>();
        twoDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            oneDataList.add("A项目:" + i);
            twoDataList.add("B项目:" + i);
        }
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
                    ToastUtil.showShort(getActivity(),"请扫描正确的二维码");
//                }


//            }
//        }
    }

}
