package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.zlpolygonview.ZLPolygonView;
import com.ph.teabar.database.dao.DaoImp.FriendImpl;
import com.ph.teabar.database.dao.DaoImp.FriendInforImpl;
import com.ph.teabar.database.dao.DaoImp.UserEntryImpl;
import com.ph.teabar.database.dao.FriendDao;
import com.ph.teabar.database.dao.FriendInforDao;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.SocialInformAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Event;
import teabar.ph.com.teabar.pojo.EventType;
import teabar.ph.com.teabar.pojo.Friend;
import teabar.ph.com.teabar.pojo.FriendInfor;
import teabar.ph.com.teabar.pojo.UserEntry;
import teabar.ph.com.teabar.util.SharePreferenceManager;


public class SocialInformActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.rv_social_inform)
    RecyclerView rv_social_inform;
    FriendInforImpl friendInforDao;
    MyApplication application;
    SocialInformAdapter socialInformAdapter ;

    private List<FriendInfor> mList = new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_socialinform;
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        friendInforDao = new FriendInforImpl(getApplicationContext());
        mList = friendInforDao.findAll();
        socialInformAdapter = new SocialInformAdapter(this,mList);
        rv_social_inform.setLayoutManager(new LinearLayoutManager(this));
        rv_social_inform.setAdapter(socialInformAdapter);
        socialInformAdapter.SetOnItemClick(new SocialInformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final FriendInfor friendInfor =  socialInformAdapter.getmData().get(position);
                ContactManager.acceptInvitation(friendInfor.getId()+"", friendInfor.getAppKey(), new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            //接收好友请求成功
                           friendInfor .setAddNum(1);
                          friendInforDao.update(friendInfor);
                          socialInformAdapter.notifyDataSetChanged();
                          toast("接收好友请求成功");

                        } else {
                            //接收好友请求失败
                            toast("接收好友请求失败");
                        }
                    }
                });
            }
        });
    }

        String userNickname;
        //接收到好友事件
        public void onEvent(final ContactNotifyEvent event) {
            final String reason = event.getReason();
            final String username = event.getFromUsername();
            final String appKey = event.getfromUserAppKey();
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String desc, UserInfo userInfo) {
                    if (status == 0) {
                        userNickname = userInfo.getNickname();
                        //对方接收了你的好友请求
                        FriendInfor friendInfor =  friendInforDao.findById(Long.valueOf(username));
                        if (event.getType() == ContactNotifyEvent.Type.invite_accepted) {
                            if (friendInfor==null) {
                                FriendInfor friend = new FriendInfor();
                                friend.setUseName(userNickname);
                                friend.setId(Long.valueOf(username));
                                friend.setAppKey(appKey);
                                friend.setAddNum(0);
                                friendInforDao.insert(friend);
                                mList.add(friend);
                            }else {
                                mList.add(friendInfor);
                            }
                            socialInformAdapter.notifyDataSetChanged();
                            //拒绝好友请求
                        } else if (event.getType() == ContactNotifyEvent.Type.invite_declined) {


                        } else if (event.getType() == ContactNotifyEvent.Type.invite_received) {
                            //如果同一个人申请多次,则只会出现一次;当点击进验证消息界面后,同一个人再次申请则可以收到
                            if (friendInfor == null) {
                                FriendInfor friend = new FriendInfor();
                                friend.setUseName(userNickname);
                                friend.setAppKey(appKey);
                                friend.setId(Long.valueOf(username));
                                friend.setAddNum(0);
                                friendInforDao.insert(friend);
                            }else {
                                friendInfor.setAddNum(0);
                                friendInforDao.update(friendInfor);
                            }
                            socialInformAdapter.notifyDataSetChanged();

                        } else if (event.getType() == ContactNotifyEvent.Type.contact_deleted) {
                            JMessageClient.deleteSingleConversation(userInfo.getUserName(), userInfo.getAppKey());
                            if (friendInfor!=null){
                                friendInforDao.delete(friendInfor);
                            }
                            socialInformAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    }




    @Override
    public void doBusiness(Context mContext) {

    }
    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;

        }
    }
}
