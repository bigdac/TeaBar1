package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.view.MyLayoutManager;


public class TeaFragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @Override
    public int bindLayout() {
        return R.layout.fragment_tealist;
    }

    @Override
    public void initView(View view) {
        RecyclerView recycler_view = view.findViewById(R.id.rv_tealist);
        list = new ArrayList<>();
        for (int i=0;i<20;i++){
            list.add(i+"");
        }
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), R.layout.item_mailpic,list );
        MyLayoutManager layoutManager=new MyLayoutManager(getActivity(),2);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(recyclerViewAdapter);
//        recyclerViewAdapter.setItemListener(new BaseRecyclerAdapter.ItemListener() {
//            @Override
//            public void onItemClick(BaseViewHolder holder, int position) {
//                startActivity(new Intent(getActivity(),MakeActivity.class));
//            }
//        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
