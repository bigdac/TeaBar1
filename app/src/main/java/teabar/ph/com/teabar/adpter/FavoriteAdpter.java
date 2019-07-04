package teabar.ph.com.teabar.adpter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class FavoriteAdpter extends RecyclerView.Adapter< FavoriteAdpter.MyViewHolder> {
    Context context;
    List<Tea> mData;
    String userId;
    public FavoriteAdpter(Context context, List<Tea> list ,String usetId) {
        this.context = context;
        this.mData = list;
        this.userId =usetId;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favirate,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.tv_name.setText(mData.get(i).getProductNameEn());
        myViewHolder.iv_favirate_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog1(i);
            }
        });
        myViewHolder.bt_brew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MakeActivity.class);
                intent.putExtra("teaId",mData.get(i).getTeaId());
                context.startActivity(intent);
            }
        });
    }

    /**
     * 取消喜愛
     */
    Dialog dialog;
    int which;
    private void customDialog1(final int position) {
        dialog  = new Dialog(context, R.style.MyDialog);
        View view = View.inflate(context, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        tv_dia_title.setText(R.string.dialog_love_deltitle );
        et_dia_name.setText(R.string.dialog_love_deltitle1 );
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(context).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.45f);
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
                    which = position;
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId );
                    params.put("teaId",mData.get(position).getTeaId() );
                    new CollectTeaAsyncTask().execute(params);
                   dialog.dismiss();

            }
        });
        dialog.show();

    }

    /**
     *  q取消喜愛
     *
     */
    String returnMsg1;
    class CollectTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
            ip ="/app/cancelCollectTea";
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+ip,prarms);
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");

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
                mData.remove(which);
                notifyDataSetChanged();
                    break;

                case "4000":
                    ToastUtil.showShort(context, "连接超时，请重试");
                    break;
                default:
                    ToastUtil.showShort(context, returnMsg1);

                    break;

            }
        }
    }


    public void  setmData(List<Tea> mData){
        this.mData= mData;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_favirate_yes;
        TextView tv_name ;
        TextView bt_brew;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_favirate_yes = itemView.findViewById(R.id.iv_favirate_yes);
            tv_name = itemView.findViewById(R.id.tv_name);
            bt_brew = itemView.findViewById(R.id.bt_brew);

        }
    }
}
