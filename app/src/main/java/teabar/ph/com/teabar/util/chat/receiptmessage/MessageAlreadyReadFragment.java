package teabar.ph.com.teabar.util.chat.receiptmessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.ToastUtil;


/**
 * Created by ${chenyn} on 2017/9/5.
 */

public class MessageAlreadyReadFragment extends BaseFragment {
    private Activity mContext;
    private View mRootView;
    private ListView mReceipt_alreadyRead;
    private AlreadyReadAdapter mAdapter;
    private long mGroupId;

    public MessageAlreadyReadFragment(long groupIdForReceipt) {
        this.mGroupId = groupIdForReceipt;
    }
    public MessageAlreadyReadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();


    }



    private void initListViewClick() {
        mReceipt_alreadyRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = (UserInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent();
//                if (userInfo.isFriend()) {
//                    intent.setClass(mContext, FriendInfoActivity.class);
//                }else {
//                    intent.setClass(mContext, GroupNotFriendActivity.class);
//                }
//                intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
//                intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
//                intent.putExtra(JGApplication.GROUP_ID, mGroupId);
//                startActivity(intent);
                ToastUtil.showShort(getActivity(),"DDDDDDDDDDD");
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_receipt_already_read;
    }

    @Override
    public void initView(View view) {
        mReceipt_alreadyRead = (ListView) mRootView.findViewById(R.id.receipt_alreadyRead);
        mAdapter = new AlreadyReadAdapter(this);
        mReceipt_alreadyRead.setAdapter(mAdapter);

        initListViewClick();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
