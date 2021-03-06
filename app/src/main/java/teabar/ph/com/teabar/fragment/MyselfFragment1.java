package teabar.ph.com.teabar.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

//我的页面
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
    TextView tv_my_friend;//好友數量
    @BindView(R.id.tv_my_brew)
    TextView tv_my_brew;
    @BindView(R.id.tv_my_plan)
    TextView tv_my_plan;//計畫數量
    String id;
    int setNum;
    Map<String,Object>  params=new HashMap<>();
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
        params.put("userId",id);

        new GetPersonPlanAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);

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
    int planSize=0;
    //获取个人计划
    class GetPersonPlanAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,BaseFragment> {


        public GetPersonPlanAsync(BaseFragment baseFragment) {
            super(baseFragment);
        }



        @Override
        protected Integer doInBackground(BaseFragment fragment, Map<String, Object>... maps) {
            String url=HttpUtils.ipAddress+"/app/getUserPlan";
            int code=0;
            Map<String,Object> map=maps[0];
            try {
                String result=HttpUtils.postOkHpptRequest(url,map);
                Log.i("GetPersonPlanAsync","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("state");
                    if (code==200){

                        JSONArray data=jsonObject.getJSONArray("data");
                        int size=data.length();
                        planSize=size;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(BaseFragment fragment1, Integer integer) {
            if (integer==200){
                tv_my_plan.setText(planSize+"");
            }
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
                Intent intent=new Intent(getActivity(),MyPlanActivity.class);
                intent.putExtra("userId",id);
                startActivity(intent);//我的個人計畫
                break;
            case R.id.rl_my_fb:
                startActivity(new Intent(getActivity(),MyIssueActivity.class));//我的發佈
                break;
            case R.id.rl_my_ask:
                startActivity(new Intent(getActivity(),MyQuestionActivity.class));//我的問卷記錄
                break;
            case R.id.rl_my_sz:
                startActivity(new Intent(getActivity(),SettingActivity.class));//我的設置
                break;

            case R.id.vp_progress:
                startActivity(new Intent(getActivity(),DrinkNumActivity.class));//茶飲頁面
                break;

            case R.id.iv_may_bj://個人設置頁面
                startActivityForResult(new Intent(getActivity(),PersonnalActivity.class),7300);
                break;

            case R.id.rl_my_nearest:
                startActivity(new Intent(getActivity(),NearestActivity.class));//最近的喝過的茶
                break;

            case R.id.rl_my_like://我的最愛
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
