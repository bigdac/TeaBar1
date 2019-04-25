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
import teabar.ph.com.teabar.pojo.Equpment;

public class EqupmentAdapter extends RecyclerView.Adapter<EqupmentAdapter.MyViewHolder> {

    private List<Equpment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private  OnopenClickListener onopenClickListener;

    private boolean isShare=false;
    public EqupmentAdapter(Context context , List<Equpment> list ) {
        this.context = context;
        this.mData = list;

    }
    public List<Equpment> getmData(){
        return mData;
    }

    public void setEqumentData(String macAddress,Equpment equpment){
        for (int i = 0;i<mData.size();i++){
            Equpment equpment1 = mData.get(i);
            if ((macAddress).equals(equpment1.getMacAdress()) ){
                mData.set(i,equpment1);
                notifyItemChanged(i);
            }
        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_equipment,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if( mData.get(position).getIsFirst()){
           holder.iv_equ_choose.setVisibility(View.VISIBLE);
        }else {
            holder.iv_equ_choose.setVisibility(View.GONE);
        }

        final boolean isOpen[] ={true};
        if (!isOpen[0]){
            holder.iv_equ_open.setImageResource(R.mipmap.equ_close);
        }else {
            holder.iv_equ_open.setImageResource(R.mipmap.equ_open);

        }
            holder.iv_equ_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isOpen[0]){
                        holder.iv_equ_open.setImageResource(R.mipmap.equ_close);
                        isOpen[0]=false;
                    }else {
                        holder.iv_equ_open.setImageResource(R.mipmap.equ_open);
                        isOpen[0]=true;
                    }
                    onopenClickListener.onItemClick(view,position,isOpen[0]);
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
    public void SetopenItemClick(OnopenClickListener onopenClickListener){
        this.onopenClickListener = onopenClickListener ;
    }
    public interface OnopenClickListener {
        void onItemClick(View view, int position,boolean b);


    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tv_equ_name;
        ImageView iv_equ_open,iv_equ_choose;
        RelativeLayout rl_equitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_equ_open = (ImageView) itemView.findViewById(R.id.iv_equ_open);
            tv_equ_name= (TextView)itemView.findViewById(R.id.tv_equ_name);
            iv_equ_choose = itemView.findViewById(R.id.iv_equ_choose);
            iv_equ_choose.setVisibility(View.GONE);

        }
    }


}
