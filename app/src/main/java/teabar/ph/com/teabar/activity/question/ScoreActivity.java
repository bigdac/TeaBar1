package teabar.ph.com.teabar.activity.question;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.Recommend1Activity;
import teabar.ph.com.teabar.adpter.AnswerAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.view.FlowTagView;
import teabar.ph.com.teabar.view.ScoreProgressBar;
import teabar.ph.com.teabar.view.ScoreView1;
import teabar.ph.com.teabar.view.ScoreView2;
import teabar.ph.com.teabar.view.ScoreView3;
import teabar.ph.com.teabar.view.ScoreView4;

public class ScoreActivity extends BaseActivity {
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
    List<String> list=new ArrayList<>();
    double bodyGrades,nutritionGrades,mindGradesc,lifeGrades;
    double allGreade;
    List<Tea> teaList  ;
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
        return R.layout.activity_score;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
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


    @OnClick({R.id.iv_score_back,R.id.bt_score_plan})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_score_back:
                startActivity(MainActivity.class);
                break;

            case R.id.bt_score_plan:
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
