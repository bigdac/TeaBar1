package teabar.ph.com.teabar.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jmessage.support.qiniu.android.dns.NetworkReceiver;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.ChatActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.adpter.FriendAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.bean.FirstEvent;
import teabar.ph.com.teabar.pojo.Event;
import teabar.ph.com.teabar.util.LogUtil;
import teabar.ph.com.teabar.util.SortConvList;
import teabar.ph.com.teabar.util.SortTopConvList;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class FriendFragment extends BaseFragment {

    @BindView(R.id.tv_talk_no)
    TextView tv_talk_no;
    RecyclerView rv_friend;
    FriendAdapter friendAdapter ;
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();
    List<Conversation> delFeedBack = new ArrayList<>();

    private NetworkReceiver mReceiver;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private static final int DISMISS_REFRESH_HEADER = 0x3001;
    private static final int ROAM_COMPLETED = 0x3002;
    private Activity mContext;

    public static boolean isRunning = false;
    @Override
    public int bindLayout() {
        return R.layout.fragment_friend;

    }

    @Override
    public void initView(View view) {
        isRunning=true;
        mContext = this.getActivity();
        rv_friend = view.findViewById(R.id.rv_friend);
        JMessageClient.registerEventReceiver(this);
        EventBus.getDefault().register(this);
        initConvListAdapter();
        initReceiver();

    }
    public void setNullConversation(boolean isHaveConv) {
        if (isHaveConv) {
            tv_talk_no.setVisibility(View.GONE);
        } else {
            tv_talk_no.setVisibility(View.VISIBLE);
        }
    }
    public void initConvListAdapter() {
        forCurrent.clear();
        topConv.clear();
        delFeedBack.clear();
        int i = 0;
        mDatas = JMessageClient.getConversationList();
        if (mDatas != null && mDatas.size() > 0) {
             setNullConversation(true);
            SortConvList sortConvList = new SortConvList();
            Collections.sort(mDatas, sortConvList);
            for (Conversation con : mDatas) {
                if (con.getTargetId().equals("feedback_Android")) {
                    delFeedBack.add(con);
                }
                if (!TextUtils.isEmpty(con.getExtra())) {
                    forCurrent.add(con);
                }
            }
            topConv.addAll(forCurrent);
            mDatas.removeAll(forCurrent);
            mDatas.removeAll(delFeedBack);

        } else {
         setNullConversation(false);
        }
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        friendAdapter = new FriendAdapter(getActivity(),mDatas,FriendFragment.this);
        rv_friend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_friend.setAdapter(friendAdapter);
        friendAdapter.SetOnItemClick(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),ChatActivity.class);
                intent.putExtra("targetId",  friendAdapter.getmDate().get(position).getTargetId());
                intent.putExtra("targetAppKey",friendAdapter.getmDate().get(position).getTargetAppKey());
                friendAdapter.getmDate().get(position).setUnReadMessageCnt(0);
                friendAdapter.notifyDataSetChanged();
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                customDialog1(position);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        isRunning=true;


    }

    Dialog dialog;
    private void customDialog1(final int position ) {
        dialog  = new Dialog(getActivity(), R.style.MyDialog);
        View view = View.inflate(getActivity(), R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText("删除对话");
        et_dia_name.setText("是否删除对话");
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.75f);
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
                Conversation conversation = friendAdapter.getmDate().get(position);
                UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                JMessageClient.deleteSingleConversation(userInfo.getUserName(), userInfo.getAppKey());
                mDatas.remove(conversation);
                friendAdapter.notifyDataSetChanged();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Message msg = event.getMessage();
        final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
        String targetId = userInfo.getUserName();
        Conversation conv = JMessageClient.getSingleConversation(targetId, userInfo.getAppKey());
        if (conv != null  ) {
            friendAdapter.setToTop(conv);
            friendAdapter.notifyDataSetChanged();
        }

    }


    public void onEventMainThread(FirstEvent event) {
        if ("FirstEvent".equals(event.getMsg())){
            initConvListAdapter();
        }

    }

    /**
     * 接收离线消息
     *
     * @param event 离线消息事件
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android")) {
            android.os.Message message = new android.os.Message();
            message.obj =conv;
            message.what=REFRESH_CONVERSATION_LIST;
            mHandler.sendMessage(message);

        }
    }

    /**
     * 消息撤回
     */
    public void onEvent(MessageRetractEvent event) {
        Conversation conversation = event.getConversation();
        android.os.Message message = new android.os.Message();
        message.obj =conversation;
        message.what=REFRESH_CONVERSATION_LIST;
        mHandler.sendMessage(message);

    }

    /**
     * 消息已读事件
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        friendAdapter.notifyDataSetChanged();
    }

    /**
     * 消息漫游完成事件
     *
     * @param event 漫游完成后， 刷新会话事件
     */
    public void onEvent(ConversationRefreshEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android")) {
            android.os.Message message = new android.os.Message();
            message.obj =conv;
            message.what=REFRESH_CONVERSATION_LIST;
            mHandler.sendMessage(message);

            //多端在线未读数改变时刷新
            if (event.getReason().equals(ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED)) {
                android.os.Message message1 = new android.os.Message();
                message.obj =conv;
                message.what=REFRESH_CONVERSATION_LIST;
                mHandler.sendMessage(message1);

            }
        }
    }


    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(mReceiver, filter);
    }

    //监听网络状态的广播
    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                    ToastUtil.showShort(mContext,"当前网络不可用, 请检查您的网络设置");
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    friendAdapter.setToTop(conv);
                    break;
                case DISMISS_REFRESH_HEADER:
//                    mContext.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            mConvListView.dismissLoadingHeader();
//                        }
//                    });
                    break;
                case ROAM_COMPLETED:
                    conv = (Conversation) msg.obj;
                    friendAdapter.addAndSort(conv);
                    break;
            }
        }
    };


    public void onEventMainThread(Event event) {
        switch (event.getType()) {
            case createConversation:
                Conversation conv = event.getConversation();
                if (conv != null) {
                    friendAdapter.addNewConversation(conv);
                }
                break;
            case deleteConversation:
                conv = event.getConversation();
                if (null != conv) {
                   friendAdapter.deleteConversation(conv);
                }
                break;
            //收到保存为草稿事件
            case draft:
                conv = event.getConversation();
                String draft = event.getDraft();
                //如果草稿内容不为空，保存，并且置顶该会话
                if (!TextUtils.isEmpty(draft)) {
                   friendAdapter.putDraftToMap(conv, draft);
                   friendAdapter.setToTop(conv);
                    //否则删除
                } else {
                     friendAdapter.delDraftFromMap(conv);
                }
                break;
            case addFriend:
                break;
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        isRunning=false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver!=null)
            mContext.unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }
}
