package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                mData.set(i,equpment);
                notifyItemChanged(i);
            }
        }

    }

    public void setEqumentData1( List<Equpment> equpment){

               this. mData =equpment;
               notifyDataSetChanged();


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
    boolean  Open;
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if( mData.get(position).getIsFirst()){
           holder.iv_equ_choose.setVisibility(View.VISIBLE);
        }else {
            holder.iv_equ_choose.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mData.get(position).getName())){
            holder.tv_equ_name.setText(mData.get(position).getName());
        }

        if (mData.get(position).getMStage()==0){
            Open = false;
        }else {
            Open =true;
        }
        final boolean isOpen[] ={Open};
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
                        mData.get(position).setMStage(0);
                        isOpen[0]=false;
                    }else {
                        holder.iv_equ_open.setImageResource(R.mipmap.equ_open);
                        mData.get(position).setMStage(2);
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
            if (!TextUtils.isEmpty(mData.get(position).getLightColor())){
                String[] aa = mData.get(position).getLightColor().split("/");
                int red =0;
                int green=0;
                int blue=0;
                if (aa.length>=3){
                   red = Integer.valueOf(aa[0]);
                   green = Integer.valueOf(aa[1]);
                   blue = Integer.valueOf(aa[2]);
                }
                int color = Color.rgb(red, green, blue);
                holder.tv_equ_online.setText("在线");
                holder.tv_equ_online.setTextColor(context.getResources().getColor(R.color.nomal_green));
                GradientDrawable myGrad = (GradientDrawable)holder.tv_light_bj.getBackground();
                myGrad.setColor(color);
            }else {
                GradientDrawable myGrad = (GradientDrawable)holder.tv_light_bj.getBackground();
                myGrad.setColor(Color.parseColor("#bbbbbb"));
            }




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
        TextView  tv_equ_name,tv_light_bj,tv_equ_online;
        ImageView iv_equ_open,iv_equ_choose;
        RelativeLayout rl_equitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_equ_open = (ImageView) itemView.findViewById(R.id.iv_equ_open);
            tv_equ_name= (TextView)itemView.findViewById(R.id.tv_equ_name);
            tv_light_bj = itemView.findViewById(R.id.tv_light_bj);
            iv_equ_choose = itemView.findViewById(R.id.iv_equ_choose);
            iv_equ_choose.setVisibility(View.GONE);
            tv_equ_online = itemView.findViewById(R.id.tv_equ_online);
        }
    }


}
