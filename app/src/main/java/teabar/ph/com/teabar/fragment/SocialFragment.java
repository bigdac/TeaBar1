package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.social.AddSocialActivity;
import teabar.ph.com.teabar.activity.chat.FriendListActivity;
import teabar.ph.com.teabar.activity.social.SocialInformActivity;
import teabar.ph.com.teabar.base.BaseFragment;



public class SocialFragment extends BaseFragment {

    TextView tv_login_regist,tv_login_login;
    FriendCircleFragment2 friendCircleFragment;
    FriendFragment friendFragment;
    RelativeLayout rl_social_inform;
    ImageView iv_social_friend;
    @BindView(R.id.tv_social_mes)
    TextView tv_social_mes;
    @BindView(R.id.tv_hasmess)
    TextView tv_hasmess;
    ImageView iv_main_add;
    private List<Conversation> mDatas = new ArrayList<Conversation>();

    @Override
    public int bindLayout() {
        return R.layout.fragment_social;
    }

    @Override
    public void initView(View view) {
        iv_main_add =view.findViewById(R.id.iv_main_add);
        iv_main_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddSocialActivity.class));
            }
        });
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        friendCircleFragment=new FriendCircleFragment2();
        friendFragment = new FriendFragment();
        fragmentTransaction.replace(R.id.li_social,friendCircleFragment ).commit();
        tv_login_regist = view.findViewById(R.id.tv_login_regist);
        tv_login_login = view.findViewById(R.id.tv_login_login);
        iv_social_friend = view.findViewById(R.id.iv_social_friend);
        iv_social_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FriendListActivity.class);
                startActivityForResult(intent,1000);
            }
        });

        rl_social_inform = view.findViewById(R.id.rl_social_inform);
        rl_social_inform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_social_mes.setVisibility(View.INVISIBLE);
                tv_hasmess.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getActivity(),SocialInformActivity.class));
            }
        });
        tv_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_login_regist.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                tv_login_regist.setTextColor(getActivity().getResources().getColor(R.color.login_black));
                tv_login_login.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv_login_login.setTextColor(getActivity().getResources().getColor(R.color.social_gray));
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.li_social,friendFragment ).commit();
                rl_social_inform.setVisibility(View.VISIBLE);
                iv_social_friend.setVisibility(View.VISIBLE);
                tv_hasmess.setVisibility(View.INVISIBLE);
                iv_main_add.setVisibility(View.INVISIBLE);

            }
        });
        tv_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_login_login.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                tv_login_login.setTextColor(getActivity().getResources().getColor(R.color.login_black));
                tv_login_regist.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv_login_regist.setTextColor(getActivity().getResources().getColor(R.color.social_gray));
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.li_social,friendCircleFragment ).commit();
                rl_social_inform.setVisibility(View.INVISIBLE);
                iv_social_friend.setVisibility(View.INVISIBLE);
                iv_main_add.setVisibility(View.VISIBLE);

            }
        });
        mDatas = JMessageClient.getConversationList();
        if (mDatas != null && mDatas.size() > 0) {
            tv_hasmess.setVisibility(View.VISIBLE);
        }

    }




        public void Refrashfriend(){
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            FriendFragment friendFragment = new FriendFragment();
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.li_social,friendFragment ).commit();
        }
    public void RefrashView(){
        tv_social_mes.setVisibility(View.VISIBLE);
        tv_hasmess.setVisibility(View.VISIBLE);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
