package teabar.ph.com.teabar.activity.device;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.MethodAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;


public class AddMethodActivity extends BaseActivity {

    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.iv_drink_scup)
    ImageView iv_drink_scup;
    @BindView(R.id.iv_drink_bcup)
    ImageView iv_drink_bcup;
    @BindView(R.id.tv_add_min)
    TextView tv_add_min;
    @BindView(R.id.tv_add_wd1)
    TextView tv_add_wd1;
    MyApplication application;
    MethodAdapter methodAdapter;
    List<String> mList = new ArrayList<>();
    private RangeSeekBar seekbar1,seekbar2;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_addmethod;
    }

    @Override
    public void initView(View view) {

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);

        initView();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    public void initView(){
        seekbar1 = findViewById(R.id.seekbar1);
        seekbar1.setValue(8);
        seekbar1.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                tv_add_min.setText((int) leftValue+"");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //do what you want!!
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //do what you want!!


            }
        });

        seekbar2 = findViewById(R.id.seekbar2);
        seekbar2.setValue(60);
        seekbar2.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                tv_add_wd1.setText((int) leftValue+"");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //do what you want!!
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //do what you want!!


            }
        });
    }

    @OnClick({ R.id.iv_power_fh,R.id.rl_drink_smal,R.id.rl_drink_big})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;
            case R.id.rl_drink_smal:

                break;

            case R.id.rl_drink_big:

                break;
        }
    }
}
