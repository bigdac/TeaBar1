package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EvaluateAdapter;
import teabar.ph.com.teabar.adpter.MyplanAdapter;
import teabar.ph.com.teabar.adpter.TeaAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Plan;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.chat.adpter.TextWatcherAdapter;
import teabar.ph.com.teabar.view.FlowTagView;

public class SearchActivity extends BaseActivity {
    MyApplication application;
    QMUITipDialog tipDialog;
    @BindView(R.id.fv_history)
    FlowTagView fv_history;
//    @BindView(R.id.fv_teste)
//    FlowTagView fv_teste;
//    @BindView(R.id.fv_function)
//    FlowTagView fv_function;
//    @BindView(R.id.fv_aim)
//    FlowTagView fv_aim;
    @BindView(R.id.li_search_old)
    LinearLayout li_search_old;
    @BindView(R.id.et_search_search)
    EditText et_search_search;
    @BindView(R.id.iv_search_del)
    ImageView iv_search_del;
    @BindView(R.id.iv_history_del)
    ImageView iv_history_del;
    @BindView(R.id.rv_main_tealist)
    RecyclerView rv_main_tealist;
    @BindView(R.id.rv_main_jh)
    RecyclerView rv_main_jh;

    //标签类相关
    private EvaluateAdapter adapter_his;
    List<String> list=new ArrayList<>();
    String userId ;
    String name;
    SharedPreferences preferences;
    List<Tea> teaList= new ArrayList<>();
    List<Plan> planList = new ArrayList<>();
    TeaAdapter teaAdapter1;
    MyplanAdapter myplanAdapter ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_search;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences =  getSharedPreferences("my",Context.MODE_PRIVATE);
        userId = preferences.getString("userId","");
        et_search_search.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
                    iv_search_del.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (et_search_search.length()==0){
                    iv_search_del.setVisibility(View.INVISIBLE);

                }
            }
        });
        iv_search_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_search.setText("");
            }
        });

        initView();
        teaAdapter1 = new TeaAdapter(this,teaList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_main_tealist.setLayoutManager(linearLayoutManager);
        rv_main_tealist.setAdapter(teaAdapter1);
        myplanAdapter = new MyplanAdapter(this,planList);
        //解决滑动冲突、滑动不流畅
        rv_main_tealist.setHasFixedSize(true);
        rv_main_tealist.setNestedScrollingEnabled(false);
        rv_main_jh.setLayoutManager(new LinearLayoutManager(this));
        rv_main_jh.setAdapter(myplanAdapter);
        rv_main_jh.setHasFixedSize(true);
        rv_main_jh.setNestedScrollingEnabled(false);

    }

    @Override
    public void doBusiness(Context mContext) {
        getHistory();

    }

    @Override
    public void widgetClick(View v) {

    }
    String returnMsg1;
    /*  获取問題*/
    class getSearchResultAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/searchTea?userId="+userId+"&search="+name);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONObject jsonObject1 =  jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {

                            JSONArray jsonArray  = jsonObject1.getJSONArray("tea");
                            Gson gson = new Gson();
                            for (int i = 0;i<jsonArray.length();i++){
                                Tea tea = gson.fromJson(jsonArray.get(i).toString(),Tea.class);
                                teaList.add(tea);
                            }
                            JSONArray jsonArray1  = jsonObject1.getJSONArray("plan");
                            for (int i = 0;i<jsonArray1.length();i++){
                                Plan plan = gson.fromJson(jsonArray1.get(i).toString(),Plan.class);
                                planList.add(plan);
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
                    getHistory();
                    if (teaList.size()==0&&planList.size()==0){
                        toast(getText(R.string.toast_search_no).toString());
                    }
                    teaAdapter1.update(teaList);
                    myplanAdapter.update(planList);
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    break;
                case "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());

                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( getText(R.string.toast_all_cs).toString());
                    break;

            }
        }
    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())

                .create();
        tipDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @OnClick({R.id.iv_power_fh,R.id.tv_search_search,R.id.iv_history_del})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;

            case R.id.tv_search_search:
                if (!TextUtils.isEmpty(et_search_search.getText()))
