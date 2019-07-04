package teabar.ph.com.teabar.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.FriendFragment;
import teabar.ph.com.teabar.util.SharePreferenceManager;
import teabar.ph.com.teabar.util.SortConvList;
import teabar.ph.com.teabar.util.SortTopConvList;
import teabar.ph.com.teabar.util.ThreadUtil;
import teabar.ph.com.teabar.util.TimeFormat;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyviewHolder> {

    private List<String> mData;
    private Context context;
    private  OnItemClickListener onItemClickListener;
    private List<Conversation> mDatas;

    private Map<String, String> mDraftMap = new HashMap<>();
    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3003;
    private SparseBooleanArray mArray = new SparseBooleanArray();
    private SparseBooleanArray mAtAll = new SparseBooleanArray();
    private HashMap<Conversation, Integer> mAtConvMap = new HashMap<>();
    private HashMap<Conversation, Integer> mAtAllConv = new HashMap<>();
    private UserInfo mUserInfo;
    private GroupInfo mGroupInfo;
    FriendFragment friendFragment;

    public FriendAdapter(Context context , List<Conversation> data ,FriendFragment friendFragment) {
        this.context = context;
        this. friendFragment =friendFragment;
        this.mDatas = data;

    }

    public List<Conversation> getmDate (){
        return mDatas;
    }
    public void RemovemDate (Conversation conversation){
        mData.remove(conversation);
        notifyDataSetChanged();
    }
    /**
     * 收到消息后将会话置顶
     *
     * @param conv 要置顶的会话
     */
    public void setToTop(Conversation conv) {
        int oldCount = 0;
        int newCount = 0;
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {

                friendFragment.setNullConversation(true);
            }
        });

        //如果是旧的会话
        for (Conversation conversation : mDatas) {
            if (conv.getId().equals(conversation.getId())) {
                //如果是置顶的,就直接把消息插入,会话在list中的顺序不变
                if (!TextUtils.isEmpty(conv.getExtra())) {
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                    //这里一定要return掉,要不还会走到for循环之后的方法,就会再次添加会话
                    return;
                    //如果不是置顶的,就在集合中把原来的那条消息移出,然后去掉置顶的消息数量,插入到集合中
                } else {
                    //因为后面要改变排序位置,这里要删除
                    mDatas.remove(conversation);
                    //这里要排序,因为第一次登录有漫游消息.离线消息(其中群组变化也是用这个事件下发的);所以有可能会话的最后一条消息
                    //时间比较早,但是事件下发比较晚,这就导致乱序.所以要重新排序.

                    //排序规则,每一个进来的会话去和倒叙list中的会话比较时间,如果进来的会话的最后一条消息就是最早创建的
                    //那么这个会话自然就是最后一个.所以直接跳出循环,否则就一个个向前比较.
                    for (int i = mDatas.size(); i > SharePreferenceManager.getCancelTopSize(); i--) {
                        if (conv.getLatestMessage() != null && mDatas.get(i - 1).getLatestMessage() != null) {
                            if (conv.getLatestMessage().getCreateTime() > mDatas.get(i - 1).getLatestMessage().getCreateTime()) {
                                oldCount = i - 1;
                            } else {
                                oldCount = i;
                                break;
                            }
                        } else {
                            oldCount = i;
                        }
                    }
                    mDatas.add(oldCount, conv);
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                    return;
                }
            }
        }
        if (mDatas.size() == 0) {
            mDatas.add(conv);
        } else {
            //如果是新的会话,直接去掉置顶的消息数之后就插入到list中
            for (int i = mDatas.size(); i > SharePreferenceManager.getCancelTopSize(); i--) {
                if (conv.getLatestMessage() != null && mDatas.get(i - 1).getLatestMessage() != null) {
                    if (conv.getLatestMessage().getCreateTime() > mDatas.get(i - 1).getLatestMessage().getCreateTime()) {
                        newCount = i - 1;
                    } else {
                        newCount = i;
                        break;
                    }
                } else {
                    newCount = i;
                }
            }
            mDatas.add(newCount, conv);
        }
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    //置顶会话
    public void setConvTop(Conversation conversation) {
        int count = 0;
        //遍历原有的会话,得到有几个会话是置顶的
        for (Conversation conv : mDatas) {
            if (!TextUtils.isEmpty(conv.getExtra())) {
                count++;
            }
        }
        conversation.updateConversationExtra(count + "");
        mDatas.remove(conversation);
        mDatas.add(count, conversation);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);

    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {

        myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view,position);
            }
        });
        myviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClickListener.onLongItemClick(view,position);
                return false;
            }
        });
        final Conversation convItem = mDatas.get(position);

        String draft = mDraftMap.get(convItem.getId());

        //如果会话草稿为空,显示最后一条消息
        if (TextUtils.isEmpty(draft)) {
            Message lastMsg = convItem.getLatestMessage();
            if (lastMsg != null) {
                TimeFormat timeFormat = new TimeFormat(context, lastMsg.getCreateTime());
//                //会话界面时间
                myviewHolder. datetime.setText(timeFormat.getTime());
                String contentStr;
                switch (lastMsg.getContentType()) {
//                    case image:
//                        contentStr = mContext.getString(R.string.type_picture);
//                        break;
//                    case voice:
//                        contentStr = mContext.getString(R.string.type_voice);
//                        break;
//                    case location:
//                        contentStr = mContext.getString(R.string.type_location);
//                        break;
//                    case file:
//                        String extra = lastMsg.getContent().getStringExtra("video");
//                        if (!TextUtils.isEmpty(extra)) {
//                            contentStr = mContext.getString(R.string.type_smallvideo);
//                        } else {
//                            contentStr = mContext.getString(R.string.type_file);
//                        }
//                        break;
//                    case video:
//                        contentStr = mContext.getString(R.string.type_video);
//                        break;
//                    case eventNotification:
//                        contentStr = mContext.getString(R.string.group_notification);
//                        break;
//                    case custom:
//                        CustomContent customContent = (CustomContent) lastMsg.getContent();
//                        Boolean isBlackListHint = customContent.getBooleanValue("blackList");
//                        if (isBlackListHint != null && isBlackListHint) {
//                            contentStr = mContext.getString(R.string.jmui_server_803008);
//                        } else {
//                            contentStr = mContext.getString(R.string.type_custom);
//                        }
//                        break;
                    case prompt:
                        contentStr = ((PromptContent) lastMsg.getContent()).getPromptText();
                        break;
                    default:
                        contentStr = ((TextContent) lastMsg.getContent()).getText();
                        break;
                }

                MessageContent msgContent = lastMsg.getContent();
                Boolean isRead = msgContent.getBooleanExtra("isRead");
                Boolean isReadAtAll = msgContent.getBooleanExtra("isReadAtAll");
                if (lastMsg.isAtMe()) {
                    if (null != isRead && isRead) {
                        mArray.delete(position);
                        mAtConvMap.remove(convItem);
                    } else {
                        mArray.put(position, true);
                    }
                }
                if (lastMsg.isAtAll()) {
                    if (null != isReadAtAll && isReadAtAll) {
                        mAtAll.delete(position);
                        mAtAllConv.remove(convItem);
                    } else {
                        mAtAll.put(position, true);
                    }

                }
                long gid = 0;
                if (convItem.getType().equals(ConversationType.group)) {
                    gid = Long.parseLong(convItem.getTargetId());
                }

                if (mAtAll.get(position) && MyApplication.isAtall.get(gid) != null && MyApplication.isAtall.get(gid)) {
                    contentStr = "[@所有人] " + contentStr;
                    SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    myviewHolder.  content.setText(builder);
                } else if (mArray.get(position) && MyApplication.isAtMe.get(gid) != null && MyApplication.isAtMe.get(gid)) {
                    //有人@我 文字提示
                    contentStr = context.getString(R.string.somebody_at_me) + contentStr;
                    SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                    builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    myviewHolder.  content.setText(builder);
                } else {
                    if (lastMsg.getTargetType() == ConversationType.group && !contentStr.equals("[群成员变动]")) {
                        UserInfo info = lastMsg.getFromUser();
                        String fromName = info.getDisplayName();
                        if (MyApplication.isAtall.get(gid) != null && MyApplication.isAtall.get(gid)) {
                            myviewHolder.   content.setText("[@所有人] " + fromName + ": " + contentStr);
                        } else if (MyApplication.isAtMe.get(gid) != null && MyApplication.isAtMe.get(gid)) {
                            myviewHolder.  content.setText("[有人@我] " + fromName + ": " + contentStr);
                            //如果content是撤回的那么就不显示最后一条消息的发起人名字了
                        } else if (msgContent.getContentType() == ContentType.prompt) {
                            myviewHolder.  content.setText(contentStr);
                        } else if (info.getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                            myviewHolder.  content.setText(contentStr);
                        } else {
                            myviewHolder.  content.setText(fromName + ": " + contentStr);
                        }
                    } else {
                        if (MyApplication.isAtall.get(gid) != null && MyApplication.isAtall.get(gid)) {
                            myviewHolder.    content.setText("[@所有人] " + contentStr);
                        } else if (MyApplication.isAtMe.get(gid) != null && MyApplication.isAtMe.get(gid)) {
                            myviewHolder.    content.setText("[有人@我] " + contentStr);
                        } else {
                            if (lastMsg.getUnreceiptCnt() == 0) {
                                if (lastMsg.getTargetType().equals(ConversationType.single) &&
                                        lastMsg.getDirect().equals(MessageDirect.send) &&
                                        !lastMsg.getContentType().equals(ContentType.prompt) &&
                                        //排除自己给自己发送消息
                                        !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                                    myviewHolder.    content.setText(/*"[已读]" +*/ contentStr);
                                } else {
                                    myviewHolder.     content.setText(contentStr);
                                }
                            } else {
                                if (lastMsg.getTargetType().equals(ConversationType.single) &&
                                        lastMsg.getDirect().equals(MessageDirect.send) &&
                                        !lastMsg.getContentType().equals(ContentType.prompt) &&
                                        !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
//                                    contentStr = "[未读]" + contentStr;
//                                    SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
//                                    builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.line_normal)),
//                                            0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    myviewHolder.    content.setText(contentStr);
                                } else {
                                    myviewHolder.   content.setText(contentStr);
                                }

                            }
                        }
                    }
                }
            } else {
                if (convItem.getLastMsgDate() == 0) {
                    //会话列表时间展示的是最后一条会话,那么如果最后一条消息是空的话就不显示
                    myviewHolder.   datetime.setText("");
                    myviewHolder.    content.setText("");
                } else {
                    TimeFormat timeFormat = new TimeFormat(context, convItem.getLastMsgDate());
                    myviewHolder.   datetime.setText(timeFormat.getTime());
                    myviewHolder.   content.setText("");
                }
            }
        } else {
//            draft = "[草稿]" + draft;
            SpannableStringBuilder builder = new SpannableStringBuilder(draft);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            myviewHolder.   content.setText(builder);
        }

        if (convItem.getType().equals(ConversationType.single)) {

            myviewHolder.    convName.setText(convItem.getTitle());
            mUserInfo = (UserInfo) convItem.getTargetInfo();
            if (mUserInfo != null) {
                mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            myviewHolder.headIcon.setImageBitmap(bitmap);
                        } else {
//                            myviewHolder.     headIcon.setImageResource(R.drawable.jmui_head_icon);
                            Glide.with(context).load(((UserInfo) mDatas.get(position).getTargetInfo()).getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new teabar.ph.com.teabar.util.GlideCircleTransform(context)).into( myviewHolder.headIcon);

                        }
                    }
                });
            } else {
//                myviewHolder.  headIcon.setImageResource(R.drawable.jmui_head_icon);
                Glide.with(context).load(((UserInfo) mDatas.get(position).getTargetInfo()).getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new teabar.ph.com.teabar.util.GlideCircleTransform(context)).into( myviewHolder.headIcon);

            }

        myviewHolder. convName.setText(convItem.getTitle());
        }

        if (convItem.getUnReadMsgCnt() > 0) {

            myviewHolder.  newMsgNumber.setVisibility(View.GONE);
            if (convItem.getType().equals(ConversationType.single)) {
                if (mUserInfo != null && mUserInfo.getNoDisturb() == 1) {

                } else {
                    myviewHolder.   newMsgNumber.setVisibility(View.VISIBLE);
                }
                if (convItem.getUnReadMsgCnt() < 100) {
                    myviewHolder.    newMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
                } else {
                    myviewHolder. newMsgNumber.setText("99+");
                }
            }

        } else {

           myviewHolder. newMsgNumber.setVisibility(View.GONE);
        }

    }

    public void sortConvList() {
        forCurrent.clear();
        topConv.clear();
        int i = 0;
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);
        for (Conversation con : mDatas) {
            if (!TextUtils.isEmpty(con.getExtra())) {
                forCurrent.add(con);
            }
        }
        topConv.addAll(forCurrent);
        mDatas.removeAll(forCurrent);
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        notifyDataSetChanged();
    }

    public void addNewConversation(Conversation conv) {
        mDatas.add(0, conv);
        if (mDatas.size() > 0) {
            friendFragment.setNullConversation(true);
        } else {
            friendFragment.setNullConversation(false);
        }
        notifyDataSetChanged();
    }

    public void addAndSort(Conversation conv) {
        mDatas.add(conv);
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);
        notifyDataSetChanged();
    }

    public void deleteConversation(Conversation conversation) {
        mDatas.remove(conversation);
        notifyDataSetChanged();
    }

    public void putDraftToMap(Conversation conv, String draft) {
        mDraftMap.put(conv.getId(), draft);
    }

    public void delDraftFromMap(Conversation conv) {
        mArray.delete(mDatas.indexOf(conv));
        mAtConvMap.remove(conv);
        mAtAllConv.remove(conv);
        mDraftMap.remove(conv.getId());
        notifyDataSetChanged();
    }

    public String getDraft(String convId) {
        return mDraftMap.get(convId);
    }

    public boolean includeAtMsg(Conversation conv) {
        if (mAtConvMap.size() > 0) {
            Iterator<Map.Entry<Conversation, Integer>> iterator = mAtConvMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Conversation, Integer> entry = iterator.next();
                if (conv == entry.getKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public   interface  OnItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view , int position);
    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
    public boolean includeAtAllMsg(Conversation conv) {
        if (mAtAllConv.size() > 0) {
            Iterator<Map.Entry<Conversation, Integer>> iterator = mAtAllConv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Conversation, Integer> entry = iterator.next();
                if (conv == entry.getKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getAtMsgId(Conversation conv) {
        return mAtConvMap.get(conv);
    }

    public int getatAllMsgId(Conversation conv) {
        return mAtAllConv.get(conv);
    }

    public void putAtConv(Conversation conv, int msgId) {
        mAtConvMap.put(conv, msgId);
    }

    public void putAtAllConv(Conversation conv, int msgId) {
        mAtAllConv.put(conv, msgId);
    }

    private static class UIHandler extends Handler {

        private final WeakReference<FriendAdapter> mAdapter;

        public UIHandler(FriendAdapter adapter) {
            mAdapter = new WeakReference<>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            FriendAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();
    //取消会话置顶
        public void setCancelConvTop(Conversation conversation) {
        forCurrent.clear();
        topConv.clear();
        int i = 0;
        for (Conversation oldConv : mDatas) {
            //在原来的会话中找到取消置顶的这个,添加到待删除中
            if (oldConv.getId().equals(conversation.getId())) {
                oldConv.updateConversationExtra("");
                break;
            }
        }
        //全部会话排序
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);

        //遍历会话找到原来设置置顶的
        for (Conversation con : mDatas) {
            if (!TextUtils.isEmpty(con.getExtra())) {
                forCurrent.add(con);
            }
        }
        topConv.addAll(forCurrent);
        SharePreferenceManager.setCancelTopSize(topConv.size());
        mDatas.removeAll(forCurrent);
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView headIcon;
        TextView convName,content,datetime,newMsgNumber;
        public MyviewHolder(View itemView){
            super(itemView);
            headIcon =itemView.findViewById(R.id.iv_talk_pic);
             convName = itemView.findViewById(  R.id.tv_talk_name);
             content = itemView.findViewById(  R.id.tv_message);
             datetime = itemView.findViewById(  R.id.tv_talk_time);
             newMsgNumber = itemView.findViewById(  R.id.tv_talk_num);
        }
    }
}
