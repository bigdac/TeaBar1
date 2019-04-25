package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.EvaluateAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.chat.adpter.TextWatcherAdapter;
import teabar.ph.com.teabar.view.FlowTagView;

public class SearchActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.fv_history)
    FlowTagView fv_history;
    @BindView(R.id.fv_teste)
    FlowTagView fv_teste;
    @BindView(R.id.fv_function)
    FlowTagView fv_function;
    @BindView(R.id.fv_aim)
    FlowTagView fv_aim;
    @BindView(R.id.li_search_old)
    LinearLayout li_search_old;
    @BindView(R.id.et_search_search)
    EditText et_search_search;
    @BindView(R.id.iv_search_del)
    ImageView iv_search_del;
    @BindView(R.id.iv_history_del)
    ImageView iv_history_del;
    //标签类相关
    private EvaluateAdapter adapter_his, adapter_teste,adapter_function,adapter_aim;
    List<String> list=new ArrayList<>();
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

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        application.addActivity(this);
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
                    iv_search_del.setVisibility(View.GONE);
                }
            }
        });
        iv_search_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_search.setText("");
            }
        });
        iv_history_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter_his.setItems(new ArrayList<String>());
            }
            });
        initView();
    }

    @Override
    public void doBusiness(Context mContext) {
        for(int i=0;i<5;i++){
            list.add("手冰冷");
        }
        adapter_his.setItems(list);
        adapter_aim.setItems(list);
        adapter_function.setItems(list);
        adapter_teste.setItems(list);
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @OnClick({R.id.iv_power_fh})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_power_fh:
                finish();
                break;
        }

    }

    private void initView() {
        /*口味*/
        adapter_teste = new EvaluateAdapter(this, R.layout.item_search);
        fv_teste.setAdapter(adapter_teste);
        fv_teste.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                String e = adapter_teste.getItem(position).toString();
//                Intent intent = new Intent( this, ShopSearchResultActivity.class);
//                intent.putExtra("goodsName", e);
//                setHistory(e);
//                startActivity(intent);
            }
        });
        /*历史*/
        adapter_his = new EvaluateAdapter(this, R.layout.item_search);
        fv_history.setAdapter(adapter_his);
        fv_history.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                String e = adapter_his.getItem(position).toString();
//                Intent intent = new Intent(ShopSearchActivity.this, ShopSearchResultActivity.class);
//                intent.putExtra("goodsName", e);
//                startActivity(intent);
            }
        });

        /*功能*/
        adapter_function = new EvaluateAdapter(this, R.layout.item_search);
        fv_function.setAdapter(adapter_function);
        fv_function.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {

            }
        });


        /*目标*/
        adapter_aim = new EvaluateAdapter(this, R.layout.item_search);
        fv_aim.setAdapter(adapter_aim);
        fv_aim.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {

            }
        });
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
            li_search_old.setVisibility(View.GONE);
            li_search_old.setVisibility(View.GONE);
        }else {
            li_search_old.setVisibility(View.VISIBLE);
            li_search_old.setVisibility(View.VISIBLE);
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
}
