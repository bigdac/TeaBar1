package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Equpment;

public class EqupmentXqAdapter extends RecyclerView.Adapter<EqupmentXqAdapter.MyViewHolder> {

    private List<String> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    boolean isOpen =true;
    private  Equpment equpment;

    private boolean isShare=false;
    public EqupmentXqAdapter(Context context , List<String> list ,Equpment equpment) {
        this.context = context;
        this.mData = list;
        this.equpment = equpment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_equmentxq,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_equ_xq1.setVisibility(View.GONE);
        switch (position){
            case 0:
                holder.tv_equ_name.setText(R.string.equ_xq_name);
                String name = equpment.getName();
                holder.tv_equ_xq.setText(name);
                holder.tv_equ_xq.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.tv_equ_name.setText(R.string.equ_xq_inform);
                holder.tv_equ_xq.setVisibility(View.INVISIBLE);

                break;
            case 2:
                holder.tv_equ_name.setText(R.string.equ_xq_state);
                holder.iv_equ_choose.setVisibility(View.GONE);
                holder.tv_equ_xq.setVisibility(View.GONE);
                holder.tv_equ_xq1.setVisibility(View.VISIBLE);
                String s ="";
                switch (equpment.getMStage()){
                    case 0:
                        s=context.getResources().getText(R.string.equ_xq_nohot).toString();
                        break;
                    case 1:

                        s=context.getResources().getText(R.string.equ_xq_ishot).toString();
                        break;
                    case 2:

                        s=context.getResources().getText(R.string.equ_xq_dg).toString();
                        break;
                    case 3:
                    case 4:
                    case 5:
                        s=context.getResources().getText(R.string.equ_xq_xm).toString();
                        break;
                }
                holder.tv_equ_xq1.setText(s);
                break;
            case 3:
                holder.tv_equ_name.setText(R.string.equ_xq_light);
                holder.tv_equ_xq.setVisibility(View.GONE);

                break;
            case 4:
                holder.tv_equ_name.setText(R.string.equ_xq_day);
                holder.tv_equ_xq.setVisibility(View.INVISIBLE);

                break;
            case 5:
                holder.tv_equ_name.setText(R.string.equ_xq_delt);
                holder.tv_equ_xq.setVisibility(View.INVISIBLE);

                break;
        }

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
    public void RefrashName(Equpment equpment){
        this.equpment = equpment;
        notifyDataSetChanged();
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

        void onLongClick(View view, int posotion);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tv_equ_name ,tv_equ_xq,tv_equ_xq1;
        ImageView iv_equ_choose;
        RelativeLayout rl_equitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_equ_choose = (ImageView) itemView.findViewById(R.id.iv_equ_choose);
            tv_equ_name= (TextView)itemView.findViewById(R.id.tv_equ_name);
            tv_equ_xq= (TextView)itemView.findViewById(R.id.tv_equ_xq);
            tv_equ_xq1= (TextView)itemView.findViewById(R.id.tv_equ_xq1);
        }
    }


}
