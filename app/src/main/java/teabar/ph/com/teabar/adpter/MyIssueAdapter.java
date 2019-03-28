package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;

public class MyIssueAdapter extends RecyclerView.Adapter<MyIssueAdapter.MyViewHolder> {

    private List<String> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    PictureAdapter pictureAdapter;
    IssueTalkAdapter issueTalkAdapter;

    private boolean isShare=false;
    public MyIssueAdapter(Context context , List<String> list ) {
        this.context = context;
        this.mData = list;
        List<String> list1 = new ArrayList<>();
        for (int i = 0;i<2;i++){
            list1.add(i+"");
        }
       pictureAdapter = new PictureAdapter(context,list1);
       issueTalkAdapter = new IssueTalkAdapter(context,list1);

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_myissuel,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.rv_pic.setLayoutManager(new GridLayoutManager(context,2));
        holder.rv_pic.setAdapter(pictureAdapter);
        holder.rv_issue_talk.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_issue_talk.setAdapter(issueTalkAdapter);
        final boolean isOpen[] ={true};
        if (!isOpen[0]){
            holder.iv_social_no.setImageResource(R.mipmap.social_no);
        }else {
            holder.iv_social_no.setImageResource(R.mipmap.social_yes);

        }
        holder.iv_social_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen[0]){
                    holder.iv_social_no.setImageResource(R.mipmap.social_no);
                    isOpen[0]=false;
                }else {
                    holder.iv_social_no.setImageResource(R.mipmap.social_yes);
                    isOpen[0]=true;
                }
            }
        });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    onItemClickListener.onLongClick(v, position);
                    return false;
                }
            });


    }
//    public void RefrashData(List<Equpment> list){
//        this.mData =list;
//    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void SetOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int posotion);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tv_social_name,tv_social_time ,tv_social_text ,tv_social_num;
        ImageView  iv_social_talk,iv_social_no;
        RecyclerView rv_pic,rv_issue_talk;
        public MyViewHolder(View itemView) {
            super(itemView);

            iv_social_talk = (ImageView) itemView.findViewById(R.id.iv_social_talk);
            iv_social_no = (ImageView) itemView.findViewById(R.id.iv_social_no);
            tv_social_name= (TextView)itemView.findViewById(R.id.tv_social_name);
            tv_social_time= (TextView)itemView.findViewById(R.id.tv_social_time);
            tv_social_text= (TextView)itemView.findViewById(R.id.tv_social_text);
            tv_social_num= (TextView)itemView.findViewById(R.id.tv_social_num);
            rv_pic = itemView.findViewById(R.id.rv_pic);
            rv_issue_talk = itemView.findViewById(R.id.rv_issue_talk);
        }
    }


}
