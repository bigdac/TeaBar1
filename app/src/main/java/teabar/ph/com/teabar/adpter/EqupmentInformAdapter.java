package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;

public class EqupmentInformAdapter extends RecyclerView.Adapter<EqupmentInformAdapter.MyViewHolder> {

    private List<String> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;


    private boolean isShare=false;
    public EqupmentInformAdapter(Context context , List<String> list ) {
        this.context = context;
        this.mData = list;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_equmentinform,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        switch (position){
            case 0:
                holder.tv_equ_name.setText(R.string.equ_xq_finish);
                break;
            case 1:
                holder.tv_equ_name.setText(R.string.equ_xq_yrfinish);

                break;
            case 2:
                holder.tv_equ_name.setText(R.string.equ_xq_water);

                break;
            case 3:
                holder.tv_equ_name.setText(R.string.equ_xq_full);


                break;
            case 4:
                holder.tv_equ_name.setText(R.string.equ_xq_wash);

                break;

        }
        final boolean isOpen[] ={true};
        if (!isOpen[0]){
            holder.iv_equ_choose.setImageResource(R.mipmap.equ_close);
        }else {
            holder.iv_equ_choose.setImageResource(R.mipmap.equ_open);

        }
        holder.iv_equ_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen[0]){
                    holder.iv_equ_choose.setImageResource(R.mipmap.equ_close);
                    isOpen[0]=false;
                }else {
                    holder.iv_equ_choose.setImageResource(R.mipmap.equ_open);
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
        TextView  tv_equ_name  ;
        ImageView iv_equ_choose;
        RelativeLayout rl_equitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_equ_choose = (ImageView) itemView.findViewById(R.id.iv_equ_choose);
            tv_equ_name= (TextView)itemView.findViewById(R.id.tv_equ_name);

        }
    }


}
