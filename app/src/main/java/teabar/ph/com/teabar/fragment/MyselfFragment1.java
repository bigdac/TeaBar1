package teabar.ph.com.teabar.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.DrinkNumActivity;
import teabar.ph.com.teabar.activity.my.FavoriteActivity;
import teabar.ph.com.teabar.activity.my.MyIssueActivity;
import teabar.ph.com.teabar.activity.my.MyPlanActivity;
import teabar.ph.com.teabar.activity.my.NearestActivity;
import teabar.ph.com.teabar.activity.my.PersonnalActivity;
import teabar.ph.com.teabar.activity.my.SettingActivity;
import teabar.ph.com.teabar.activity.question.MyQuestionActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.util.GlideCircleTransform;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.view.VerticalProgressBar;

public class MyselfFragment1 extends BaseFragment {
    VerticalProgressBar vp_progress;
    SharedPreferences preferences;
    @BindView(R.id.tv_my_name)
    TextView tv_my_name;
    @BindView(R.id.tv_id_2)
    TextView tv_id_2;
    @BindView(R.id.iv_my_pic)
    ImageView iv_my_pic;
    @BindView(R.id.tv_my_friend)
    TextView tv_my_friend;
    @BindView(R.id.tv_my_brew)
    TextView tv_my_brew;
    String id;
    int setNum;
    @Override
    public int bindLayout() {
        return R.layout.fragment_myself;

    }

    @Override
    public void initView(View view) {
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("my",Context.MODE_PRIVATE);
        String name = preferences.getString("userName","");
         id = preferences.getString("userId","");
        setNum = preferences.getInt("drinkNum",0);
        String photoUrl = preferences.getString("photoUrl","");
        if (!TextUtils.isEmpty(photoUrl)){
            Glide.with(getContext()).load(photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(getActivity())).into(iv_my_pic);
        }
        tv_my_name.setText(name);
        tv_id_2.setText(id+"");
        vp_progress = view.findViewById(R.id.vp_progress);
        vp_progress.setProgress(0);
        tv_my_brew.setText("Brews 0%");
        if (setNum!=0){
            new getNumAsyncTask (this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        String fNum = preferences.getString("friendNum","");
        if (!TextUtils.isEmpty(fNum)){
            tv_my_friend.setText(fNum);
        }
        findFriend();
    }
    int friendNum;
    @Override
    public void onStart() {
        super.onStart();
        String photo = preferences.getString("photo","");
        if (!TextUtils.isEmpty(photo)){
            Glide.with(getActivity()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(getActivity())).into(iv_my_pic);
        }
    }
    int num=0;
    @SuppressLint("StaticFieldLeak")
    class getNumAsyncTask extends BaseWeakAsyncTask<Void,Void,String,BaseFragment> {


        public getNumAsyncTask(BaseFragment baseFragment) {
            super(baseFragment);
        }

        @Override
        protected String doInBackground(BaseFragment baseFragment, Void... voids) {
            String code = "";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getUserDrink?userId="+id);

            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("state");
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArrayday = jsonObject1.getJSONArray("dayCount");
                num = jsonArrayday.length();

            } catch ( Exception e) {
                e.printStackTrace();
            }

            return code;
        }

        @Override
        protected void onPostExecute(BaseFragment baseFragment, String s) {
            switch (s){
                case "200":
                    if (tv_my_brew!=null){
                        int max = (Integer.valueOf(num)*100)/setNum ;
                        if (max<=100){
                            tv_my_brew.setText("Brews "+max+"%");
                            vp_progress.setProgress(max);
                        }else {
                            tv_my_brew.setText("Brews 100%");
                            vp_progress.setProgress(100);
                        }

                    }

                    break;

                default:

                    break;
            }
        }
    }

    public void findFriend(){
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                if (0 == responseCode) {
                    //获取好友列表成功
                   friendNum = userInfoList.size();
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);

                } else {
                    //获取好友列表失败

                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                if (tv_my_friend!=null) {
                    tv_my_friend.setText(friendNum + "");
                }
            }
        }
    };

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
                startActivityForResult(new Intent(getActivity(),PersonnalActivity.class),7300);
                break;

            case R.id.rl_my_nearest:
                startActivity(new Intent(getActivity(),NearestActivity.class));
                break;

            case R.id.rl_my_like:
                startActivity(new Intent(getActivity(),FavoriteActivity.class));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
