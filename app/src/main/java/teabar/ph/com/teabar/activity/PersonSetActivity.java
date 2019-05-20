package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.activity.question.RecommendActivity;
import teabar.ph.com.teabar.activity.social.AddSocialActivity;
import teabar.ph.com.teabar.adpter.AddressAdapter;
import teabar.ph.com.teabar.adpter.BasicExamAdapter;
import teabar.ph.com.teabar.adpter.PersonAskAdapter;
import teabar.ph.com.teabar.adpter.PlanAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.bean.CircleItem;
import teabar.ph.com.teabar.bean.CommentItem;
import teabar.ph.com.teabar.bean.PhotoInfo;
import teabar.ph.com.teabar.bean.User;
import teabar.ph.com.teabar.fragment.questionFragment.Question3Fragment;
import teabar.ph.com.teabar.fragment.questionFragment.Question6Fragment;
import teabar.ph.com.teabar.pojo.Adress;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.DisplayUtil;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.view.FlowTagView;

public class PersonSetActivity extends BaseActivity {

    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.tv_base_xxdz)
    TextView tv_base_xxdz;
    @BindView(R.id.tv_base_birthxq)
    TextView tv_base_birthxq;
    @BindView(R.id.iv_base_nan)
    ImageView iv_base_nan;
    @BindView(R.id.iv_base_nv)
    ImageView iv_base_nv;
    @BindView(R.id.iv_hy_yes)
     ImageView iv_hy_yes;
    @BindView(R.id.iv_hy_no)
    ImageView iv_hy_no;
    @BindView(R.id.et_person_name)
    EditText et_person_name;
    @BindView(R.id.et_person_tall)
     EditText et_person_tall;
    @BindView(R.id.et_person_weight)
     EditText et_person_weight;
    @BindView(R.id.fv_message)
    FlowTagView fv_message;
    @BindView(R.id.tv_question_name)
    TextView tv_question_name;
    BasicExamAdapter basicExamAdapter ;
    MyApplication application;
    PersonAskAdapter personAskAdapter;
    List<String> mList = new ArrayList<>();
    AddressAdapter addressAdapter;
    List<Adress> list = new ArrayList<>();
    List <String> stringList = new ArrayList<>();
    List<examOptions> examOptionsList = new ArrayList<>();
    private TimePickerView pvCustomTime;
    QMUITipDialog tipDialog;
    int language;
    SharedPreferences preferences;
    String userId ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_personalset;
    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh))
                .create();
        tipDialog.show();
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        language = application.IsEnglish();
        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        userId = preferences.getString("userId","");
        addressAdapter = new AddressAdapter(this,list,language);
        basicExamAdapter = new BasicExamAdapter(this,R.layout.item_basicexam);
        fv_message.setAdapter(basicExamAdapter);
        new  getCountryAsynTask().execute();
        Map<String ,Object> param = new HashMap<>();
        param.put("userId",userId);
        new FindMessAsynctask().execute(param);
        initCustomTimePicker();
        basicExamAdapter.SetOnclickLister(new BasicExamAdapter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
                examOptions examOptions= basicExamAdapter.getMdata(position);
                String num = examOptions.getOptionNum();
                if (stringList.contains(num)){
                    stringList.remove(num);
                }else {
                    if (stringList.size()<3){
                        stringList.add(num);
                    }
                }
                Log.e("GGGGGGGGGGGGGGGG", "itemClick: -->"+stringList.size() );
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    int sex = 1;
    @OnClick({R.id.iv_power_fh ,R.id.rl_set_place ,R.id.rl_set_age,R.id.iv_base_nan,R.id.tv_base_nan,
        R.id.iv_base_nv,R.id.tv_base_nv,R.id.iv_hy_yes,R.id.tv_hy_yes,R.id.iv_hy_no,R.id.tv_hy_no,R.id.bt_search_set
    })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.rl_set_place:
                showPopup();
                break;
            case R.id.rl_set_age:
                pvCustomTime.show();
                break;

            case R.id.tv_base_nan:
            case R.id.iv_base_nan:
                iv_base_nv.setImageResource(R.mipmap.set_xz2);
                iv_base_nan.setImageResource(R.mipmap.choose_nan);
                sex =1;
                break;

            case R.id.iv_base_nv:
            case R.id.tv_base_nv:
                iv_base_nv.setImageResource(R.mipmap.choose_nv);
                iv_base_nan.setImageResource(R.mipmap.set_xz2);
                sex =2;
                break;

            case R.id.iv_hy_yes:
            case R.id.tv_hy_yes:
                if (sex==2){
                iv_hy_yes.setImageResource(R.mipmap.choose_nv);
                iv_hy_no.setImageResource(R.mipmap.set_xz2);
                conceive= 1;
                }else {
                    toast(getText(R.string.toast_base_nv).toString());
                }
                break;

            case R.id.iv_hy_no:
            case R.id.tv_hy_no:
                if (sex==2){
                iv_hy_no .setImageResource(R.mipmap.choose_nv);
                iv_hy_yes.setImageResource(R.mipmap.set_xz2);
                 conceive= 0;
                }else {
                    toast(getText(R.string.toast_base_nv).toString());
                }
                break;
            case R.id.bt_search_set:
                if (TextUtils.isEmpty(et_person_name.getText())){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                if (TextUtils.isEmpty(tv_base_birthxq.getText())){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                if (TextUtils.isEmpty(et_person_tall.getText())){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                if (TextUtils.isEmpty(et_person_weight.getText())){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                if (TextUtils.isEmpty(tv_base_xxdz.getText())){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                if (stringList.size()==0){
                    toast(getText(R.string.toast_update_base).toString());
                    break;
                }
                showProgressDialog();
                Map<String,Object> param = new HashMap<>();
                param.put("userId",userId);
                param.put("weight",et_person_weight.getText().toString().trim());
                if (sex==1){
                    /*0nv 1nan*/
                    param.put("sex", 1);
                }else {
                    param.put("sex", 0);
                }
                param.put("height",et_person_tall.getText().toString().trim());
                param.put("birthday",tv_base_birthxq.getText().toString().trim());
                param.put("country",country1);
                param.put("province",province1);
                param.put("city",city1);
                param.put("area",area1);
                param.put("conceive",conceive);
                param.put("userName",userName);
                new SaveMessAsynctask().execute(param);
                String answer = Utils.listToString(stringList);
                Map<String ,Object> params = new HashMap<>();
                params.put("userId",userId);
                params.put("answer",answer);
                new  setBasicTeaAsynctask().execute(params);
                break;
        }
    }


    class setBasicTeaAsynctask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/setBasicTea " ,param);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "doInBackground: "+e.getMessage() );
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


                    break;
                case "4000":

//                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());

                    break;
                default:
//                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }

    /*上传信息*/
    class SaveMessAsynctask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/setBasic",param);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){

                        }
                    } catch ( Exception e) {
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
            switch (s){
                case "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    finish();
                    toast(getText(R.string.toast_update_cg).toString());
                    break;
                default:

                    break;
            }
        }
    }


    String  getSex ;
    String height,weight,birthday,address,userName,label,addressEn;
    int conceive =0;
   /*查找基础信息*/
    class FindMessAsynctask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/api/selectUserBasic",param);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        if ("200".equals(code)){
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            int sex1 = jsonObject1.getInt("sex");
                            if (sex1==1){
                                getSex=getText(R.string.personal_set_man).toString();
                                sex =1 ;
                            }else {
                                getSex=getText(R.string.personal_set_women).toString();
                                sex =2 ;
                            }
                            height = jsonObject1.getString("height");
                            weight = jsonObject1.getString("weight");
                            birthday = jsonObject1.getString("birthday");
                            address = jsonObject1.getString("address");
                            addressEn = jsonObject1.getString("addressEn");
                            userName = jsonObject1.getString("userName");
                            conceive = jsonObject1.getInt("conceive");
                            label = jsonObject1.getString("label");
                            country1  = jsonObject1.getString("country");
                            province1 = jsonObject1.getString("province");
                            city1 = jsonObject1.getString("city");
                            area1 = jsonObject1.getString("area");

                        }
                    } catch ( Exception e) {
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
            switch (s){
                case "200":
                    new getTeaListAsynTask().execute();
                    et_person_name.setText(userName);
                    tv_base_birthxq.setText(birthday);
                    et_person_tall.setText(height);
                    et_person_weight.setText(weight);
                    if (language==0){
                    tv_base_xxdz.setText(address);
                    }else {
                        tv_base_xxdz.setText(addressEn);
                    }
                    if (conceive==0){
                        iv_hy_no .setImageResource(R.mipmap.choose_nv);
                        iv_hy_yes    .setImageResource(R.mipmap.set_xz2);
                        sex=2;
                    }else {
                        iv_hy_yes  .setImageResource(R.mipmap.choose_nv);
                        iv_hy_no  .setImageResource(R.mipmap.set_xz2);
                        sex=1;
                    }
                    if (sex==1){
                        iv_base_nv.setImageResource(R.mipmap.set_xz2);
                        iv_base_nan.setImageResource(R.mipmap.choose_nan);
                    }else {
                        iv_base_nv.setImageResource(R.mipmap.choose_nv);
                        iv_base_nan.setImageResource(R.mipmap.set_xz2);
                    }
                    break;
                default:

                    break;
            }
        }
    }


    String examTitle;
    /*  获取問題*/
    class getTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            int type =29;
            if ( application.IsEnglish()==0){
                type=2;
            }
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/exam/getBasicExam?examId="+type );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            examTitle = jsonObject1.getString("examTitle");
                            JSONArray jsonArray  = jsonObject1.getJSONArray("examOptions");
                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                examOptions examOptions = gson.fromJson(jsonArray.get(i).toString(),examOptions.class);
                                examOptionsList.add(examOptions);
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
                    examOptionsList1.clear();
                    tv_question_name.setText(examTitle);
                    if (!TextUtils.isEmpty(label)){
                        String[] aa = label.split(",");

                            for (int j=0;j<examOptionsList.size();j++){
                                examOptions examOptions = examOptionsList.get(j);
                                for ( int i=0;i<aa.length;i++) {
                                    if (aa[i].equals(examOptionsList.get(j).getOptionNum())) {
                                        examOptions.setIsselect(true);
                                    }
                                }
                                 examOptionsList1.add(examOptions);
                            }

                        basicExamAdapter.setItems(examOptionsList1);
                        stringList = basicExamAdapter.getStringList();
                    }

                    break;
                case "4000":

                   toast( getText(R.string.toast_all_cs).toString());

                    break;
                default:

                   toast( getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }
    List<examOptions> examOptionsList1 = new ArrayList<>();
    /*  地址*/
    String returnMsg1,returnMsg2;
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
                        returnMsg1=jsonObject.getString("message2");
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
                        number=0;
                        mPopWindow.dismiss();
                        if (TextUtils.isEmpty(province)){
                            tv_base_xxdz.setText(country);
                        }else if (TextUtils.isEmpty(city)){
                            tv_base_xxdz.setText(country+"·"+province);
                        }else if (TextUtils.isEmpty(area)){
                            tv_base_xxdz.setText(country+"·"+province+"·"+city);
                        }else  {
                            tv_base_xxdz.setText(country+"·"+province+"·"+city+"·"+area);
                        }

                    }
                    break;
                case "4000":

                    toast(getText(R.string.toast_all_cs).toString());

                    break;
                default:

