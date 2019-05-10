package teabar.ph.com.teabar.activity.question;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.adpter.Recommend1Adapter;
import teabar.ph.com.teabar.adpter.RecommendAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class Recommend1Activity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.rv_recommend)
    RecyclerView rv_recommend ;
    List<Tea> list = new ArrayList<>();
    MyApplication application;
    Recommend1Adapter recommendAdapter;
    SharedPreferences preferences;
    String userId;
    @Override
    public void initParms(Bundle parms) {
       list = (List<Tea>) parms.getSerializable("teaList");


    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_recommend1;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        recommendAdapter  = new Recommend1Adapter(this,list );
        rv_recommend.setLayoutManager(new GridLayoutManager(this,2));
        rv_recommend.setAdapter(recommendAdapter);
        recommendAdapter.SetOnclickLister(new Recommend1Adapter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
                Intent intent = new Intent(Recommend1Activity.this,MakeActivity.class);
                intent.putExtra("teaId",recommendAdapter.getmData().get(position).getId());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.iv_power_fh  })
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                startActivity(MainActivity.class);
                break;
        }
    }




    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText( this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    application.removeAllActivity();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
