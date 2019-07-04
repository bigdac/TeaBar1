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
import teabar.ph.com.teabar.util.GlideCircleTransform;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.view.ScreenSizeUtils;

public class Recommend1Adapter extends RecyclerView.Adapter<Recommend1Adapter.MyViewHolder>  {
    private Context mContext;
    private List<Tea> mData;


    public Recommend1Adapter(Context context, List<Tea>list ){
        this.mContext = context;
        this.mData = list;

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
        Glide.with(mContext).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.white).transform(new GlideCircleTransform(mContext)).into(myViewHolder.iv_mail_pic);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListerner.onClikner(view,position);
            }
        });
    }

    public List<Tea> getmData(){
        return mData;
    }

    public void SetOnclickLister( OnItemClickListerner itemClickListerner){
        this.itemClickListerner = itemClickListerner;
    }

    OnItemClickListerner itemClickListerner;
    public interface OnItemClickListerner {
        void onClikner(View view,int position);
    }
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
