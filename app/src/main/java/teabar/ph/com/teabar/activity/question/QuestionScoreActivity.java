package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.ScoreRecords;
import teabar.ph.com.teabar.view.RadarChart02View;

public class QuestionScoreActivity extends BaseActivity {
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.rcv_power)
    RadarChart02View rcv_power;
    @BindView(R.id.tv_power_score)
    TextView tv_power_score;
    @BindView(R.id.tv_tea1)
    TextView tv_tea1;
    @BindView(R.id.tv_tea1_mes)
    TextView tv_tea1_mes;
    @BindView(R.id.tv_tea2)
    TextView tv_tea2;
    @BindView(R.id.tv_tea2_mes)
    TextView tv_tea2_mes;
    @BindView(R.id.tv_tea3)
    TextView tv_tea3;
    @BindView(R.id.tv_tea3_mes)
    TextView tv_tea3_mes;
    @BindView(R.id.tv_power)
     TextView tv_power;
    @BindView(R.id.tv_ques_score1)
    TextView tv_ques_score1;
    @BindView(R.id.tv_ques_score2)
    TextView tv_ques_score2;
    @BindView(R.id.tv_ques_score3)
    TextView tv_ques_score3;
    @BindView(R.id.tv_ques_score4)
    TextView tv_ques_score4;
    MyApplication application;
    double allGreade;
    ScoreRecords scoreRecords ;
    String name ;
    double bodyGrades,nutritionGrades,mindGradesc,lifeGrades;
    @Override
    public void initParms(Bundle parms) {
        scoreRecords =(ScoreRecords) parms.getSerializable("ScoreRecords");
        name = parms.getString("name");
        if (scoreRecords!=null){
        bodyGrades = Double.valueOf(scoreRecords.getBodyGrades());
        nutritionGrades = Double.valueOf(scoreRecords.getNutritionGrades());
        mindGradesc = Double.valueOf(scoreRecords.getMindGrades());
        lifeGrades = Double.valueOf(scoreRecords.getLifeGrades());
        allGreade = bodyGrades+nutritionGrades+mindGradesc+lifeGrades;
        }

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_questionscore;
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
        int number  = application.IsEnglish();
        LinkedList<Double> dataSeriesA= new LinkedList<Double>();
        dataSeriesA.add(bodyGrades);
        dataSeriesA.add(lifeGrades);
        dataSeriesA.add(nutritionGrades);
        dataSeriesA.add(mindGradesc);
        rcv_power.setData(dataSeriesA);
        tv_power_score.setText((int) allGreade+"");
        tv_power.setText(name);
         tv_ques_score1.setText((int)bodyGrades+"");
        tv_ques_score2.setText((int)lifeGrades+"");
        tv_ques_score3.setText((int)nutritionGrades+"");
        tv_ques_score4.setText((int)mindGradesc+"");
        if (scoreRecords!=null){
//            if (number==0){
            tv_tea1.setText(scoreRecords.getProductName1En());
            tv_tea1_mes.setText(scoreRecords.getTeaName1En());
            tv_tea2.setText(scoreRecords.getProductName2En());
            tv_tea2_mes.setText(scoreRecords.getTeaName2En());
            tv_tea3.setText(scoreRecords.getProductName3En());
            tv_tea3_mes.setText(scoreRecords.getTeaName3En());
//            }

        }

    }
    @OnClick({R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;
        }
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
