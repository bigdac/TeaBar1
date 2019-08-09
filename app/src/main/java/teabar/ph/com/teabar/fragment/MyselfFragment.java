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

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.my.PersonnalActivity;
import teabar.ph.com.teabar.activity.my.SettingActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.GlideCircleTransform;

public class MyselfFragment extends BaseFragment {

    SharedPreferences preferences;
    @BindView(R.id.tv_my_name)
    TextView tv_my_name;
    @BindView(R.id.tv_id_2)
    TextView tv_id_2;
    @BindView(R.id.iv_my_pic)
    ImageView iv_my_pic;
    @Override
    public int bindLayout() {
        return R.layout.fragment_myself1;
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
    }

    @Override
    public void onStart() {
        super.onStart();
        String photo = preferences.getString("photo","");
        if (!TextUtils.isEmpty(photo)){

            Glide.with(getActivity()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(getActivity())).into(iv_my_pic);
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.rl_my_sz,R.id.iv_may_bj})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.rl_my_sz:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                break;
            case R.id.iv_may_bj:
                startActivityForResult(new Intent(getActivity(),PersonnalActivity.class),7300);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==7300){
            String name = preferences.getString("userName","");
            tv_my_name.setText(name);
        }
    }
}
