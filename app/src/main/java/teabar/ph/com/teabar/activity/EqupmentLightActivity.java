package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EqupmentInformAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;

public class EqupmentLightActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.iv_equ_fh)
    ImageView iv_equ_fh;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_equipmentlight;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        application.addActivity(this);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_equ_fh ,R.id.rl_light_1,R.id.rl_light_2,R.id.rl_light_3})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;

            case R.id.rl_light_1:
                startActivity(ChooseColorActvity.class);
                break;

            case R.id.rl_light_2:
                popupmenuWindow();
                break;

            case R.id.rl_light_3:

                break;
        }
    }
    private PopupWindow popupWindow1;
    public void popupmenuWindow() {
        if (popupWindow1 != null && popupWindow1.isShowing()) {
            return;
        }

        View view = View.inflate(this, R.layout.popview_mode, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        RelativeLayout rl_mode = (RelativeLayout) view.findViewById(R.id.rl_mode);
        RelativeLayout rl_mode_1 = (RelativeLayout) view.findViewById(R.id.rl_mode_1);
        RelativeLayout rl_mode_2 = (RelativeLayout) view.findViewById(R.id.rl_mode_2);
        RelativeLayout rl_mode_3 = (RelativeLayout) view.findViewById(R.id.rl_mode_3);
        TextView tv_del = (TextView) view.findViewById(R.id.tv_del);
        TextView tv_esure = (TextView) view.findViewById(R.id.tv_esure);
        final ImageView iv_mode_1 = (ImageView) view.findViewById(R.id.iv_mode_1);
        final ImageView iv_mode_2 = (ImageView) view.findViewById(R.id.iv_mode_2);
        final ImageView iv_mode_3 = (ImageView) view.findViewById(R.id.iv_mode_3);
        if (popupWindow1==null)
            popupWindow1 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow1.setFocusable(true);
        popupWindow1.setOutsideTouchable(true);
        //添加弹出、弹入的动画
        popupWindow1.setAnimationStyle(R.style.Popupwindow);
        popupWindow1.showAtLocation(view, Gravity.BOTTOM, 0,0);
//        WindowManager.LayoutParams lp=getWindow().getAttributes();
//            lp.alpha=0.3f;
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getWindow().setAttributes(lp);
        backgroundAlpha(0.5f);
        //添加按键事件监听

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_del:
                        popupWindow1.dismiss();

                        break;
                    case R.id.tv_esure:

                        popupWindow1.dismiss();
                        break;

                    case R.id.rl_mode_1:
                        iv_mode_1.setVisibility(View.VISIBLE);
                        iv_mode_2.setVisibility(View.INVISIBLE);
                        iv_mode_3.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rl_mode_2:
                        iv_mode_1.setVisibility(View.INVISIBLE);
                        iv_mode_2.setVisibility(View.VISIBLE);
                        iv_mode_3.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rl_mode_3:
                        iv_mode_1.setVisibility(View.INVISIBLE);
                        iv_mode_2.setVisibility(View.INVISIBLE);
                        iv_mode_3.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };

        rl_mode_1.setOnClickListener(listener);
        rl_mode_2.setOnClickListener(listener);
        rl_mode_3.setOnClickListener(listener);
        tv_del.setOnClickListener(listener);
        tv_esure.setOnClickListener(listener);
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });

    }
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
}
