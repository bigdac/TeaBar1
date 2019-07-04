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


    @BindView(R.id.rcv_power)
    RadarChart02View rcv_power;
    @BindView(R.id.tv_power_score)
    TextView tv_power_score;
    @BindView(R.id.tv_ques_score1)
    TextView tv_ques_score1;
    @BindView(R.id.tv_ques_score2)
    TextView tv_ques_score2;
    @BindView(R.id.tv_ques_score3)
    TextView tv_ques_score3;
    @BindView(R.id.tv_ques_score4)
    TextView tv_ques_score4;
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
        tv_power_score.setText((int) allGreade+"");
        tv_ques_score1.setText((int)bodyGrades+"");
        tv_ques_score2.setText((int)lifeGrades+"");
        tv_ques_score3.setText((int)nutritionGrades+"");
        tv_ques_score4.setText((int)mindGradesc+"");
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
                    Toast.makeText( this,getText(R.string.toast_main_exit),Toast.LENGTH_SHORT).show();
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
