package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;

public class ChooseDeviceAdapter extends RecyclerView.Adapter<ChooseDeviceAdapter.MyViewHolder> {

    private List<Equpment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private  OnopenClickListener onopenClickListener;
    private  OnlightClickListener onlightClickListener;
    private boolean isShare=false;
    public ChooseDeviceAdapter(Context context , List<Equpment> list ) {
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
                boolean ishot = equpment1.getInform_isHot();
                equpment.setInform_isHot(ishot);
                mData.set(i,equpment);

            }
        }
        notifyDataSetChanged();
    }

    public void setEqumentData1( List<Equpment> equpment){
               this. mData =equpment;
               notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choosedevice,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }
    boolean Open = false;
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final boolean isOpen[] ={  mData.get(position).getInform_isHot()};

            if (!TextUtils.isEmpty(mData.get(position).getName())) {
                holder. tv_choose_name.setText(mData.get(position).getName());
            }

            if (mData.get(position).getOnLine()) {
                holder.iv_choose_online.setImageResource(R.mipmap.device_online);
            } else {
                holder.iv_choose_online.setImageResource(R.mipmap.device_outline);
            }

        if (isOpen[0]){
             holder.rl_device_xz.setBackgroundResource(R.mipmap.choose_devicebj1);

        }else {
            holder.rl_device_xz.setBackgroundResource(R.mipmap.choose_devicebj);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClickListener.onItemClick(v, position);
                for (int i =0;i<mData.size();i++) {
                    if (i==position) {
                        mData.get(i).setInform_isHot(true);
                    } else {
                        mData.get(i).setInform_isHot(false);
                    }
                }
                notifyDataSetChanged();
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


    @Override
    public int getItemCount() {
        return mData.size();
    }


    /*item 的點擊*/
    public void SetOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int posotion);
    }

   /*開關按鈕的點擊*/
    public void SetopenItemClick(OnopenClickListener onopenClickListener){
        this.onopenClickListener = onopenClickListener ;
    }
    public interface OnopenClickListener {
        void onItemClick(View view, int position, boolean b);

    }

    /*燈光按鈕的點擊*/
    public void SetlightItemClick(OnlightClickListener onlightClickListener){
        this.onlightClickListener = onlightClickListener ;
    }
    public interface OnlightClickListener {
        void onItemClick(View view, int position, int b);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_choose_online  ;
        TextView tv_choose_name ;
        RelativeLayout rl_device_xz;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_choose_online = itemView.findViewById(R.id.iv_choose_online);
            tv_choose_name = itemView.findViewById(R.id.tv_choose_name);
            rl_device_xz = itemView.findViewById(R.id.rl_device_xz);
        }
    }


}
