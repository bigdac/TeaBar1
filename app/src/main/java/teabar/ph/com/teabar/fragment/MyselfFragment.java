package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.DrinkNumActivity;
import teabar.ph.com.teabar.activity.my.FavoriteActivity;
import teabar.ph.com.teabar.activity.MyIssueActivity;
import teabar.ph.com.teabar.activity.my.MyPlanActivity;
import teabar.ph.com.teabar.activity.question.MyQuestionActivity;
import teabar.ph.com.teabar.activity.my.NearestActivity;
import teabar.ph.com.teabar.activity.my.PersonnalActivity;
import teabar.ph.com.teabar.activity.my.SettingActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.GlideCircleTransform;
import teabar.ph.com.teabar.view.VerticalProgressBar;

public class MyselfFragment extends BaseFragment {
    VerticalProgressBar vp_progress;
    SharedPreferences preferences;
    @BindView(R.id.tv_my_name)
    TextView tv_my_name;
    @BindView(R.id.tv_id_2)
    TextView tv_id_2;
    @BindView(R.id.iv_my_pic)
    ImageView iv_my_pic;
    @Override
    public int bindLayout() {
        return R.layout.fragment_myself;

    }

    @Override
    public void initView(View view) {
        preferences = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
        String name = preferences.getString("userName","");
        String id = preferences.getString("userId","");
        String photoUrl = preferences.getString("photoUrl","");
        if (!TextUtils.isEmpty(photoUrl)){
            Glide.with(getActivity()).load(photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(getActivity())).into(iv_my_pic);
        }
        tv_my_name.setText(name);
        tv_id_2.setText(id+"");
         vp_progress = view.findViewById(R.id.vp_progress);
        vp_progress.setProgress(40);

    }

    @Override
    public void onStart() {
        super.onStart();
        String photo = preferences.getString("photo","");
        if (!TextUtils.isEmpty(photo)){
            File file = new File(photo);
            Glide.with(getActivity()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(getActivity())).into(iv_my_pic);

        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.rl_my_jh,R.id.rl_my_fb,R.id.rl_my_ask,R.id.rl_my_sz,R.id.vp_progress,R.id.iv_may_bj,R.id.rl_my_like,R.id.rl_my_nearest})
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

            case R.id.rl_my_nearest:
                startActivity(new Intent(getActivity(),NearestActivity.class));
                break;

            case R.id.rl_my_like:
                startActivity(new Intent(getActivity(),FavoriteActivity.class));
                break;
        }
    }
}
