package teabar.ph.com.teabar.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.jessyan.autosize.utils.ScreenUtils;
import pub.devrel.easypermissions.EasyPermissions;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.AddSocialActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.adpter.CircleAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.bean.CircleItem;
import teabar.ph.com.teabar.bean.CommentConfig;
import teabar.ph.com.teabar.bean.CommentItem;
import teabar.ph.com.teabar.bean.FavortItem;
import teabar.ph.com.teabar.bean.PhotoInfo;
import teabar.ph.com.teabar.bean.User;
import teabar.ph.com.teabar.mvp.contract.CircleContract;
import teabar.ph.com.teabar.mvp.presenter.CirclePresenter;
import teabar.ph.com.teabar.util.CommonUtils;
import teabar.ph.com.teabar.util.DisplayUtil;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.widgets.CommentListView;
import teabar.ph.com.teabar.widgets.DivItemDecoration;




public class FriendCircleFragment1 extends BaseFragment  implements CircleContract.View, EasyPermissions.PermissionCallbacks{
    protected static final String TAG = MainActivity.class.getSimpleName();
    private CircleAdapter circleAdapter;
    private LinearLayout edittextbody;
    private EditText editText;
    private ImageView sendIv;
    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;
    private CirclePresenter presenter;
    private CommentConfig commentConfig;
    private SuperRecyclerView recyclerView;
    private RelativeLayout bodyLayout;
    private LinearLayoutManager layoutManager;
    private final static int TYPE_PULLREFRESH = 1;
    private final static int TYPE_UPLOADREFRESH = 2;
    QMUITipDialog tipDialog;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private List<CircleItem> circleItemList = new ArrayList<>();
    private List<CircleItem> circleItemList1 = new ArrayList<>();
    SharedPreferences preferences;
    long id ;
    int currentPage = 1;
    int  Type = 0;
    Context context;
    @Override
    public int bindLayout() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_main1;
    }

    @Override
    public void initView(View view) {
        presenter = new CirclePresenter( this);
        context= getActivity();
        preferences = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
        id = preferences.getLong("userId",0);
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后...")
                .create();
        initView1(view);
        initPermission();
        //实现自动下拉刷新功能
        recyclerView.getSwipeToRefresh().post(new Runnable(){
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作

            }
        });


    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    private void initPermission() {
        String[] perms = {Manifest.permission.CALL_PHONE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "因为功能需要，需要使用相关权限，请允许",
                    100, perms);
        }
    }

    public void showProgressDialog() {
        tipDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AddSocialActivity.isFrash){
            showProgressDialog();
            Type = TYPE_PULLREFRESH;
            currentPage=1;
            circleItemList.clear();
            Map<String ,Object> params = new HashMap<>();
            params.put("id",id);
            params.put("currentPage",currentPage);
            params.put("pageSize",10);
            new ShowContentAsynctask().execute(params);
        }
    }

    @Override
    public void onDestroy() {
        if(presenter !=null){
            presenter.recycle();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
    private void initView1(View view) {

        recyclerView = (SuperRecyclerView)view. findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        presenter.loadData(TYPE_PULLREFRESH);
                        Type = TYPE_PULLREFRESH;
                        currentPage=1;
                        circleItemList.clear();
                        Map<String ,Object> params = new HashMap<>();
                        params.put("id",id);
                        params.put("currentPage",currentPage);
                        params.put("pageSize",10);
                        new ShowContentAsynctask().execute(params);
                    }
                }, 2000);
            }
        };
        recyclerView.setRefreshListener(refreshListener);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Glide.with(getActivity()).resumeRequests();
                }else{
                    Glide.with(getActivity()).pauseRequests();
                }

            }
        });

        circleAdapter = new CircleAdapter(getActivity());
        circleAdapter.setCirclePresenter(presenter);
        recyclerView.setAdapter(circleAdapter);

        /*处理点赞逻辑*/
        circleAdapter.SetOnItemClick(new CircleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    int num = circleAdapter.getDates1().get(position-1).getClickNum();
                    if (num==0){
                        circleAdapter.getDates1().get(position-1).setClickNum(1);
                    }else {
                        circleAdapter.getDates1().get(position-1).setClickNum(0);
                    }
                    long time1 = circleAdapter.getDates1().get(position-1).getClickTime1();
                    long time2 = System.currentTimeMillis();
                Log.e(TAG, "onItemClick: --> "+time1+">>>>>>>>>>>"+time2 +"..."+(position-1) );
                    if (time2-time1>=2000){
                        Log.e(TAG, "onItemClick: -->KKKKKKKKKKKKKKKKKKKKKKK"  );
                        Message message = new Message();
                        message.what = 1;
                        message.obj = position-1;
                        handler.sendMessageDelayed(message,2000);
                        circleAdapter.getDates1().get(position-1).setClickTime1(time2);
                    }

            }
        });
        edittextbody = (LinearLayout) view.findViewById(R.id.editTextBodyLl);
        bodyLayout = (RelativeLayout) view.findViewById(R.id.bodyLayout);
        editText = (EditText) view.findViewById(R.id.circleEt);
        sendIv = (ImageView) view.findViewById(R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    //发布评论
                    String content =  editText.getText().toString().trim();
                    if(TextUtils.isEmpty(content)){
                        Toast.makeText(getActivity(), "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    presenter.addComment(content, commentConfig);
                    CommentItem item = new CommentItem();
                    item.setId(circleItemList.get(commentConfig.circlePosition).getId());
                    item.setContent(content);
                    item.setUser( circleItemList.get(commentConfig.circlePosition).getUser() );
                    update2AddComment(commentConfig.circlePosition,item);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

        setViewTreeObserver();
    }



    boolean isRunning= false;
    int mPosition = -1;
    @SuppressLint("HandlerLeak")
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case  1:
                    int position =(int)msg.obj;
                    Map<String,Object> params1 = new HashMap<>();
                    Map<String,Object> params2 = new HashMap<>();
                    params1.put("userId",id);
                    params1.put("contentId",circleItemList.get(position).getId());
                    params2.put("position",position);
                    Log.e(TAG, "onItemClick: -->"+circleAdapter.getDates1().get(position).isOpen()+"...."+circleItemList.get(position).getId() );
                    new GiveThumbsUpAsynctask().execute(params1,params2);
                    break;
            }
        }
    };
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
    茶吧查看发布*/
    String returnMsg1;
    class ShowContentAsynctask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/content/showContent",param);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){
