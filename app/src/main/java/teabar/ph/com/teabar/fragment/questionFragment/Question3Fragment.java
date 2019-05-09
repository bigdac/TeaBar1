package teabar.ph.com.teabar.fragment.questionFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.adpter.AddressAdapter;
import teabar.ph.com.teabar.adpter.RecyclerViewAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.pojo.Adress;
import teabar.ph.com.teabar.util.DisplayUtil;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;


public class Question3Fragment extends BaseFragment {
    RecyclerViewAdapter recyclerViewAdapter;
    List<Adress> list = new ArrayList<>();
    @BindView(R.id.iv_question_women)
    ImageView iv_question_women;
    @BindView(R.id.iv_question_man)
    ImageView iv_question_men;
    @BindView(R.id.et_question_birthday)
    TextView et_question_birthday;
    @BindView(R.id.et_question_place)
    TextView et_question_place;
    private TimePickerView pvCustomTime;
    AddressAdapter addressAdapter;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question3;
    }

    @Override
    public void initView(View view) {

        addressAdapter = new AddressAdapter(getActivity(),list);
        new getCountryAsynTask().execute();
        initCustomTimePicker();
    }
    String sex = "1";
    @OnClick({R.id.bt_question1_esure,R.id.li_question_women,R.id.li_question_man,R.id.et_question_birthday,R.id.et_question_place})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_question1_esure:

                    if (TextUtils.isEmpty(et_question_birthday.getText())){
                        ToastUtil.showShort(getActivity(),"生日不能為空");
                        return;
                    }
                    if (TextUtils.isEmpty(et_question_place.getText())){
                        ToastUtil.showShort(getActivity(),"地區不能為空");
                        return;
                    }
                    if ("1".equals(sex)){
                        ((BaseQuestionActivity)getActivity()).rePlaceFragment(4);
                    }else {
                        ((BaseQuestionActivity)getActivity()).rePlaceFragment(3);
                    }
                ((BaseQuestionActivity)getActivity()).setMesssage(sex,et_question_birthday.getText().toString() );
                break;

            case R.id.li_question_man:
                iv_question_women.setImageResource(R.mipmap.set_xz2);
                iv_question_men.setImageResource(R.mipmap.choose_nan);
                sex = "1";
                break;

            case R.id.li_question_women:
                iv_question_women.setImageResource(R.mipmap.choose_nv);
                iv_question_men.setImageResource(R.mipmap.set_xz2);
                sex = "0";
                break;

            case R.id.et_question_birthday:
                pvCustomTime.show();
                break;

            case R.id.et_question_place:
                showPopup();
                break;
        }
    }
    /*  获取問題*/
    String returnMsg1;
    long examId = 1 ;
    class getCountryAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getMap?id="+examId );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        list.clear();
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONArray jsonArray =  jsonObject.getJSONArray("data");
                        if ("200".equals(code)) {

                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                Adress address = gson.fromJson(jsonArray.get(i).toString(),Adress.class);
                                list.add(address);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    code="4000";
                }
            }
            return code;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "200":
                    addressAdapter.update(list);
                    if (list.size()==0){
                        ((BaseQuestionActivity)getActivity()).setAdress(country1,province1,city1,area1);
                        number=0;
                        mPopWindow.dismiss();
                        if (TextUtils.isEmpty(province)){
                            et_question_place.setText(country);
                        }else if (TextUtils.isEmpty(city)){
                            et_question_place.setText(country+"·"+province);
                        }else if (TextUtils.isEmpty(area)){
                            et_question_place.setText(country+"·"+province+"·"+city);
                        }else  {
                            et_question_place.setText(country+"·"+province+"·"+city+"·"+area);
                        }

                    }
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:

                    ToastUtil.showShort(getActivity(), returnMsg1);
                    break;

            }
        }
    }

    private PopupWindow mPopWindow;
    private View contentViewSign;
    int number = 0 ;
    String  country,province,city,area;
    String  country1,province1,city1,area1;
    private void showPopup() {
        if (examId!=1){
            examId = 1 ;
            new getCountryAsynTask().execute();
        }
        contentViewSign = LayoutInflater.from(getActivity()).inflate(R.layout.popview_address, null);
        final TextView tv_address_1 = contentViewSign.findViewById(R.id.tv_address_1);
        RelativeLayout rl_address_main = contentViewSign.findViewById(R.id.rl_address_main);
        final ImageView iv_address_back = contentViewSign.findViewById(R.id.iv_address_back);
        ImageView iv_address_close = contentViewSign.findViewById(R.id.iv_address_close);
        final LinearLayout li_address_hot = contentViewSign.findViewById(R.id.li_address_hot);
        RecyclerView rv_address_country = contentViewSign.findViewById(R.id.rv_address_country);
        TextView address_hot_1 = contentViewSign.findViewById(R.id.address_hot_1);
        TextView address_hot_2 = contentViewSign.findViewById(R.id.address_hot_2);
        TextView address_hot_3 = contentViewSign.findViewById(R.id.address_hot_3);
        TextView address_hot_4 = contentViewSign.findViewById(R.id.address_hot_4);
        TextView address_hot_5 = contentViewSign.findViewById(R.id.address_hot_5);
        TextView address_hot_6 = contentViewSign.findViewById(R.id.address_hot_6);
        address_hot_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "中国";
                province = "澳门";
                country1 = "130";
                province1 = "655";
                number=2;
                examId = 655;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        address_hot_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "英国";
                country1 = "3482";
                number=1;
                examId = 3482;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        address_hot_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "中国";
                province = "台湾";
                country1 = "130";
                province1 = "612";
                number=2;
                examId = 612;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        address_hot_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "中国";
                country1 = "130";
                number=1;
                examId = 130;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        address_hot_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "美国";
                country1 = "2141";
                number=1;
                examId = 2141;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        address_hot_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country =null;
                province =null;
                city =null;
                area =null;
                country1 =null;
                province1 =null;
                city1 =null;
                area1 =null;
                country = "中国";
                province = "香港";
                country1 = "130";
                province1 = "636";
                number=2;
                examId = 636;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        rv_address_country.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_address_country.setAdapter(addressAdapter);
        iv_address_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number=0;
                mPopWindow.dismiss();
            }
        });
        addressAdapter.SetOnclickLister(new AddressAdapter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
                Adress adress = addressAdapter.getmData().get(position);
                examId = adress.getId();
                number++;

                if (number==1){
                    country = adress.getCname();
                    country1 = adress.getId()+"";
                }else if (number==2){
                    province = adress.getCname();
                    province1 = adress.getId()+"";
                    city=null;
                    area=null;
                    city1=null;
                    area1=null;
                }else if (number==3){
                    city =  adress.getCname();
                    city1 = adress.getId()+"";
                }else if (number==4){
                    area = adress.getCname();
                    area1 = adress.getId()+"";
                }
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        iv_address_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addressAdapter.update(list);
        mPopWindow = new PopupWindow(contentViewSign);
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight((int)(DisplayUtil.getScreenHeight(getActivity())*0.75));
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        //点击空白处时，隐藏掉pop窗口
        mPopWindow.setFocusable(true);
//        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow. setOutsideTouchable(true);
        mPopWindow.setClippingEnabled(false);
        backgroundAlpha(0.5f);
        //添加pop窗口关闭事件
        mPopWindow.setAnimationStyle(R.style.Popupwindow);
        mPopWindow.setOnDismissListener(new poponDismissListener());
//        mPopWindow.showAsDropDown(findViewById(R.id.li_main_bt));
        mPopWindow.showAtLocation(rl_address_main, Gravity.BOTTOM, 0, 0);

    }



    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }

    }
    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
        lp.alpha = f;
        getActivity(). getWindow().setAttributes(lp);
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
