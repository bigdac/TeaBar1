package teabar.ph.com.teabar.fragment.questionFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.ArrayList;
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


public class Question5Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.et_ques_weight)
    TextView et_ques_weight;
    @BindView(R.id.et_ques_tall)
    TextView et_ques_tall;
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    private TimePickerView pvCustomTime;

    @Override
    public int bindLayout() {
        return R.layout.fragment_question5;
    }

    @Override
    public void initView(View view) {
        if ( ((BaseQuestionActivity)getActivity()).getType()==0){
            iv_power_fh.setVisibility(View.GONE);
        }else {
            iv_power_fh.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.iv_power_fh,R.id.bt_question1_esure,R.id.et_ques_tall,R.id.et_ques_weight})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
               customDialog3();
                break;
            case R.id.bt_question1_esure:
                if (TextUtils.isEmpty(et_ques_weight.getText())){
                    ToastUtil.showShort(getActivity(), getText(R.string.personal_set_weightnull).toString());
                    return;
                }
                if (TextUtils.isEmpty(et_ques_tall.getText())){
                    ToastUtil.showShort(getActivity(), getText(R.string.personal_set_tallnull).toString());
                    return;
                }
                String text1 = et_ques_tall.getText().toString().replaceAll("cm","");
                String text2 = et_ques_weight.getText().toString().replaceAll("kg","");
                ((BaseQuestionActivity)getActivity()).setMesssage2(text1,text2);
                ((BaseQuestionActivity)getActivity()).rePlaceFragment(5);
                break;

            case R.id.et_ques_tall:
                customDialog();
                break;
            case R.id.et_ques_weight:
                customDialog1();
                break;
        }
    }
    /**
     * 自定义对话
     */
    Dialog dialog;
    private void customDialog3(  ) {
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

    Dialog dialog1;


    TextView tv_tall_qx,tv_tall_qd ;
    WheelView wheelView;
    private void customDialog( ) {

        dialog1  = new Dialog(getActivity(), R.style.MyDialog);
        View view = View.inflate(getActivity(), R.layout.item_tall, null);
        tv_tall_qx = view.findViewById(R.id.tv_tall_qx);
        tv_tall_qd = view.findViewById(R.id.tv_tall_qd);
        wheelView = view.findViewById(R.id.wheelview);
        wheelView.setCyclic(false);

        final List<String> mOptionsItems = new ArrayList<>();
        for (int i =1;i<219;i++){
            mOptionsItems.add(i+"cm");
        }

        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
//                Toast.makeText(getActivity(), "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();
            }
        });
        wheelView.setCurrentItem(165);

        dialog1.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog1.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        tv_tall_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog1!=null&&dialog1.isShowing())
                dialog1.dismiss();

            }
        });
        tv_tall_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =   wheelView.getAdapter().getItem(wheelView.getCurrentItem()).toString();
                et_ques_tall.setText(text);
                if (dialog1!=null&&dialog1.isShowing())
                dialog1.dismiss();
            }
        });

        dialog1.show();

    }

    private void customDialog1( ) {

        dialog1  = new Dialog(getActivity(), R.style.MyDialog);
        View view = View.inflate(getActivity(), R.layout.item_tall, null);
        tv_tall_qx = view.findViewById(R.id.tv_tall_qx);
        tv_tall_qd = view.findViewById(R.id.tv_tall_qd);
        wheelView = view.findViewById(R.id.wheelview);
        wheelView.setCyclic(false);

        final List<String> mOptionsItems = new ArrayList<>();
        for (int i =1;i<200;i++){
            mOptionsItems.add(i+"kg");
        }

        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
//                Toast.makeText(getActivity(), "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();
            }
        });
        wheelView.setCurrentItem(60);

        dialog1.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog1.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        tv_tall_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog1!=null&&dialog1.isShowing())
                dialog1.dismiss();

            }
        });
        tv_tall_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =   wheelView.getAdapter().getItem(wheelView.getCurrentItem()).toString();
                et_ques_weight.setText(text);
                if (dialog1!=null&&dialog1.isShowing())
                dialog1.dismiss();
            }
        });

        dialog1.show();

    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
