package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.view.RadarChart02View;


public class  PowerpicActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.rcv_power)
    RadarChart02View rcv_power;
    @BindView(R.id.tv_power_score)
    TextView tv_power_score;
    MyApplication application;
    List<Tea> teaList  ;
    double bodyGrades,nutritionGrades,mindGradesc,lifeGrades;
    double allGreade;
    @Override
    public void initParms(Bundle parms) {
        bodyGrades= parms.getDouble("bodyGrades");
        nutritionGrades= parms.getDouble("nutritionGrades");
        mindGradesc= parms.getDouble("mindGradesc");
        lifeGrades= parms.getDouble("lifeGrades");
        teaList = (List<Tea>) parms.getSerializable("teaList");
        allGreade = bodyGrades+nutritionGrades+mindGradesc+lifeGrades;
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_powerpic;
    }

    @Override
    public void initView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        LinkedList<Double> dataSeriesA= new LinkedList<Double>();
        dataSeriesA.add(bodyGrades);
        dataSeriesA.add(lifeGrades);
        dataSeriesA.add(nutritionGrades);
        dataSeriesA.add(mindGradesc);
        rcv_power.setData(dataSeriesA);
        tv_power_score.setText(allGreade+"分");
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({ R.id.bt_power_next})
    public void onClick(View view){
        switch (view.getId()){
            case  R.id.bt_power_next:
                Intent intent = new Intent( this,Recommend1Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("teaList", (Serializable) teaList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
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
