package teabar.ph.com.teabar.activity.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EqupmentInformAdapter;
import teabar.ph.com.teabar.adpter.EqupmentXqAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class EqupmentInformActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.rv_equmentxq)
    RecyclerView rv_equmentxq;

    @BindView(R.id.iv_equ_fh)
    ImageView iv_equ_fh;
    List<String> stringList;
    EqupmentInformAdapter equpmentInformAdapter;
    SharedPreferences preferences;
    String notOpen[] = {"notOpen1","notOpen2","notOpen3"};
    String mac ;
    @Override
    public void initParms(Bundle parms) {
        mac = parms.getString("mac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_inform;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        String Open = preferences.getString(mac,"111");
        Log.e(TAG, "initView: --->"+Open );

        stringList = new ArrayList<>();
        stringList.add(Open.substring(0,1));
        stringList.add(Open.substring(1,2));
        stringList.add(Open.substring(2,3));
        equpmentInformAdapter = new EqupmentInformAdapter(this,stringList);
        rv_equmentxq.setLayoutManager(new LinearLayoutManager(this));
        rv_equmentxq.setAdapter(equpmentInformAdapter);
        equpmentInformAdapter.SetOnItemClick(new EqupmentInformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List Data = equpmentInformAdapter.getmData();
                String mes = "";
                for (int i = 0;i<Data.size();i++){
                    mes = mes+Data.get(i);
                }
                Log.e(TAG, "onItemClick: ___>"+mes );
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(mac,mes);
                editor.commit();
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_equ_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;
        }
    }
}
