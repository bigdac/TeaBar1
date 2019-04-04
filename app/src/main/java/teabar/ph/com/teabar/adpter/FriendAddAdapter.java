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

import java.util.List;

import teabar.ph.com.teabar.R;

public class FriendAddAdapter extends RecyclerView.Adapter<FriendAddAdapter.MyviewHolder> {

    private List<String> mData;
    private Context context;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public FriendAddAdapter(Context context , List<String> list ) {
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
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, int position) {


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void IsFriend(){

    }
    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_talk_pic;
        Button bt_friend_add;
        TextView tv_talk_name,tv_talk_time,tv_message;
        public MyviewHolder(View itemView){
            super(itemView);
            bt_friend_add = itemView.findViewById(R.id.bt_friend_add);
        }
    }
}
