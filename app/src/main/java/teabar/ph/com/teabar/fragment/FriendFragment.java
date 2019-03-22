package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.FriendAdapter;
import teabar.ph.com.teabar.base.BaseFragment;

public class FriendFragment extends BaseFragment {
    RecyclerView rv_friend;
    FriendAdapter friendAdapter ;
    List<String> list = new ArrayList<>();
    @Override
    public int bindLayout() {
        return R.layout.fragment_friend;

    }

    @Override
    public void initView(View view) {
        rv_friend = view.findViewById(R.id.rv_friend);
        for (int i = 0;i<10;i++){
            list.add(i+"");
        }
        friendAdapter = new FriendAdapter(getActivity(),list);
        rv_friend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_friend.setAdapter(friendAdapter);

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
