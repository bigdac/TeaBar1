package teabar.ph.com.teabar.activity.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.Mode.InfoModel;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.FriendAddAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Friend;
import teabar.ph.com.teabar.util.HttpUtils;


public class AddFriendActivity extends BaseActivity {

//    @BindView(R.id.tv_main_1)
//    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.rv_friend_inform)
    RecyclerView rv_friend_inform;
    @BindView(R.id.et_add_id)
    EditText et_add_id;
    MyApplication application;
    FriendAddAdapter friendAddAdapter ;
    List<Friend> friendList = new ArrayList<>();
    QMUITipDialog tipDialog;
    private UserInfo mMyInfo;
    private String mTargetAppKey;
    private int cdeo= 0;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_addfriend;
    }

    @Override
    public void initView(View view) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                ScreenUtils.getStatusBarHeight());
//        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        friendAddAdapter = new FriendAddAdapter(this,friendList);
        rv_friend_inform.setLayoutManager(new LinearLayoutManager(this));
        rv_friend_inform.setAdapter(friendAddAdapter);
        friendAddAdapter.SetOnItemClick(new FriendAddAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showProgressDialog();
                    String searchUserName = friendAddAdapter.getmData().get(position).getId()+"";
                    addFriend(searchUserName);

            }

        });

    }






    @Override
    public void doBusiness(Context mContext) {

    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.iv_power_fh,R.id.bt_friend_search})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                setResult(cdeo);
                finish();
                break;

            case R.id.bt_friend_search:
                /*搜索好友*/
                hintKbTwo();
                String searchUserName = et_add_id.getText().toString().trim();
                if (!TextUtils.isEmpty(searchUserName)) {
                    Map<String,Object> params = new HashMap<>();
                    params.put("search",searchUserName);
                    showProgressDialog();
                    new SearchFriendAsyncTask(this).execute(params);

                }else {
                    toast(getText(R.string.social_friend_search).toString());
                }
                break;

        }
    }
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    /*掉极光IM添加好友*/
    public void addFriend (String searchUserName){
        JMessageClient.getUserInfo(searchUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (responseCode == 0) {
                    InfoModel.getInstance().friendInfo = info;
                    Intent intent = new Intent(AddFriendActivity.this,AddFriendActivity1.class);
                    intent.putExtra("userName",info.getUserName());
                    startActivity(intent);
                    cdeo=3000;
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                } else {
                   toast( getText(R.string.toast_search_no).toString());
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                }
            }
        });
    }

    String returnMsg1,returnMsg2;
    class SearchFriendAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {


        public SearchFriendAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code  ="";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/selectUser",params);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        friendList.clear();
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2=jsonObject.getString("message3");
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        for (int i =0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            long id = jsonObject1.getLong("id");
//                            String userName = jsonObject1.getString("userName");
//                            String photoUrl = jsonObject1.getString("photoUrl");
//                            Friend friend = new Friend();
//                            friend.setId(id);
//                            friend.setPhotoUrl(photoUrl);
//                            friend.setUserName(userName);
//                            friendList.add(friend);
//                        }
                        JsonObject content = new JsonParser().parse(result).getAsJsonObject();
                        JsonArray list = content.getAsJsonArray("data");
                        Gson gson = new Gson();
                        for (int i = 0; i < list.size(); i++) {
                            //通过反射 得到UserBean.class
                            Friend userList = gson.fromJson(list.get(i), Friend.class);
                            friendList.add(userList);
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
                    friendAddAdapter.notifyDataSetChanged();
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }

                    break;

                case "4000":
                    toast(getText(R.string.toast_all_cs).toString());
                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    break;

                default:
                    if (application.IsEnglish()==0){
                        toast(returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }

                    if (tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    break;
            }
        }


    }
}
