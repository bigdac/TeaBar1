package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.Mode.InfoModel;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.FriendAddAdapter;
import teabar.ph.com.teabar.adpter.SocialInformAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.dialog.LoadDialog;


public class AddFriendActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.rv_friend_inform)
    RecyclerView rv_friend_inform;
    @BindView(R.id.et_add_id)
    EditText et_add_id;
    MyApplication application;
    FriendAddAdapter friendAddAdapter ;
    List<String> list = new ArrayList<>();
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        for (int i=0;i<5;i++){
            list.add(i+"");
        }
        friendAddAdapter = new FriendAddAdapter(this,list);
        rv_friend_inform.setLayoutManager(new LinearLayoutManager(this));
        rv_friend_inform.setAdapter(friendAddAdapter);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.iv_power_fh,R.id.bt_friend_search})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.bt_friend_search:
                hintKbTwo();
                String searchUserName = et_add_id.getText().toString().trim();
                if (!TextUtils.isEmpty(searchUserName)) {
                    LoadDialog.show(this);


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
}
