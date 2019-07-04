package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.ScoreRecords;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.view.ScoreView1;
import teabar.ph.com.teabar.view.ScoreView2;
import teabar.ph.com.teabar.view.ScoreView3;
import teabar.ph.com.teabar.view.ScoreView4;

public class QuestionScore1Activity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.sv_proview4)
    ScoreView4 sv_proview4;
    @BindView(R.id.sv_proview3)
    ScoreView3 sv_proview3;
    @BindView(R.id.sv_proview2)
    ScoreView2 sv_proview2;
    @BindView(R.id.sv_proview1)
    ScoreView1 sv_proview1;
    @BindView(R.id.tv_score_body)
    TextView tv_score_body;
    @BindView(R.id.tv_score_eat)
    TextView tv_score_eat;
    @BindView(R.id.tv_score_life)
    TextView tv_score_life;
    @BindView(R.id.tv_score_mood)
    TextView tv_score_mood;
    @BindView(R.id.tv_score_allscore)
    TextView tv_score_allscore;
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
    @BindView(R.id.tv_score_title)
    TextView tv_score_title;
    List<String> list=new ArrayList<>();
    double bodyGrades,nutritionGrades,mindGradesc,lifeGrades;
    double allGreade;
    List<Tea> teaList  ;
    ScoreRecords scoreRecords ;
    String name,title ;
    @Override
    public void initParms(Bundle parms) {
        scoreRecords =(ScoreRecords) parms.getSerializable("ScoreRecords");
        name = parms.getString("name");
        title = parms.getString("title");
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
        return R.layout.activity_myquestionscore1;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        tv_score_title.setText(title);
        GradientDrawable myGrad1 = (GradientDrawable)tv_score_body.getBackground();
        myGrad1.setColor(Color.parseColor("#2bdca3"));
        GradientDrawable myGrad2 = (GradientDrawable)tv_score_eat.getBackground();
        myGrad2.setColor(Color.parseColor("#8a9cf8"));
        GradientDrawable myGrad3 = (GradientDrawable)tv_score_life.getBackground();
        myGrad3.setColor(Color.parseColor("#45e3cb"));
        GradientDrawable myGrad4 = (GradientDrawable)tv_score_mood.getBackground();
        myGrad4.setColor(Color.parseColor("#c8a8fb"));
        NumberFormat nf = NumberFormat.getInstance();
        tv_score_allscore.setText(  nf.format(allGreade)+"");
        tv_score_body.setText(nf.format(bodyGrades) +"");
        tv_score_life.setText(nf.format(lifeGrades) +"");
        tv_score_eat.setText(nf.format(nutritionGrades) +"");
        tv_score_mood.setText(nf.format(mindGradesc) +"");
        sv_proview4.setCurProgress((int)mindGradesc);
        sv_proview3.setCurProgress((int)lifeGrades);
        sv_proview2.setCurProgress((int)nutritionGrades);
        sv_proview1.setCurProgress((int)bodyGrades);

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

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @OnClick({R.id.iv_score_back })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_score_back:
                finish();
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
