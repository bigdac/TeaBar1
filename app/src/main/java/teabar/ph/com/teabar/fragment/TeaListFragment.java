package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.MailAdapter;
import teabar.ph.com.teabar.base.BaseFragment;

public class TeaListFragment extends BaseFragment {
    RecyclerView rv_tealist;
    MailAdapter mailAdapter;
    List<String> list = new ArrayList<>();
    @Override
    public int bindLayout() {
        return R.layout.fragment_tealist;
    }

    @Override
    public void initView(View view) {
    /*    rv_tealist = view.findViewById(R.id.rv_tealist);
        for (int i=0;i<11;i++){
            list.add(i+"");
        }
        mailAdapter= new MailAdapter(getActivity(),list);
        rv_tealist.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv_tealist.setAdapter(mailAdapter);*/
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
