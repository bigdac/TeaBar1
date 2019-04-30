package teabar.ph.com.teabar.activity.question;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.fragment.FriendCircleFragment1;
import teabar.ph.com.teabar.fragment.FriendFragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question1Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question2Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question3Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question4Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question5Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question6Fragment;

public class BaseQuestionActivity extends BaseActivity {
    @BindView(R.id.li_question)
    LinearLayout li_question;
    Question1Fragment question1Fragment;
    Question2Fragment question2Fragment;
    Question3Fragment question3Fragment;
    Question4Fragment question4Fragment;
    Question5Fragment question5Fragment;
    Question6Fragment question6Fragment;
    BaseFragment [] baseFragments;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_basequestion;
    }

    @Override
    public void initView(View view) {
         question1Fragment = new Question1Fragment();
         question2Fragment = new Question2Fragment();
         question3Fragment = new Question3Fragment();
         question4Fragment = new Question4Fragment();
         question5Fragment = new Question5Fragment();
         question6Fragment = new Question6Fragment();
         baseFragments= new BaseFragment[]{question1Fragment,question2Fragment,question3Fragment,question4Fragment,question5Fragment,question6Fragment};
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.li_question,question1Fragment ).commit();


    }

    public void  rePlaceFragment (int type){
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager =  getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.li_question,baseFragments[type]).commit();
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
