package teabar.ph.com.teabar.adpter;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.MyViewHolder>  {
    private Context mContext;
    private List<Tea> mData;
    String userId;

    public RecommendAdapter(Context context, List<Tea>list,String userId){
        this.mContext = context;
        this.mData = list;
        this.userId = userId;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recommend,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
        String headImg = mData.get(position).getTeaPicture();
        myViewHolder.tv_mail_name .setText(mData.get(position).getProductNameEn());
        Glide.with(mContext).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.white).into(myViewHolder.iv_mail_pic);

//        if (mData.get(position).isLove()){
//            Open = true;
//        }else {
//            Open =false;
//        }
//        final boolean isOpen[] ={Open};
//        if (!isOpen[0]){
//            myViewHolder.iv_mail_xa.setImageResource(R.mipmap.social_no);
//        }else {
//            myViewHolder.iv_mail_xa.setImageResource(R.mipmap.social_yes);
//
//        }
//        myViewHolder.iv_mail_xa.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if (isOpen[0]){
//                    customDialog1(position,false);
//
//                }else {
//                    customDialog1(position,true);
//
//                }
//
//            }
//        });
    }

    /**
     * 添加喜愛
     */
    Dialog dialog;
    boolean which;
    private void customDialog1(final int position , final boolean add) {
        dialog  = new Dialog(mContext, R.style.MyDialog);
        View view = View.inflate(mContext, R.layout.dialog_del, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dia_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dia_qd);
        TextView tv_dia_title = view.findViewById(R.id.tv_dia_title);
        TextView et_dia_name = view.findViewById(R.id.et_dia_name);
        if (add){
            tv_dia_title.setText( R.string.dialog_love_title);
            et_dia_name.setText(R.string.dialog_love_title1);
        }else {
            tv_dia_title.setText(R.string.dialog_love_deltitle );
            et_dia_name.setText(R.string.dialog_love_deltitle1 );
        }

        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(mContext).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(mContext).getScreenWidth() * 0.75f);
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

                notifyDataSetChanged();
                if (add){
                    mData.get(position).setLove(true);
                    which = true;
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId );
                    params.put("teaId",mData.get(position).getId() );
                    new CollectTeaAsyncTask().execute(params);


                }else {
                    mData.get(position).setLove(false);
                    which = false;
                    Map<String,Object> params = new HashMap<>();
                    params.put("userId",userId );
                    params.put("teaId",mData.get(position).getId() );
                    new CollectTeaAsyncTask().execute(params);

                }

                dialog.dismiss();

            }
        });
        dialog.show();

    }

    /**
     *  添加喜愛
     *
     */
    String returnMsg1;
    class CollectTeaAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            String ip;
            Map<String, Object> prarms = maps[0];
            if (which){
                ip ="/app/collectTea";
            }else {
                ip ="/app/cancelCollectTea";
            }
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

                    break;

                case "4000":
                    ToastUtil.showShort(mContext, "连接超时，请重试");
                    break;
                default:
                    ToastUtil.showShort(mContext, returnMsg1);

                    break;

            }
        }
    }

    boolean  Open;
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_mail_name;
        ImageView iv_mail_pic,iv_mail_xa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_mail_pic = itemView.findViewById(R.id.iv_mail_pic);
            tv_mail_name = itemView.findViewById(R.id.tv_mail_name);
            iv_mail_xa = itemView.findViewById(R.id.iv_mail_xa);
            iv_mail_xa.setVisibility(View.GONE);
        }
    }
    public void update(List<Tea> tea1 ){
        this.mData  = tea1;
        notifyDataSetChanged();
    }

}
