package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.NetWorkUtil;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.view.NoSrcollViewPage;


public class FriendCircleFragment extends BaseFragment {
    RefreshLayout refreshLayou;
    RecyclerView rv_social;
    @Override
    public int bindLayout() {
        return R.layout.fragment_friendcircle;
    }

    @Override
    public void initView(View view) {

         rv_social = view.findViewById(R.id.rv_social);
        refreshLayou = view. findViewById(R.id.refreshLayout_circle);
        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
                 /*   repairLists.clear();
                    pageNum=1;
                    getRepairList();*/
                }else {
                    ToastUtil.showShort(getActivity(),"无网络可用，请检查网络");
                }

            }

        });

        refreshLayou.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
//                    refreshLayout.finishLoadMore(5000,false,false);
                   /* pageNum++;
                    getRepairList();*/
                }else {
                    ToastUtil.showShort( getActivity(),"无网络可用，请检查网络");
                }
            }
        });


    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
