package teabar.ph.com.teabar.fragment.questionFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;


public class Question7Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.iv_question1)
    ImageView iv_question1;
    @BindView(R.id.iv_question2)
    ImageView iv_question2;
    @BindView(R.id.iv_question3)
    ImageView iv_question3;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question7;
    }

    @Override
    public void initView(View view) {
        if ( ((BaseQuestionActivity)getActivity()).getType()==0){
            iv_power_fh.setVisibility(View.GONE);
        }else {
            iv_power_fh.setVisibility(View.VISIBLE);
        }
    }
    int choose =0;
    @OnClick({R.id.iv_power_fh,R.id.li_question1,R.id.li_question2,R.id.li_question3,R.id.bt_question1_esure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                 customDialog1();
                break;
            case R.id.li_question1:
                iv_question1.setVisibility(View.VISIBLE);
                iv_question2.setVisibility(View.INVISIBLE);
                iv_question3.setVisibility(View.INVISIBLE);
                choose =1;
                break;
            case R.id.li_question2:
                iv_question1.setVisibility(View.INVISIBLE);
                iv_question2.setVisibility(View.VISIBLE);
                iv_question3.setVisibility(View.INVISIBLE);
                choose =2;
                break;
            case R.id.li_question3:
                iv_question1.setVisibility(View.INVISIBLE);
                iv_question2.setVisibility(View.INVISIBLE);
                iv_question3.setVisibility(View.VISIBLE);
                choose =3;
                break;
            case R.id.bt_question1_esure:
                if (choose==0){
                    ToastUtil.showShort(getActivity(),getText(R.string.question_toa_xz).toString());
                }else {
                    ((BaseQuestionActivity)getActivity()).toStarActivity(choose);
                }

                break;
        }
    }
    /**
     * 自定义对话
     */
    Dialog dialog;
    private void customDialog1(  ) {
        dialog  = new Dialog( getActivity(), R.style.MyDialog);
        View view = View.inflate(getActivity(), R.layout.dialog_del1, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        et_dia_name.setText(this.getText(R.string.question_back).toString());
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.45f);

        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MainActivity.class));
                dialog.dismiss();

            }
        });
        dialog.show();

    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
