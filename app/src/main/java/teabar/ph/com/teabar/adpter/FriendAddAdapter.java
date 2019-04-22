package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Friend;

public class FriendAddAdapter extends RecyclerView.Adapter<FriendAddAdapter.MyviewHolder> {

    private List<Friend> mData;
    private Context context;
    private  OnItemClickListener onItemClickListener;
    Boolean IsFriend = false;

    public FriendAddAdapter(Context context , List<Friend> list ) {
        this.context = context;
        this.mData = list;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_friendadd,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {
        if (IsFriend){
            myviewHolder.bt_friend_add.setVisibility(View.GONE);
        }else {
            myviewHolder.bt_friend_add.setVisibility(View.VISIBLE);
        }
        myviewHolder.friend_id.setText(mData.get(position).getId()+"");
        myviewHolder.tv_talk_name .setText(mData.get(position).getUserName());
        Glide.with(context).load(mData.get(position).getPhotoUrl())
                .placeholder(R.mipmap.my_pic)//图片加载出来前，显示的图片
                .fallback( R.mipmap.my_pic) //url为空的时候,显示的图片
                .error(R.mipmap.my_pic)//图片加载失败后，显示的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(myviewHolder.iv_talk_pic);
        myviewHolder.bt_friend_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view,position);
            }
        });

    }

    public List<Friend>  getmData(){
        return mData;
    }

    public void IsFriend (boolean isFriend ){
        this.IsFriend = isFriend;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public   interface  OnItemClickListener {
        void onItemClick(View view, int position);

    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_talk_pic;
        Button bt_friend_add;
        TextView tv_talk_name,friend_id ;
        public MyviewHolder(View itemView){
            super(itemView);
            bt_friend_add = itemView.findViewById(R.id.bt_friend_add);
            tv_talk_name = itemView.findViewById(R.id.tv_talk_name);
            friend_id  =itemView.findViewById(R.id.friend_id);
            iv_talk_pic = itemView.findViewById(R.id.iv_talk_pic);
        }
    }
}
