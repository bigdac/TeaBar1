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
import teabar.ph.com.teabar.pojo.News;
import teabar.ph.com.teabar.util.GlideCircleTransform;

public class NewFeedAdapter extends RecyclerView.Adapter<NewFeedAdapter.MyViewHolder> {

    private List<News> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;


    private boolean isShare=false;
    public NewFeedAdapter(Context context , List<News> list ) {
        this.context = context;
        this.mData = list;

    }

    

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iteam_newfeed,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_new_title.setText(mData.get(position).getTitile());
        holder.tv_new_mes.setText(mData.get(position).getContent());
        Glide.with(context).load(mData.get(position).getNewsPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.nomal_green).into(holder.iv_new_pic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    onItemClickListener.onLongClick(v, position);
//                    return false;
//                }
//            });


    }

    public void  setmData(List mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<News> getmData(){
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void SetOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_new_pic ;
        TextView tv_new_title,tv_new_mes;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_new_pic= itemView.findViewById(R.id.iv_new_pic);
            tv_new_title = itemView.findViewById(R.id.tv_new_title);
            tv_new_mes = itemView.findViewById(R.id.tv_new_mes);
        }
    }


}
