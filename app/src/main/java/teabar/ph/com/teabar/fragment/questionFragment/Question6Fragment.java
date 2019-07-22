package teabar.ph.com.teabar.fragment.questionFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.question.BaseQuestionActivity;
import teabar.ph.com.teabar.activity.question.RecommendActivity;
import teabar.ph.com.teabar.adpter.BasicExamAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;
import teabar.ph.com.teabar.view.FlowTagView;


public class Question6Fragment extends BaseFragment {

    List<examOptions> list = new ArrayList<>();
    BasicExamAdapter basicExamAdapter ;
    @BindView(R.id.fv_message)
    FlowTagView fv_message;
    @BindView(R.id.tv_question_name)
    TextView tv_question_name;
    List <String> stringList = new ArrayList<>();
    SharedPreferences preferences;
    String userId;
    List<Tea> listA = new ArrayList<>();
    List<Tea> listB = new ArrayList<>();
    List<Tea> listC = new ArrayList<>();
    List lists [] = {listA,listB,listC};
    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @Override
    public int bindLayout() {
        return R.layout.fragment_question6;
    }

    @Override
    public void initView(View view) {
        preferences = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
        userId = preferences.getString("userId","");
        if ( ((BaseQuestionActivity)getActivity()).getType()==0){
            iv_power_fh.setVisibility(View.GONE);
        }else {
            iv_power_fh.setVisibility(View.VISIBLE);
        }
        basicExamAdapter = new BasicExamAdapter(getActivity(),R.layout.item_basicexam);
        fv_message.setAdapter(basicExamAdapter);
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
        String examtitle = ((BaseQuestionActivity)getActivity()).getExamTitle();
        List<examOptions> mylists = ((BaseQuestionActivity)getActivity()).getQuesList();
         if (!TextUtils.isEmpty(examtitle)&&mylists.size()!=0){
             basicExamAdapter.setItems(mylists);
             tv_question_name.setText(examtitle);
         }else {
             new getTeaListAsynTask().execute();
         }

    }
    String answer ;
    @OnClick({R.id.bt_question1_esure,R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
               customDialog1();
                break;
            case R.id.bt_question1_esure:
                answer = Utils.listToString(stringList);
                Map<String ,Object> params = new HashMap<>();
                params.put("userId",userId);
                params.put("answer",answer);
                new setBasicTeaAsynctask().execute(params);
                Log.e(TAG, "onClick: --》"+Utils.listToString(stringList)+stringList.size()+"..." );
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

    String returnMsg1,returnMsg2;
    String examTitle;
    /*  获取問題*/
    class getTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            int type =29;
            if (((BaseQuestionActivity)getActivity()).getLanguage()==0){
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
                                list.add(examOptions);
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
                    basicExamAdapter.setItems(list);
                    tv_question_name.setText(examTitle);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());

                    break;
                default:

                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }
    Tea tea1;
    Tea tea2;
    Tea tea3;

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
                        listA.clear();
                        listB.clear();
                        listC.clear();
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            String title = null;
                            if(answer.length()>2){
                                 title = answer.replace(",","");
                            }else {
                                title = answer;
                            }
                            for (int i =0 ;i<title.length();i++){
                                JSONArray jsonArray  = jsonObject1.getJSONArray(title.substring(i,i+1));
                                Gson gson = new Gson();
                                for (int j = 0;j<jsonArray.length();j++){
                                    Tea tea = gson.fromJson(jsonArray.get(j).toString(),Tea.class);
                                    lists[i].add(tea);
                                }
                            }
                            if (listC.size()>0){
                                int num1 = (int) (Math.random() * listC.size() );
                                tea1 = listC.get(num1);
                                do {
                                    int num2 = (int) (Math.random() * listB.size() );
                                    tea2  = listB.get(num2);
                                } while (tea2.getId()==tea1.getId());
                                do {
                                    int num3 = (int) (Math.random() * listA.size() );
                                    tea3 = listA.get(num3);
                                } while (tea3.getId()==tea2.getId()||tea3.getId()==tea1.getId());

                            }else if (listB.size()>0){
                                int num1 = (int) (Math.random() * listB.size() );
                                tea1 = listB.get(num1);
                                do {
                                    int num2 = (int) (Math.random() * listB.size() );
                                    tea2  = listB.get(num2);
                                } while (tea2.getId()==tea1.getId());
                                do {
                                    int num3 = (int) (Math.random() * listA.size() );
                                    tea3 = listA.get(num3);
                                } while (tea3.getId()==tea2.getId()||tea3.getId()==tea1.getId());


                            }else {
                                int num1 = (int) (Math.random() * listA.size() );
                                tea1 = listA.get(num1);
                                do {
                                    int num2 = (int) (Math.random() * listA.size() );
                                    tea2  = listA.get(num2);
                                } while (tea2.getId()==tea1.getId());
                                do {
                                    int num3 = (int) (Math.random() * listA.size() );
                                    tea3 = listA.get(num3);
                                } while (tea3.getId()==tea2.getId()||tea3.getId()==tea1.getId());
                            }
                            Log.e(TAG, "onPostExecute: -->"+tea1+"..."+tea2+"..."+tea3 );
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

//                    Intent intent = new Intent(getActivity(),RecommendActivity.class);
//                    intent.putExtra("tea1",tea1);
//                    intent.putExtra("tea2",tea2);
//                    intent.putExtra("tea3",tea3);
//                    startActivity(intent);
                    ((BaseQuestionActivity)getActivity()).setMesssage3(tea1,tea2,tea3);
                    ((BaseQuestionActivity)getActivity()).rePlaceFragment(6);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());

                    break;
                default:
                    ToastUtil.showShort(getActivity(), getActivity().getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }

}
