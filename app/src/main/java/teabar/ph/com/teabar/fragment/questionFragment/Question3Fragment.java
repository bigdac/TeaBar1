package teabar.ph.com.teabar.fragment.questionFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;


public class Question3Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<String> list;
    @BindView(R.id.iv_question_women)
    ImageView iv_question_women;
    @BindView(R.id.iv_question_man)
    ImageView iv_question_men;
    @BindView(R.id.et_question_birthday)
    TextView et_question_birthday;
    private TimePickerView pvCustomTime;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question3;
    }

    @Override
    public void initView(View view) {
        initCustomTimePicker();
    }
    @OnClick({R.id.bt_question1_esure,R.id.li_question_women,R.id.li_question_man,R.id.et_question_birthday})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_question1_esure:
                ((BaseQuestionActivity)getActivity()).rePlaceFragment(3);
                break;

            case R.id.li_question_man:
                iv_question_women.setImageResource(R.mipmap.set_xz2);
                iv_question_men.setImageResource(R.mipmap.choose_nan);
                break;

            case R.id.li_question_women:
                iv_question_women.setImageResource(R.mipmap.choose_nv);
                iv_question_men.setImageResource(R.mipmap.set_xz2);
                break;

            case R.id.et_question_birthday:
                pvCustomTime.show();
                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = getTime(date);
                et_question_birthday.setText(time);


            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色

                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.animGravity(Gravity.RIGHT)// default is center*/
                .setTextColorCenter(Color.parseColor("#00dfad"))//设置选中项的颜色
                .setTitleBgColor(Color.WHITE)
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(18)
                .setDividerColor(Color.TRANSPARENT)//设置分割线的颜色

                .setType(new boolean[]{ true, true, true,false, false, false})
                .setLabel("","","","", "","")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0,0, 0,0,0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
