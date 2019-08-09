package teabar.ph.com.teabar.activity.my;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Plan;

public class PlanDialog extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_am)
    TextView tv_am;
    @BindView(R.id.tv_pm)
    TextView tv_pm;
    @BindView(R.id.img_am)
    ImageView img_am;
    @BindView(R.id.img_pm)
    ImageView img_pm;
    public PlanDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_plan_timer);
        unbinder= ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (plan!=null){
            updatePlan(plan);
        }
    }
    public void updatePlan(Plan plan){
        tv_am.setText(plan.getAmTime());
        tv_pm.setText(plan.getPmTime());
        int amFlag=plan.getAmFlag();
        int pmFlag=plan.getPmFlag();
        if (amFlag==1){
            img_am.setImageResource(R.mipmap.plan_open);
        }else {
            img_am.setImageResource(R.mipmap.plan_close);
        }
        if (pmFlag==1){
            img_pm.setImageResource(R.mipmap.plan_open);
        }else {
            img_pm.setImageResource(R.mipmap.plan_close);
        }
    }
    @OnClick({R.id.img_am,R.id.img_pm})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_am:
                if (listener!=null){
                    listener.OnAmClickListener();
                }
                break;
            case R.id.img_pm:
                if (listener!=null){
                    listener.OnPmClickListener();
                }
                break;
        }
    }
    Plan plan;

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public AmOrPmClickListener listener;
    public interface AmOrPmClickListener{
        void OnAmClickListener();
        void OnPmClickListener();
    }

    public void setListener(AmOrPmClickListener listener) {
        this.listener = listener;
    }
}
