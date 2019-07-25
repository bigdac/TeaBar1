package teabar.ph.com.teabar.activity.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.social.AddSocialActivity;
import teabar.ph.com.teabar.adpter.viewholder.CircleAdapter3;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.bean.CircleItem;
import teabar.ph.com.teabar.bean.CommentConfig;
import teabar.ph.com.teabar.bean.CommentItem;
import teabar.ph.com.teabar.bean.FavortItem;
import teabar.ph.com.teabar.bean.PhotoInfo;
import teabar.ph.com.teabar.bean.User;
import teabar.ph.com.teabar.mvp.contract.CircleContract;
import teabar.ph.com.teabar.mvp.presenter.CirclePresenter;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.widgets.DivItemDecoration;

public class MyIssueActivity extends BaseActivity implements CircleContract.View {


    private CircleAdapter3 circleAdapter;
    private SuperRecyclerView recyclerView;
    List<String> mList = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    MyApplication application;
    private final static int TYPE_PULLREFRESH = 1;
    private final static int TYPE_UPLOADREFRESH = 2;
    private List<CircleItem> circleItemList = new ArrayList<>();
    private List<CircleItem> circleItemList1 = new ArrayList<>();
    int currentPage = 1;
    QMUITipDialog tipDialog;
    int  Type = 0;
    SharedPreferences preferences;
    private CirclePresenter presenter;
    String id ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_myissue;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);

        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        id = preferences.getString("userId","");
        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        recyclerView = (SuperRecyclerView)view. findViewById(R.id.recyclerView);

        recyclerView.getSwipeToRefresh().post(new Runnable(){
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作

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
                        new  ShowContentAsynctask(MyIssueActivity.this).execute(params);
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
                    Glide.with(MyIssueActivity.this).resumeRequests();
                }else{
                    Glide.with(MyIssueActivity.this).pauseRequests();
                }

            }
        });
        circleAdapter = new CircleAdapter3( this,circleItemList);
        presenter = new CirclePresenter(this );
        circleAdapter.setCirclePresenter(presenter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        recyclerView.setAdapter(circleAdapter);
        /*处理点赞逻辑*/
        circleAdapter.SetOnItemClick(new CircleAdapter3.OnItemClickListener() {
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

    }
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
                    new GiveThumbsUpAsynctask(MyIssueActivity.this).execute(params1,params2);
                    break;
            }
        }
    };

     /*
       茶吧点赞*/

    class GiveThumbsUpAsynctask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public GiveThumbsUpAsynctask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
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
        protected void onPostExecute(BaseActivity baseActivity, String s) {

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

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_plan_fh })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_plan_fh:
                finish();
                break;


        }
    }

   /* 茶吧查看发布*/
    String returnMsg1;

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
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }
    /*
    * 查看 发布内容
    * */
    class ShowContentAsynctask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public ShowContentAsynctask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/content/myContent",param);
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
        protected void onPostExecute(BaseActivity baseActivity, String s) {

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
                    ToastUtil.showShort(MyIssueActivity.this,returnMsg1);
                    break;
            }
        }
    }

    @Override
    public void update2DeleteCircle(String circleId) {

    }

    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {

    }

    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {

    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {

    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {

    }

    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (loadType == TYPE_PULLREFRESH){
            recyclerView.setRefreshing(false);
            circleAdapter.setData(datas);

        }else if(loadType == TYPE_UPLOADREFRESH){
            Log.e(TAG, "update2loadData222: -->"+circleAdapter.getDates1().size() );
//            circleAdapter.getDatas().addAll(datas);
            circleAdapter.setData(datas);

            Log.e(TAG, "update2loadData333: -->"+circleAdapter.getDates1().size() );
        }

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
                            new ShowContentAsynctask(MyIssueActivity.this).execute(params);
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


}
