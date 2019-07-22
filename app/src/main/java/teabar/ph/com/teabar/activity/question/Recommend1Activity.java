package teabar.ph.com.teabar.activity.question;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.BuyNowActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.adpter.Recommend1Adapter;
import teabar.ph.com.teabar.adpter.RecommendAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class Recommend1Activity extends BaseActivity {

    @BindView(R.id.rv_recommend)
    RecyclerView rv_recommend ;
    List<Tea> list = new ArrayList<>();
    MyApplication application;
    Recommend1Adapter recommendAdapter;
    SharedPreferences preferences;
    String userId;
    String shopUrl;
    QMUITipDialog tipDialog;

    @Override
    public void initParms(Bundle parms) {
       list = (List<Tea>) parms.getSerializable("teaList");


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
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_recommend1;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        shopUrl = preferences.getString("shopUrl","");
        if (TextUtils.isEmpty(shopUrl)){
            new GetwebAsyncTask(this).execute();
        }
        recommendAdapter  = new Recommend1Adapter(this,list );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_recommend.setLayoutManager(linearLayoutManager);
        rv_recommend.setAdapter(recommendAdapter);
        recommendAdapter.SetOnclickLister(new Recommend1Adapter.OnItemClickListerner() {
            @Override
            public void onClikner(View view, int position) {
                Intent intent = new Intent(Recommend1Activity.this,MakeActivity.class);
                intent.putExtra("teaId",recommendAdapter.getmData().get(position).getId());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.iv_power_fh ,R.id.bt_device_buy })
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                startActivity(MainActivity.class);
                break;

            case R.id.bt_device_buy:
                if (TextUtils.isEmpty(shopUrl)){
                    showProgressDialog();
                    new  GetwebAsyncTask(this).execute();
                }else {
                    String url = "";
                    Intent intent1 = new Intent(this,BuyNowActivity.class);
                    for (int i=0;i<list.size();i++){
                        if (TextUtils.isEmpty(url)){
                            url = list.get(i).getShopId()+":1";
                        }else {
                            url = url+","+list.get(i).getShopId()+":1";
                        }
                    }
                    intent1.putExtra("myUrl","https://lify-wellness.myshopify.com/cart/"+url);
                    Log.e(TAG, "onClick: __>"+url );
                    startActivity(intent1);
                }
                break;

        }
    }


    class GetwebAsyncTask extends BaseWeakAsyncTask<Void ,Void ,String,BaseActivity> {

        public GetwebAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Void... voids) {
            String code ="";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/tea/getTeaUrl?urlId=1");
            if (!TextUtils.isEmpty(result)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("state");
                    JSONObject data = jsonObject.getJSONObject("data");
                    shopUrl  = data.getString("shopUrl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(BaseActivity baseActivity, String s) {

            switch (s){
                case "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("shopUrl",shopUrl);
                    editor.commit();
                    break;
                default:

                    break;
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText( this,getText(R.string.toast_main_exit),Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    application.removeAllActivity();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
