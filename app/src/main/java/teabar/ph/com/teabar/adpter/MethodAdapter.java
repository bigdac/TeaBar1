package teabar.ph.com.teabar.adpter;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.EquipmentDetailsActivity;
import teabar.ph.com.teabar.pojo.MakeMethod;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.MyviewHolder> {

    private List<MakeMethod> mData;
    private Context context;
    QMUITipDialog tipDialog;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public MethodAdapter(Context context , List<MakeMethod> list ) {
        this.context = context;
        this.mData = list;

    }
    //显示dialog
    public void showProgressDialog() {

        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后...")
                .create();
        tipDialog.show();
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_method,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {
        MakeMethod makeMethod = mData.get(position);
        myviewHolder.tv_method_temp.setText(makeMethod.getTemp()+"℃");
        myviewHolder.tv_method_capacity.setText(makeMethod.getCapacity()+"ml");
        myviewHolder.tv_method_time.setText(makeMethod.getTime()+"s");
        myviewHolder.tv_plan_name.setText(makeMethod.getName()+"");
        myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListerner.onClikner(view,position);
            }
        });
        myviewHolder.iv_method_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove= position;
              customDialog1(position);
            }
        });


    }
    /**
     * 自定义对话框删除设备
     */
    Dialog dialog;
    private void customDialog1(final int position ) {
        dialog  = new Dialog(context, R.style.MyDialog);
        View view = View.inflate(context, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText("删除方法");
        et_dia_name.setText("是否删除方法");
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(context).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.75f);
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
                params.put("brewId",mData.get(position).getId() );
                new DelMethordAsynTask().execute(params);
                dialog.dismiss();

            }
        });
        dialog.show();

    }



    int remove = -1;
    String returnMsg1,returnMsg2;
    class DelMethordAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/deleteBrew",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message2");

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
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    mData.remove(remove);
                    notifyDataSetChanged();
                    ToastUtil.showShort(context, returnMsg1);

                    break;
                case "4000":
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    ToastUtil.showShort(context,context.getText(R.string.toast_all_cs).toString());

                    break;
                default:
                    if (tipDialog!=null&&tipDialog.isShowing()){
                        tipDialog.dismiss();
                    }
                    ToastUtil.showShort(context, returnMsg1);
                    break;

            }
        }
    }


    public List<MakeMethod> getmData(){
        return mData;
    }

    public void SetOnclickLister(OnItemClickListerner itemClickListerner){
        this.itemClickListerner = itemClickListerner;
    }

    OnItemClickListerner itemClickListerner;
    public interface OnItemClickListerner {
        void onClikner(View view,int position);
    }

    public void setMethod(List<MakeMethod> list){
        this .mData = list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_method_del;
        TextView tv_method_temp,tv_method_capacity,tv_method_time,tv_plan_name;
        public MyviewHolder(View itemView){
            super(itemView);
            iv_method_del = itemView.findViewById(R.id.iv_method_del);
            tv_method_temp = itemView.findViewById(R.id.tv_method_temp);
            tv_method_capacity = itemView.findViewById(R.id.tv_method_capacity);
            tv_method_time = itemView.findViewById(R.id.tv_method_time);
            tv_plan_name = itemView.findViewById(R.id.tv_plan_name);
        }
    }
}
