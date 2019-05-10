package teabar.ph.com.teabar.activity.device;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;
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
import teabar.ph.com.teabar.adpter.EqupmentXqAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class EquipmentDetailsActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    @BindView(R.id.rv_equmentxq)
    RecyclerView rv_equmentxq;
    @BindView(R.id.iv_equxq_xz)
    ImageView iv_equxq_xz;
    List<String> stringList;
    EqupmentXqAdapter equpmentXqAdapter;
    EquipmentImpl equipmentDao;
    Equpment equpment;
    String userId;
    SharedPreferences preferences;
    QMUITipDialog tipDialog;
    int resultCode = 0;
    List<Equpment> listEqu = new ArrayList<>();
    Equpment FirstEqu;
    @Override
    public void initParms(Bundle parms) {
        equpment = (Equpment) parms.getSerializable("equment");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_equipmentdetails;
    }

    @Override
    public void initView(View view) {
        resultCode=0;
        if (application == null) {
            application = (MyApplication) getApplication();
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getLong("userId",0)+"";
        stringList = new ArrayList<>();
        equipmentDao = new EquipmentImpl(getApplicationContext());
        listEqu= equipmentDao.findAll();
        for (int j=0;j<listEqu.size();j++){
            if (listEqu.get(j).getIsFirst()){
                FirstEqu = listEqu.get(j);
            }
        }
        if (equpment.getIsFirst()){
            iv_equxq_xz.setImageResource(R.mipmap.equ_choose);
        }
        for (int i = 0;i<6;i++){
            stringList.add(i+"");
        }
        equpmentXqAdapter = new EqupmentXqAdapter(this,stringList,equpment);
        rv_equmentxq.setLayoutManager(new LinearLayoutManager(this));
        rv_equmentxq.setAdapter(equpmentXqAdapter);
        equpmentXqAdapter.SetOnItemClick(new EqupmentXqAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0:
                        customDialog();

                        break;
                    case 1:
                        startActivity(EqupmentInformActivity.class);

                        break;
                    case 3:
                        startActivity(EqupmentLightActivity.class);
                        resultCode=2000;
                        break;
                    case 4:
                        startActivity(EqupmentWashActivity.class);
                        break;
                    case 5:
                        customDialog1();

                        break;
                }
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
    }

    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后。。。")
                .create();
        tipDialog.show();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_equ_fh,R.id.li_equ_choose})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_equ_fh:
                setResult(resultCode);
                finish();
                break;

            case R.id.li_equ_choose:
                if (equpment.getIsFirst()){
                   toast("当前设备以为默认设备");
                }else
                customDialog2();
                break;
        }
    }

    /**
     * 自定义对话框修改名称
     */
    Dialog dialog;
    int position ;
    String equName="";
    int Flag=-1; //g更新  0 更新名字 1 更新默认设备
    private void customDialog( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_rename, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        final EditText et_dia_name = view.findViewById(R.id.et_dia_name);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
                if (!TextUtils.isEmpty(et_dia_name.getText().toString().trim())){
                    showProgressDialog();
                    Map<String,Object> params = new HashMap<>();
                    params.put("id",equpment.getEqupmentId());
                    params.put("deviceName",et_dia_name.getText().toString().trim());
                    params.put("userId",userId);
                    equName = et_dia_name.getText().toString().trim();
                    Flag=0;
                    new UpdateDeviceAsyncTask().execute(params);
                    resultCode=2000;
                    dialog.dismiss();
                }else {
                    toast("设备名不能为空");
                }

            }
        });
        dialog.show();

    }
    /**
     * 自定义对话框删除设备
     */
    private void customDialog1( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
                showProgressDialog();
                Map<String,Object> params = new HashMap<>();
                params.put("id",equpment.getEqupmentId());
                params.put("userId",userId);
                new DeleteDeviceAsyncTask().execute(params);
                resultCode=2000;
                dialog.dismiss();

            }
        });
        dialog.show();

    }
    /**
     * 自定义对话框修改为默认设备
     */
    private void customDialog2( ) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText("默认设备");
        et_dia_name.setText("是否设为默认设备");
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
                showProgressDialog();
                iv_equxq_xz.setImageResource(R.mipmap.equ_choose);
                Map<String,Object> params = new HashMap<>();
                params.put("id",equpment.getEqupmentId());
                params.put("flag",1);
                params.put("userId",userId);
                new UpdateDeviceAsyncTask().execute(params);
                Flag=1;
                dialog.dismiss();
                resultCode=2000;
            }
        });
        dialog.show();

    }
    /*
    *
    *更新设备，设置为默认设备
    * */
    String returnMsg1;
    class UpdateDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/updateDevice",params);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1 = jsonObject.getString("message1");
                    } catch (JSONException e) {

                    }

                }else{
                    code="4000";
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case  "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    if (Flag==0){
                        if (!TextUtils.isEmpty(equName)){
                            equpment.setName(equName);
                            equipmentDao.update(equpment);
                            equName="";
                            equpmentXqAdapter.RefrashName(equpment);
                        }
                    }else {
                        equpment.setIsFirst(true);
                        FirstEqu.setIsFirst(false);
                        equipmentDao.update(FirstEqu);
                        equipmentDao.update(equpment);
                       List<Equpment>  equpments = equipmentDao.findAll();
            }
                    Flag=-1;
                    toast( returnMsg1);
                    break;

                case  "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;

                    default:
                        if (tipDialog!=null&&tipDialog.isShowing()){
                            tipDialog.dismiss();
                        }
                        toast( returnMsg1);
                        break;
            }
        }
    }

    /*
     *
     *删除设备
     * */
    class DeleteDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/deleteDevice",params);
            Log.e(TAG, "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1 = jsonObject.getString("message1");
                    } catch (JSONException e) {

                    }

                }else{
                    code="4000";
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case  "200":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    equipmentDao.delete(equpment);
                    toast( returnMsg1);
                    setResult(2000);
                    finish();
                    break;

                case  "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( "连接超时，请重试");
                    break;

                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    toast( returnMsg1);
                    break;
            }
        }
    }
}
