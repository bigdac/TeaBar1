package teabar.ph.com.teabar.activity.chat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.UserEntry;
import teabar.ph.com.teabar.util.ToastUtil;


public class AddFriendActivity1 extends BaseActivity {


    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.et_reason)
    EditText et_reason;
    @BindView(R.id.tv_addfs)
    TextView tv_addfs;
    MyApplication application;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    private UserInfo mMyInfo;
    private String userName;
    private String mTargetAppKey;
    String name;
    @Override
    public void initParms(Bundle parms) {
        Intent intent = getIntent();
        userName =intent.getStringExtra("userName");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_friend1;
    }

    @Override
    public void initView(View view) {

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        name =  preferences.getString("userName","");
        if (TextUtils.isEmpty(name)) {
            et_reason.setText(getText(R.string.social_friend_wo).toString());
        } else {
            et_reason.setText(getText(R.string.social_friend_wo).toString() + name);
        }


    }



    private void sendAddReason() {
        mMyInfo = JMessageClient.getMyInfo();
        mTargetAppKey = mMyInfo.getAppKey();

        ContactManager.sendInvitationRequest(userName, null, et_reason.getText().toString().trim(), new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.e(TAG, "gotResult: -->"+responseMessage );

                if (responseCode == 0) {
//                    UserEntry userEntry = UserEntry.getUser(mMyInfo.getUserName(), mMyInfo.getAppKey());
////                    FriendRecommendEntry entry = FriendRecommendEntry.getEntry(userEntry,
////                            userName, mTargetAppKey);
////                    if (null == entry) {
////                        entry = new FriendRecommendEntry(finalUid, userName, "", finalDisplayName, mTargetAppKey,
////                                finalTargetAvatar, finalDisplayName, reason, FriendInvitation.INVITING.getValue(), userEntry, 100);
////                    } else {
////                        entry.state = FriendInvitation.INVITING.getValue();
////                        entry.reason = reason;
////                    }
////                    entry.save();
                    tipDialog.dismiss();
                   toast(  getText(R.string.toast_add_cg).toString());
                    finish();
                } else if (responseCode == 871317) {
                    toast(  getText(R.string.toast_add_you).toString());
                    tipDialog.dismiss();
                } else {
                    tipDialog.dismiss();
                    toast(  getText(R.string.toast_add_sb).toString());
                }
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

    @OnClick({ R.id.iv_power_fh,R.id.tv_addfs})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.tv_addfs:
                showProgressDialog();
                sendAddReason();
                break;
        }
    }

}
