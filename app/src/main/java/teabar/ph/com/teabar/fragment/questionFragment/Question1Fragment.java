package teabar.ph.com.teabar.fragment.questionFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.view.MyLayoutManager;


public class Question1Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question1;
    }

    @Override
    public void initView(View view) {

    }

    @OnClick({R.id.bt_question1_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_question1_esure:
                ((BaseQuestionActivity)getActivity()).rePlaceFragment(2);
                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
