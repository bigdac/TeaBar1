package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.pojo.Tea;

public class NearestAdpter extends RecyclerView.Adapter< NearestAdpter.MyViewHolder> {
    Context context;
    List<Tea> mData;

    public NearestAdpter(Context context, List<Tea> list ) {
        this.context = context;
        this.mData = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favirate,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.tv_name.setText(mData.get(i).getTeaNameEn());
        myViewHolder.bt_brew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MakeActivity.class);
                intent.putExtra("teaId",mData.get(i).getTeaId());
                context.startActivity(intent);
            }
        });
    }
    public void  setmData(List<Tea> mData){
        this.mData= mData;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_favirate_yes;
        TextView tv_name;
        Button bt_brew;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_favirate_yes = itemView.findViewById(R.id.iv_favirate_yes);
            tv_name = itemView.findViewById(R.id.tv_name);
            bt_brew = itemView.findViewById(R.id.bt_brew);
            iv_favirate_yes.setVisibility(View.GONE);
        }
    }
}
