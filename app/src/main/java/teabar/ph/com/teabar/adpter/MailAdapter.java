package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Tea;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MyViewHolder>  {
    private Context mContext;
    private List<Tea> mData;

    public MailAdapter(Context context, List<Tea>list){
        this.mContext = context;
        this.mData = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_mailpic,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        String headImg = mData.get(position).getTeaPicture();
        myViewHolder.tv_mail_name .setText(mData.get(position).getProductNameEn());
        Glide.with(mContext).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.white).into(myViewHolder.iv_mail_pic);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_mail_name;
        ImageView iv_mail_pic;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_mail_pic = itemView.findViewById(R.id.iv_mail_pic);
            tv_mail_name = itemView.findViewById(R.id.tv_mail_name);
        }
    }
    public void update(List<Tea> tea1 ){
        this.mData  = tea1;
        notifyDataSetChanged();
    }

}
