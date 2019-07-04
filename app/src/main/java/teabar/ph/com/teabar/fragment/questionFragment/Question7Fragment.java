package teabar.ph.com.teabar.fragment.questionFragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.ToastUtil;


public class Question7Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.iv_question1)
    ImageView iv_question1;
    @BindView(R.id.iv_question2)
    ImageView iv_question2;
    @BindView(R.id.iv_question3)
    ImageView iv_question3;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question7;
    }

    @Override
    public void initView(View view) {

    }
    int choose =0;
    @OnClick({R.id.li_question1,R.id.li_question2,R.id.li_question3,R.id.bt_question1_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.li_question1:
                iv_question1.setVisibility(View.VISIBLE);
                iv_question2.setVisibility(View.INVISIBLE);
                iv_question3.setVisibility(View.INVISIBLE);
                choose =1;
                break;
            case R.id.li_question2:
                iv_question1.setVisibility(View.INVISIBLE);
                iv_question2.setVisibility(View.VISIBLE);
                iv_question3.setVisibility(View.INVISIBLE);
                choose =2;
                break;
            case R.id.li_question3:
                iv_question1.setVisibility(View.INVISIBLE);
                iv_question2.setVisibility(View.INVISIBLE);
                iv_question3.setVisibility(View.VISIBLE);
                choose =3;
                break;
            case R.id.bt_question1_esure:
                if (choose==0){
                    ToastUtil.showShort(getActivity(),getText(R.string.question_toa_xz).toString());
                }else {
                    ((BaseQuestionActivity)getActivity()).toStarActivity(choose);
                }

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
