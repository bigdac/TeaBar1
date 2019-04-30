package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.pojo.Comment;
import teabar.ph.com.teabar.util.GlideCircleTransform;

public class AllCommentAdapter extends RecyclerView.Adapter<AllCommentAdapter.MyViewHolder> {

    private List<Comment> mDatas = new ArrayList<>();
    private Context mContext;
    public AllCommentAdapter(Context context, List<Comment> list) {
        this.mContext = context;
        this.mDatas = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_allcomment,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_all_name.setText(mDatas.get(i).getCommentUserName());
        myViewHolder.tv_all_mess.setText(mDatas.get(i).getComment());
        String headImg = mDatas.get(i).getPhotoUrl();
        Glide.with(mContext).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(mContext)).into(myViewHolder.iv_all_head);

    }

    public void  setmDatas(List<Comment> list){
        this.mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_all_head;
        TextView tv_all_name,tv_all_mess;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           iv_all_head = itemView.findViewById(R.id.iv_all_head);
           tv_all_name = itemView.findViewById(R.id.tv_all_name);
           tv_all_mess = itemView.findViewById(R.id.tv_all_mess);
        }
    }


}