//                            circleItemList.clear();
                            circleItemList1.clear();
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray items = data.getJSONArray("items");
                            for (int i=0;i<items.length();i++){
                                List<PhotoInfo> photos = new ArrayList<>();
                                List<CommentItem> commentItems = new ArrayList<>();
                                CircleItem circleItem  =new CircleItem();
                                JSONObject contents = items.getJSONObject(i);

                                String id  = contents.getString("id");
                                String content = contents.getString("content");
                                String picture1  = "null".equals(contents.getString("picture1"))? "":contents.getString("picture1");
                                String picture2  ="null".equals(contents.getString("picture2"))? "":contents.getString("picture2");
                                String picture3  ="null".equals(contents.getString("picture3"))? "":contents.getString("picture3");
                                String picture4  ="null".equals(contents.getString("picture4"))? "":contents.getString("picture4");
                                String pictures [] = {picture1,picture2,picture3,picture4};
                                for (int j =0;j<4;j++){
                                    if (!TextUtils.isEmpty(pictures[j])){
                                        PhotoInfo photoInfo = new PhotoInfo();
                                        photoInfo.setUrl(pictures[j]);
                                        photos.add(photoInfo);
                                    }
                                }
                                long userId = contents.getLong("userId");
                                String createTime = stampToDate(contents.getString("createTime1"));
                                int thumbsUp = contents.getInt("thumbsUp");
                                int commentNum = contents.getInt("commentNum");
                                String userName = contents.getString("userName");
                                int isFlag = contents.getInt("isFlag");
                                String photoUrl = contents.getString("photoUrl");
                                User user = new User();
                                user.setId(userId+"");
                                user.setName(userName);
                                user.setHeadUrl(photoUrl);
                                if (isFlag==0){
                                    circleItem.setOpen(false);
                                }else {
                                    circleItem.setOpen(true);
                                }
                                circleItem.setIsFlag(isFlag);
                                circleItem.setClickNum(isFlag);
                                circleItem.setId(id);
                                circleItem.setContent(content);
                                circleItem.setPhotos(photos);
                                circleItem.setCreateTime(createTime);
                                circleItem.setUser(user);
                                circleItem.setCommentNum(commentNum);
                                circleItem.setThumbsUp(thumbsUp);
                                if (!contents.isNull("comment")){
                                    JSONObject comments = contents.getJSONObject("comment");
                                    String commentUserName = comments.getString("commentUserName");
                                    String comment = comments.getString("comment");
                                    /*回复*/
                                    CommentItem item = new CommentItem();
                                    item.setId(userId+"");
                                    item.setUser(new User("",commentUserName,""));
                                    item.setContent(comment);
                                    commentItems.add(item);
                                    circleItem.setComments(commentItems);
                                }
                                circleItemList.add(circleItem);
                                circleItemList1.add(circleItem);

                            }
                        }
                    } catch ( Exception e) {
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
            switch (s){
                case "200":
                    currentPage++;
                    Log.e(TAG, "onPostExecute: -->"+circleItemList.size()+"...."+currentPage+"...."+Type );
                    update2loadData(Type,circleItemList);
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                        AddSocialActivity.isFrash= false;
                    }
                    break;
                    default:
                        if (tipDialog.isShowing()){
                            tipDialog.dismiss();
                            AddSocialActivity.isFrash= false;
                        }
                        ToastUtil.showShort(getActivity(),returnMsg1);
                        break;
            }
        }
    }


    /*
       茶吧查看发布*/

    class ToCommentAsynctask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/content/toComment",param);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){


                        }
                    } catch ( Exception e) {
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
            switch (s){
                case "200":

                    break;
                default:
                    ToastUtil.showShort(getActivity(),returnMsg1);
                    break;
            }
        }


    }


  /*
       茶吧点赞*/

    class GiveThumbsUpAsynctask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            Map<String,Object> param1 = maps[1];
            int position =  (int) param1.get("position");
            String result = "";
           if ( circleAdapter.getDates1().get(position).isOpen()&&circleItemList.get(position).getClickNum()!=circleItemList.get(position).getIsFlag()){

                        result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress + "/content/giveThumbsUp", param);
                        CircleItem circleItem = circleItemList.get(position);
                        circleItem.setIsFlag(1);
                        circleItemList.set(position, circleItem);
               Log.e(TAG, "doInBackground: -->"+circleItemList.get(position).getIsFlag() );


           }else  if (!circleAdapter.getDates1().get(position).isOpen()&&circleItemList.get(position).getClickNum()!=circleItemList.get(position).getIsFlag()){

                   result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress + "/content/cancelPoints", param);
                   CircleItem circleItem = circleItemList.get(position);
                   circleItem.setIsFlag(0);
                   circleItemList.set(position, circleItem);
               Log.e(TAG, "doInBackground: -->"+circleItemList.get(position).getIsFlag() );

           }
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){


                        }
                    } catch ( Exception e) {
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
            switch (s){
                case "200":
                    mPosition=-1;
                    break;
                default:
                    mPosition=-1;
//                    ToastUtil.showShort(getActivity(),returnMsg1);
                    break;
            }
        }


    }




    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = ScreenUtils.getStatusBarHeight() ;//状态栏高度
