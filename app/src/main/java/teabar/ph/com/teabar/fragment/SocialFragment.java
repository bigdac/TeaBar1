package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.ClickViewPageAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.NetWorkUtil;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

public class SocialFragment extends BaseFragment {

   TextView tv_login_regist,tv_login_login;

    FriendCircleFragment friendCircleFragment;
    FriendFragment friendFragment;
    @Override
    public int bindLayout() {
        return R.layout.fragment_social;
    }

    @Override
    public void initView(View view) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        friendCircleFragment=new FriendCircleFragment();
        friendFragment = new FriendFragment();
        fragmentTransaction.replace(R.id.li_social,friendCircleFragment ).commit();
        tv_login_regist = view.findViewById(R.id.tv_login_regist);
        tv_login_login = view.findViewById(R.id.tv_login_login);
        tv_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_login_regist.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                tv_login_regist.setTextColor(getActivity().getResources().getColor(R.color.login_black));
                tv_login_login.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv_login_login.setTextColor(getActivity().getResources().getColor(R.color.social_gray));
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.li_social,friendFragment ).commit();

            }
        });
        tv_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_login_login.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                tv_login_login.setTextColor(getActivity().getResources().getColor(R.color.login_black));
                tv_login_regist.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv_login_regist.setTextColor(getActivity().getResources().getColor(R.color.social_gray));
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.li_social,friendCircleFragment ).commit();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
