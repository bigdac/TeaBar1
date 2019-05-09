package teabar.ph.com.teabar.fragment.questionFragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.ToastUtil;


public class Question5Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.et_ques_weight)
    EditText et_ques_weight;
    @BindView(R.id.et_ques_tall)
    EditText et_ques_tall;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question5;
    }

    @Override
    public void initView(View view) {

    }
    @OnClick({R.id.bt_question1_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_question1_esure:
                if (TextUtils.isEmpty(et_ques_weight.getText())){
                    ToastUtil.showShort(getActivity(),"體重不能為空");
                    return;
                }
                if (TextUtils.isEmpty(et_ques_tall.getText())){
                    ToastUtil.showShort(getActivity(),"身高不能為空");
                    return;
                }
                ((BaseQuestionActivity)getActivity()).setMesssage2(et_ques_tall.getText().toString(),et_ques_weight.getText().toString());
                ((BaseQuestionActivity)getActivity()).rePlaceFragment(5);
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
