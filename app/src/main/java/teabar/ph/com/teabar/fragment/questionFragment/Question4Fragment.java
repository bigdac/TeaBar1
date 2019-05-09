package teabar.ph.com.teabar.fragment.questionFragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;


public class Question4Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.iv_question_man)
    ImageView iv_question_man;
    @BindView(R.id.iv_question_women)
    ImageView iv_question_women;

    @Override
    public int bindLayout() {
        return R.layout.fragment_question4;
    }

    @Override
    public void initView(View view) {

    }
    int choose = 1;
    @OnClick({R.id.bt_question1_esure,R.id.li_question_man,R.id.li_question_women})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_question1_esure:
                ((BaseQuestionActivity)getActivity()).setMesssage1(choose);
                ((BaseQuestionActivity)getActivity()).rePlaceFragment(4);
                break;

            case R.id.li_question_women:
                iv_question_women.setImageResource(R.mipmap.choose_nv);
                iv_question_man.setImageResource(R.mipmap.set_xz2);
                choose=0;
                break;
            case R.id.li_question_man:
                iv_question_women.setImageResource(R.mipmap.set_xz2);
                iv_question_man.setImageResource(R.mipmap.choose_nan);
                choose =1 ;
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
