package teabar.ph.com.teabar.activity.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.FriendInforImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.FriendListAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.FriendInfor;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;


public class FriendListActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.iv_power_add)
   ImageView iv_power_add;
    @BindView(R.id.rv_friend_inform)
    RecyclerView rv_friend_inform;
    MyApplication application;
    FriendListAdapter friendListAdapter ;
    List<UserInfo> list =new ArrayList<>() ;
    FriendInforImpl friendInforDao;
    int delfriend = 0;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_friendlist;
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        friendInforDao = new FriendInforImpl(getApplicationContext());
        friendListAdapter = new FriendListAdapter(this,list);
        rv_friend_inform.setLayoutManager(new LinearLayoutManager(this));
        rv_friend_inform.setAdapter(friendListAdapter);
        findFriend();
        friendListAdapter.SetOnItemClick(new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FriendListActivity.this,ChatActivity.class);
                intent.putExtra("targetId",friendListAdapter.getmData().get(position).getUserName());
                intent.putExtra("targetAppKey",friendListAdapter.getmData().get(position).getAppKey());
                intent.putExtra("name",friendListAdapter.getmData().get(position).getUserName());
                delfriend=1000;
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                customDialog1(position);
            }
        });
    }

    public void findFriend(){
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                if (0 == responseCode) {
                    //获取好友列表成功
                    friendListAdapter.setmData(userInfoList);
                    friendListAdapter.notifyDataSetChanged();


                } else {
                    //获取好友列表失败
                    toast("获取好友列表失败，请重试");
                }
            }
        });
    }


    Dialog dialog;
    private void customDialog1(final int position ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this , R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText("删除好友");
        et_dia_name.setText("是否删除好友");
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendListAdapter.getmData().get(position).removeFromFriendList(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            //移出好友列表成功
                            toast("删除好友成功");
                            UserInfo userInfo =  friendListAdapter.getmData().get(position ) ;
                            FriendInfor friendInfor = friendInforDao.findById(Long.valueOf(userInfo.getUserName()));
                            if (friendInfor!=null){
                                friendInforDao.delete(friendInfor);
                            }

                            JMessageClient.deleteSingleConversation(userInfo.getUserName(), userInfo.getAppKey());
                            friendListAdapter.getmData().remove( friendListAdapter.getmData().get(position));
                            friendListAdapter.notifyDataSetChanged();
                            delfriend=1000;

                        } else {
                            //移出好友列表失败
                            toast("删除好友失败");
                        }
                    }
                });

                dialog.dismiss();

            }
        });
        dialog.show();

    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.iv_power_fh,R.id.iv_power_add})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                setResult(delfriend);
                finish();
                break;

            case R.id.iv_power_add:
                Intent intent = new Intent();
                intent.setClass(FriendListActivity.this,AddFriendActivity.class);
                startActivityForResult(intent,6000);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==3000){
            findFriend();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
