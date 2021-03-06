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

public class EqupmentAdapter extends RecyclerView.Adapter<EqupmentAdapter.MyViewHolder> {

    private List<Equpment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private  OnopenClickListener onopenClickListener;
    private  OnlightClickListener onlightClickListener;
    private boolean isShare=false;
    public EqupmentAdapter(Context context , List<Equpment> list ) {
        this.context = context;
        this.mData = list;
        mData.add(new Equpment());

    }
    public List<Equpment> getmData(){
        return mData;
    }

    public void setEqumentData(String macAddress,Equpment equpment){
        for (int i = 0;i<mData.size();i++){
                Equpment equpment1 = mData.get(i);
            if ((macAddress).equals(equpment1.getMacAdress()) ){
                mData.set(i,equpment);

            }
        }
        notifyDataSetChanged();
    }

    public void setEqumentData1( List<Equpment> equpment){
                this.mData.clear();
               this. mData =equpment;
               mData.add(new Equpment());
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
        if (position<mData.size()-1) {
            holder.rl_equ_noequ.setVisibility(View.GONE);
            holder.rl_equ_hasequ.setVisibility(View.VISIBLE);
            if (mData.get(position).getIsFirst()) {
                holder.iv_equ_choose.setVisibility(View.VISIBLE);
            } else {
                holder.iv_equ_choose.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mData.get(position).getName())) {
                holder.tv_equ_name.setText(mData.get(position).getName());
            }
            int litght = mData.get(position).getLightOpen();
            switch (mData.get(position).getMStage()) {
                case 0xb2:
                    Open = false;

                    break;
                case -1:
                    Open = false;
                    break;

                default:
                    Open = true;
                    break;
            }
            if (mData.get(position).getOnLine()) {
                holder.iv_equ_equ.setImageResource(R.mipmap.device_online);
            } else {
                holder.iv_equ_equ.setImageResource(R.mipmap.device_outline);
            }

            final boolean isOpen[] = {Open};
            if (!isOpen[0]) {
                holder.iv_equ_open.setImageResource(R.mipmap.device_close);
            } else {
                holder.iv_equ_open.setImageResource(R.mipmap.device_open);

            }
            holder.iv_equ_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData.get(position).getOnLine()) {
                        if (!Utils.isFastClick()) {
                            if (mData.get(position).getMStage() != 0xb6 &&mData.get(position).getMStage() != 0xb7) {
                                Log.e("TTTTT", "onClick: -->" + isOpen[0]);
                                if (isOpen[0]) {
                                    holder.iv_equ_open.setImageResource(R.mipmap.device_close);
                                    mData.get(position).setMStage(0xb2);
                                    isOpen[0] = false;
                                } else {
                                    holder.iv_equ_open.setImageResource(R.mipmap.device_open);
                                    mData.get(position).setMStage(0xb0);
                                    isOpen[0] = true;
                                }
                                onopenClickListener.onItemClick(view, position, isOpen[0]);
                            } else {
                                ToastUtil.showShort(context, context.getText(R.string.toast_updata_no).toString());
                            }
                        } else {
                            ToastUtil.showShort(context, context.getText(R.string.toast_equ_fast).toString());
                        }
                    } else {
                        ToastUtil.showShort(context, context.getText(R.string.toast_equ_online).toString());
                    }
                }
            });
            /*
            * 灯光控制
            * */
            /*holder.iv_equ_light.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData.get(position).getOnLine()) {
                        if (!Utils.isFastClick()) {
                            if (mData.get(position).getMStage() != 0xb6 || mData.get(position).getMStage() != 0xb7) {
                                Equpment equpment = mData.get(position);
                                if (equpment.getLightOpen() == 0) {
                                    equpment.setLightOpen(128);
                                    mData.set(position, equpment);
                                    holder.iv_equ_light.setImageTintList(ColorStateList.valueOf(color));

                                } else {
                                    equpment.setLightOpen(0);
                                    mData.set(position, equpment);
                                    holder.iv_equ_light.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bbbbbb")));
                                }
                                onlightClickListener.onItemClick(view, position, equpment.getLightOpen());
                            } else {
                                ToastUtil.showShort(context, context.getText(R.string.toast_updata_no).toString());
                            }
                        } else {
                            ToastUtil.showShort(context, context.getText(R.string.toast_equ_fast).toString());
                        }
                    } else {
                        ToastUtil.showShort(context, context.getText(R.string.toast_equ_online).toString());
                    }
                }
            });
*/
            if (!TextUtils.isEmpty(mData.get(position).getLightColor())) {
                String[] aa = mData.get(position).getLightColor().split("/");
                int red = 0;
                int green = 0;
                int blue = 0;
                if (aa.length >= 3) {
                    red = Integer.valueOf(aa[0]);
                    green = Integer.valueOf(aa[1]);
                    blue = Integer.valueOf(aa[2]);
                }
                color = Color.rgb(red, green, blue);
                if (litght == 0) {
                    holder.iv_equ_light.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bbbbbb")));
                } else
                    holder.iv_equ_light.setImageTintList(ColorStateList.valueOf(color));

            } else {
                    holder.iv_equ_light.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bbbbbb")));
            }

        }else {
            holder.rl_equ_noequ.setVisibility(View.VISIBLE);
            holder.rl_equ_hasequ.setVisibility(View.GONE);
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
    int color;
//    public void RefrashData(List<Equpment> list){
//        this.mData =list;
//    }

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
        void onItemClick(View view, int position,boolean b);

    }

    /*燈光按鈕的點擊*/
    public void SetlightItemClick(OnlightClickListener onlightClickListener){
        this.onlightClickListener = onlightClickListener ;
    }
    public interface OnlightClickListener {
        void onItemClick(View view, int position,int b);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  tv_equ_name;
        ImageView iv_equ_open,iv_equ_choose ,iv_equ_light,iv_equ_equ;
        RelativeLayout rl_equ_hasequ,rl_equ_noequ ;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_equ_open = (ImageView) itemView.findViewById(R.id.iv_equ_open);
            tv_equ_name= (TextView)itemView.findViewById(R.id.tv_equ_name);
            iv_equ_choose = itemView.findViewById(R.id.iv_equ_choose);
            iv_equ_light = itemView.findViewById(R.id.iv_equ_light);
            iv_equ_choose.setVisibility(View.GONE);
            iv_equ_equ = itemView.findViewById(R.id.iv_equ_equ);
            rl_equ_hasequ = itemView.findViewById(R.id.rl_equ_hasequ);
            rl_equ_noequ = itemView.findViewById(R.id.rl_equ_noequ);
        }
    }


}
