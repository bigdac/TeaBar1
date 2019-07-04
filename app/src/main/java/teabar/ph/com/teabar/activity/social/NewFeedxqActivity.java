package teabar.ph.com.teabar.activity.social;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.NewFeedAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.News;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.NetWorkUtil;

public class NewFeedxqActivity extends BaseActivity {
    @BindView(R.id.iv_newfeed_pic)
    ImageView iv_newfeed_pic;
    @BindView(R.id.tv_newxq_title)
    TextView tv_newxq_title;
    @BindView(R.id.tv_newfeed_mes)
    TextView tv_newfeed_mes;
    @BindView(R.id.tv_newfeed_jh)
    TextView tv_newfeed_jh;
    MyApplication application;
    News news ;
    @Override
    public void initParms(Bundle parms) {
        news =(News)parms.getSerializable("news");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_newfeedxq;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        if (news!=null){
            tv_newxq_title.setText(news.getTitile());
            tv_newfeed_mes.setText(news.getContent());
            tv_newfeed_jh.setText("Related Program: "+news.getRelated());
            Glide.with(this).load(news.getNewsPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.nomal_green).into(iv_newfeed_pic);

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