//                setHistory(et_search_search.getText().toString().trim());
                name = et_search_search.getText().toString().trim();
                showProgressDialog();
                teaList.clear();
                planList.clear();
                new getSearchResultAsynTask().execute();
                break;

            case R.id.iv_history_del:
                SharedPreferences.Editor editor = getSharedPreferences("SearchHistoryList", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                loadList.clear();
                adapter_his.setItems(new ArrayList<String>());
                li_search_old.setVisibility(View.INVISIBLE);
                iv_history_del.setVisibility(View.INVISIBLE);
                break;
        }

    }

    private void initView() {
//        /*口味*/
//        adapter_teste = new EvaluateAdapter(this, R.layout.item_search);
//        fv_teste.setAdapter(adapter_teste);
//        fv_teste.setItemClickListener(new FlowTagView.TagItemClickListener() {
//            @Override
//            public void itemClick(int position) {
//                String e = adapter_teste.getItem(position).toString();
////                Intent intent = new Intent( this, ShopSearchResultActivity.class);
////                intent.putExtra("goodsName", e);
////                setHistory(e);
////                startActivity(intent);
//            }
//        });
        /*历史*/
        adapter_his = new EvaluateAdapter(this, R.layout.item_search);
        fv_history.setAdapter(adapter_his);
        fv_history.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                String e = adapter_his.getItem(position).toString() ;
                Intent intent = new Intent(SearchActivity.this, SearchFinishActivity.class);
                intent.putExtra("searchName", e);
                startActivityForResult(intent,2000);
            }
        });

//        /*功能*/
//        adapter_function = new EvaluateAdapter(this, R.layout.item_search);
//        fv_function.setAdapter(adapter_function);
//        fv_function.setItemClickListener(new FlowTagView.TagItemClickListener() {
//            @Override
//            public void itemClick(int position) {
//
//            }
//        });


//        /*目标*/
//        adapter_aim = new EvaluateAdapter(this, R.layout.item_search);
//        fv_aim.setAdapter(adapter_aim);
//        fv_aim.setItemClickListener(new FlowTagView.TagItemClickListener() {
//            @Override
//            public void itemClick(int position) {
//
//            }
//        });
    }

    //将搜索记录保存到历史记录
    List<String> loadList ;
    public void getHistory() {
        loadList= new ArrayList<String>();
        SharedPreferences getDataList = getSharedPreferences("SearchHistoryList", MODE_PRIVATE);
        int environNums = getDataList.getInt("EnvironNums", 0);
        for (int i = environNums; i >0; i--) {
            String environItem = getDataList.getString("history_" + (i-1), null);
            loadList.add(environItem);
        }
        adapter_his.setItems(loadList);
        if(loadList.size()==0){
            li_search_old.setVisibility(View.INVISIBLE);
            iv_history_del.setVisibility(View.INVISIBLE);
        }else {
            li_search_old.setVisibility(View.VISIBLE);
            iv_history_del.setVisibility(View.VISIBLE);
        }
    }


    public void setHistory(String search) {

        if (loadList.size() > 5)
            loadList.remove(0);
        //遍历list，如果有相同搜索则不添加
        Boolean isSet=true;
        for(int i=0;i<loadList.size();i++){
            if(search.equals(loadList.get(i))){
                isSet=false;
            }
        }
        if (isSet)
            loadList.add(search);
        SharedPreferences.Editor editor = getSharedPreferences("SearchHistoryList", MODE_PRIVATE).edit();
        editor.putInt("EnvironNums", loadList.size());
        for (int i = 0; i < loadList.size(); i++) {
            editor.putString("history_" + i, loadList.get(i));
        }
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==3000){
            getHistory();
        }
    }
}