//                   toast( returnMsg1);
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
        contentViewSign = LayoutInflater.from(this).inflate(R.layout.popview_address, null);
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
                country = getText(R.string.adress_hot_1).toString();
                province = getText(R.string.adress_hot_71).toString();
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
                country = getText(R.string.adress_hot_8).toString();
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
                country = getText(R.string.adress_hot_1).toString();
                province = getText(R.string.adress_hot_101).toString();
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
                country = getText(R.string.adress_hot_1).toString();
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
                country =getText(R.string.adress_hot_2).toString();
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
                country = getText(R.string.adress_hot_1).toString();
                province = getText(R.string.adress_hot_41).toString();
                country1 = "130";
                province1 = "636";
                number=2;
                examId = 636;
                new getCountryAsynTask().execute();
                tv_address_1.setVisibility(View.GONE);
                li_address_hot.setVisibility(View.GONE);
            }
        });
        rv_address_country.setLayoutManager(new LinearLayoutManager(this));
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
                    if (language==0){
                    country = adress.getCname();
                    country1 = adress.getId()+"";
                    }else {
                        country = adress.getEname();
                        country1 = adress.getId()+"";
                    }
                }else if (number==2){
                    if (language==0) {
                        province = adress.getCname();
                        province1 = adress.getId() + "";
                    }else {
                        province = adress.getEname();
                        province1 = adress.getId() + "";
                    }
                    city=null;
                    area=null;
                    city1=null;
                    area1=null;
                }else if (number==3){
                    if (language==0) {
                        city = adress.getCname();
                        city1 = adress.getId() + "";
                    }else {
                        city = adress.getEname();
                        city1 = adress.getId() + "";
                    }
                }else if (number==4){
                    if (language==0) {
                        area = adress.getCname();
                        area1 = adress.getId() + "";
                    }else {
                        area = adress.getEname();
                        area1 = adress.getId() + "";
                    }
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
        mPopWindow.setHeight((int)(DisplayUtil.getScreenHeight(this)*0.75));
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
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
         getWindow().setAttributes(lp);
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
        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = getTime(date);
                tv_base_birthxq.setText(time);


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
