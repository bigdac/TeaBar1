package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseFragment;

public class FriendFragment extends BaseFragment {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SocialFragment friendFragment;
    Unbinder unbinder;
    @Override
    public int bindLayout() {
        return R.layout.fragment_friend;

    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(getActivity());
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        friendFragment=new SocialFragment();
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
        if (unbinder!=null)
            unbinder.unbind();
    }
}
