package teabar.ph.com.teabar.activity.question;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.BuyNowActivity;
import teabar.ph.com.teabar.activity.MainActivity;
import teabar.ph.com.teabar.activity.device.AddDeviceActivity1;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.adpter.RecommendAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class RecommendActivity extends BaseActivity {

    @BindView(R.id.rv_recommend)
    RecyclerView rv_recommend ;
    List<Tea> list = new ArrayList<>();
    MyApplication application;
    RecommendAdapter recommendAdapter;
    SharedPreferences preferences;
    String userId;
    int choose;
    String shopUrl;
    QMUITipDialog tipDialog;
    Tea tea1,tea2,tea3;
    @Override
    public void initParms(Bundle parms) {
         tea1 = (Tea) parms.getSerializable("tea1");
         tea2 = (Tea) parms.getSerializable("tea2");
         tea3 = (Tea) parms.getSerializable("tea3");
        choose = parms.getInt("choose");
        if (tea1!=null)
        list.add(tea1);
        if (tea2!=null)
        list.add(tea2);
        if (tea3!=null)
        list.add(tea3);
    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 360, true);//如果有自定义需求就用这个方法
        return super.getResources();

    }
    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_recommend;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","" )+"";
        shopUrl = preferences.getString("shopUrl","");
        if (TextUtils.isEmpty(shopUrl)){
            new GetwebAsyncTask(this).execute();
        }
        recommendAdapter  = new RecommendAdapter(this,list,userId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_recommend.setLayoutManager(linearLayoutManager);
        rv_recommend.setAdapter(recommendAdapter);
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
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getText(R.string.search_qsh).toString())
                .create();
        tipDialog.show();
    }
    @OnClick({R.id.tv_recom_skip,R.id.bt_device_add,R.id.bt_device_buy})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_recom_skip:
                application.removeActivity(this);
                if (choose==1){
                    startActivity(AddDeviceActivity1.class);
                }else {
                    startActivity(MainActivity.class);
                }

                break;

            case R.id.bt_device_add:
              customDialog1();
                break;

            case R.id.bt_device_buy:
                if (TextUtils.isEmpty(shopUrl)){
                    showProgressDialog();
                    new  GetwebAsyncTask(this).execute();
                }else {
                    String url = "";
                    Intent intent1 = new Intent(this,BuyNowActivity.class);

                    if (tea1!=null){
                        url = tea1.getShopId()+":1";
                    }
                    if (tea2!=null){
                        url = url+","+tea2.getShopId()+":1";
                    }
                    if (tea2!=null){
                        url = url+","+tea3.getShopId()+":1";
                    }
                    intent1.putExtra("myUrl","https://lify-wellness.myshopify.com/cart/"+url);
                    startActivity(intent1);
                }
                break;


        }
    }

    /**
     * 添加喜愛dialog
     */
    Dialog dialog;
    boolean which;
    private void customDialog1( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText( R.string.dialog_love_title);
        et_dia_name.setText(R.string.dialog_love_title1);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.45f);

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
                String teaId = "";
                for (int i=0;i<list.size();i++){
                    if (i== list.size()){
                        teaId = teaId+ list.get(i).getId();
                    }else {
                        teaId = teaId+ list.get(i).getId()+",";
                    }
                }
                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId );
                params.put("teaId",teaId);
                new CollectTeaAsyncTask(RecommendActivity.this).execute(params);

                dialog.dismiss();

            }
        });
        dialog.show();

    }


    /**
     *  添加喜愛
     *
     */
    String returnMsg1,returnMsg2;
    class CollectTeaAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public CollectTeaAsyncTask(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
                ip ="/app/collectTea";
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+ip,prarms);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");
                        returnMsg2=jsonObject.getString("message3");
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
        protected void onPostExecute(BaseActivity baseActivity, String s) {
            switch (s) {
                case "200":
                    if (application.IsEnglish()==0){
                        toast(  returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
//                    application.removeActivity(RecommendActivity.this);
//                    startActivity(AddDeviceActivity1.class);
                    break;

                case "4000":
                    toast( getText(R.string.toast_all_cs).toString());
                    break;
                default:
                    if (application.IsEnglish()==0){
                        toast(  returnMsg1);
                    }else {
                        toast(returnMsg2);
                    }
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
