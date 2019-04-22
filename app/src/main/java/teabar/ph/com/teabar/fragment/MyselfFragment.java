package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.DrinkNumActivity;
import teabar.ph.com.teabar.activity.MyIssueActivity;
import teabar.ph.com.teabar.activity.MyPlanActivity;
import teabar.ph.com.teabar.activity.MyQuestionActivity;
import teabar.ph.com.teabar.activity.PersonnalActivity;
import teabar.ph.com.teabar.activity.PowerpicActivity;
import teabar.ph.com.teabar.activity.SettingActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.view.VerticalProgressBar;

public class MyselfFragment extends BaseFragment {
    VerticalProgressBar vp_progress;
    @Override
    public int bindLayout() {
        return R.layout.fragment_myself;

    }

    @Override
    public void initView(View view) {

         vp_progress = view.findViewById(R.id.vp_progress);
        vp_progress.setProgress(40);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.rl_my_jh,R.id.rl_my_fb,R.id.rl_my_ask,R.id.rl_my_sz,R.id.vp_progress,R.id.iv_may_bj})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_my_jh:
                startActivity(new Intent(getActivity(),MyPlanActivity.class));
                break;
            case R.id.rl_my_fb:
                startActivity(new Intent(getActivity(),MyIssueActivity.class));
                break;
            case R.id.rl_my_ask:
                startActivity(new Intent(getActivity(),MyQuestionActivity.class));
                break;
            case R.id.rl_my_sz:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                break;

            case R.id.vp_progress:
                startActivity(new Intent(getActivity(),DrinkNumActivity.class));
                break;

            case R.id.iv_may_bj:
                startActivity(new Intent(getActivity(),PersonnalActivity.class));
                break;
        }
    }
}
