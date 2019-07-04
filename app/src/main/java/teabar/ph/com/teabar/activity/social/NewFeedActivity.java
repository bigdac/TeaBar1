package teabar.ph.com.teabar.activity.social;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.NewFeedAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.News;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.NetWorkUtil;

public class NewFeedActivity extends BaseActivity {
    @BindView(R.id.rv_allnews)
    RecyclerView rv_allnews;
    @BindView(R.id.refreshLayout_xq)
    RefreshLayout refreshLayou;
    List<News> feedlist = new ArrayList();
    MyApplication application;
    NewFeedAdapter newFeedAdapter ;
    int pageNum=1;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_newfeed;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        newFeedAdapter = new NewFeedAdapter(this,feedlist);
        rv_allnews.setLayoutManager(new LinearLayoutManager(this));
        rv_allnews.setAdapter(newFeedAdapter);
        newFeedAdapter.SetOnItemClick(new NewFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(NewFeedActivity.this,NewFeedxqActivity.class);
                intent.putExtra("news",newFeedAdapter.getmData().get(position));
                startActivity(intent);
            }

        });
        new FindNewsAsynctask(this).execute();
        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
                    feedlist.clear();
                    pageNum=1;
                    new FindNewsAsynctask(NewFeedActivity.this).execute();
                }else {
                    toast("无网络可用，请检查网络");
                }

            }

        });

        refreshLayou.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
//                    refreshLayout.finishLoadMore(5000,false,false);
                    pageNum++;
                    new FindNewsAsynctask(NewFeedActivity.this).execute();
                }else {
                    toast(   "无网络可用，请检查网络");
                }
            }
        });
    }
    String message1 ,message2;
    class FindNewsAsynctask extends BaseWeakAsyncTask<Void,Void,String,BaseActivity> {

        public FindNewsAsynctask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Void... voids) {
            String code ="";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/other/getNewsList?type="+application.IsEnglish()+"&pageSize=10&currentPage="+pageNum);
            Log.e(TAG, "doInBackground: -->"+result );
            try {
                JSONObject jsonObject  = new JSONObject(result);
                code = jsonObject.getString("state");
                message1 = jsonObject.getString("message2");
                message2= jsonObject.getString("message3");
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonObject1.getJSONArray("items");
                Gson gson = new Gson();
                for (int i=0;i<jsonArray.length();i++){
                    News news = gson.fromJson(jsonArray.get(i).toString(),News.class);
                    feedlist.add(news);
                }

            } catch ( Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity, String s) {

            switch (s){
                case "200":
                    newFeedAdapter.setmData(feedlist);
                    refreshLayou.finishLoadMore(true);
                    refreshLayou.finishRefresh(true);
                    break;

                    default:
                        refreshLayou.finishLoadMore(false);
                        refreshLayou.finishRefresh(false);
                        if (application.IsEnglish()==0){
                            toast(message1);
                        }else {
                            toast(message2);
                        }

                        break;

            }
        }
    }

    @OnClick({R.id.iv_power_fh})
    public void onClick(View view){

        switch (view.getId()){

            case R.id.iv_power_fh:
                finish();
                break;

        }

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
