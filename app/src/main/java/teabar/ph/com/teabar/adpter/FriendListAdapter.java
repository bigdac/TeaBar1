package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;
import teabar.ph.com.teabar.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyviewHolder> {

    private List<UserInfo> mData;
    private Context context;
    private   OnItemClickListener onItemClickListener;

    public FriendListAdapter(Context context , List<UserInfo> list ) {
        this.context = context;
        this.mData = list;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_friendlist,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {
            myviewHolder.tv_talk_name.setText(mData.get(position).getNickname());
            myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view,position);
                }
            });
            myviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongItemClick(view,position);
                    return false;
                }
            });
    }
    public void setmData(List list){
        this.mData = list;
    }
    public  List<UserInfo>  getmData(){
        return mData;
    }

    public   interface  OnItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_talk_pic;
        TextView tv_talk_name ;
        public MyviewHolder(View itemView){
            super(itemView);
            tv_talk_name = itemView.findViewById(R.id.tv_talk_name);
        }
    }
}
