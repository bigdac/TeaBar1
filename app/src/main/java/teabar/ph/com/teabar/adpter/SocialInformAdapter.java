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
import teabar.ph.com.teabar.pojo.FriendInfor;


public class SocialInformAdapter extends RecyclerView.Adapter<SocialInformAdapter.MyviewHolder> {

    private List<FriendInfor> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public SocialInformAdapter(Context context , List<FriendInfor> list ) {
        this.context = context;
        this.mData = list;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_social_inform,viewGroup,false));
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {

         myviewHolder.tv_talk_name.setText(mData.get(position).getUseName());
         myviewHolder.bt_addFriend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 onItemClickListener.onItemClick(view,position);

             }
         });
         if (mData.get(position).getAddNum()==1){
            myviewHolder. bt_addFriend .setText("已同意");
             myviewHolder. bt_addFriend .setTextColor( context.getResources().getColor(R.color.login_gray));
            myviewHolder.bt_addFriend.setClickable(false);
            myviewHolder.bt_addFriend.setBackground(context.getDrawable( R.drawable.answer_button1) );
         }

    }

    public  List<FriendInfor> getmData (){
        return mData;
    }
    public   interface  OnItemClickListener {
        void onItemClick(View view, int position);

    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_inform_pic;
        TextView tv_talk_name;
        Button bt_addFriend;
        public MyviewHolder(View itemView){
            super(itemView);
            tv_talk_name = itemView.findViewById(R.id.tv_talk_name);
            iv_inform_pic = itemView.findViewById(R.id.iv_inform_pic);
            bt_addFriend = itemView.findViewById(R.id.bt_addFriend);
        }
    }
}