//                int hingth=(int)(50 * MainActivity.scale + 0.5f);
                int screenH = bodyLayout.getRootView().getHeight();
                if(r.top != statusBarH ){
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                Log.d(TAG, "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if(keyboardH == currentKeyboardH){//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = edittextbody.getHeight();

                if(keyboardH<150){//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if(layoutManager!=null && commentConfig != null){
//                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE, getListviewOffset(commentConfig));
                    layoutManager.scrollToPosition(commentConfig.circlePosition +CircleAdapter.HEADVIEW_SIZE );

                }
            }
        });
    }
    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        List<CircleItem> circleItems = circleAdapter.getDatas();
        for(int i=0; i<circleItems.size(); i++){
            if(circleId.equals(circleItems.get(i).getId())){
                circleItems.remove(i);
                circleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {
        if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
//            item.getFavorters().add(addItem);
            circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
    }


/*
* 添加评论
* */
    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {
        if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            if ( item.getComments()!=null){
                item.getComments().add(addItem);
                item.setCommentNum(item.getCommentNum()+1);

            }else {
                List<CommentItem> commentItems = new ArrayList<>();
                commentItems.add(addItem);
                item.setComments(commentItems);
                item.setCommentNum(item.getCommentNum()+1);
            }
            Map<String ,Object> param = new HashMap<>();
            param.put("comment",addItem.getContent());
            param.put("userId",id);
            param.put("contentId",addItem.getId());
            new ToCommentAsynctask().execute(param);
            circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
        //清空评论文本
        editText.setText("");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<CommentItem> items = item.getComments();
        for(int i=0; i<items.size(); i++){
            if(commentId.equals(items.get(i).getId())){
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        edittextbody.setVisibility(visibility);

        measureCircleItemHighAndCommentItemOffset(commentConfig);

        if(View.VISIBLE==visibility){
            editText.requestFocus();
            if (hidenShowView!=null){
                hidenShowView.hiden(true);
            }
            //弹出键盘
            CommonUtils.showSoftInput( editText.getContext(),  editText);

        }else if(View.GONE==visibility){
            if (hidenShowView!=null){
                hidenShowView.hiden(false);
            }
            //隐藏键盘
            CommonUtils.hideSoftInput( editText.getContext(),  editText);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        hidenShowView= (hidenShowView) getActivity();
    }
    hidenShowView hidenShowView;
     public  interface hidenShowView{
        void hiden(boolean b);
     }
    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (loadType == TYPE_PULLREFRESH){
            recyclerView.setRefreshing(false);
            circleAdapter.setDatas(datas);
        }else if(loadType == TYPE_UPLOADREFRESH){
            Log.e(TAG, "update2loadData222: -->"+circleAdapter.getDatas().size() );
//            circleAdapter.getDatas().addAll(datas);
            circleAdapter.setDatas(datas);
            Log.e(TAG, "update2loadData333: -->"+circleAdapter.getDatas().size() );
        }
        circleAdapter.notifyDataSetChanged();
        if(circleItemList1.size()==10){
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            presenter.loadData(TYPE_UPLOADREFRESH);
                            Type = TYPE_UPLOADREFRESH;
                            Map<String ,Object> params = new HashMap<>();
                            params.put("id",id);
                            params.put("currentPage",currentPage);
                            params.put("pageSize",10);
                            new ShowContentAsynctask().execute(params);
                        }
                    }, 2000);

                }
            }, 1);
        }
        else{
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }

    }


    /**
     * 测量偏移量
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if(commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight-(int) (50 * MainActivity.scale + 0.5f);
        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        Log.i(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig){
        if(commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition /*+ CircleAdapter.HEADVIEW_SIZE */- firstPosition);

        if(selectCircleItem != null){
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if(commentLv!=null){
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if(selectCommentItem != null){
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if(parentView != null){
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }




    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Toast.makeText(this, "onPermissionsGranted  requestCode: " + requestCode , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(getActivity(), "您拒绝了相关权限，可能会导致相关功能不可用" , Toast.LENGTH_LONG).show();
        /*if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null *//* click listener *//*)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }*/
    }
}
