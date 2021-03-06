package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Adress;
import teabar.ph.com.teabar.util.Utils;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder>  {
    private Context mContext;
    private List<Adress> mData;
    private int num;/* 0 中文 1 英文*/

    public AddressAdapter(Context context, List<Adress>list,int num){
        this.mContext = context;
        this.mData = list;
        this.num = num;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        if (position<mData.size()) {
            if (num == 0) {
                myViewHolder.tv_address_place.setText(mData.get(position).getCname());
            } else {
                myViewHolder.tv_address_place.setText(mData.get(position).getEname());
            }

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    itemClickListerner.onClikner(view, position);

                }
            });
        }
    }

    public List<Adress> getmData(){
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_address_place;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_address_place = itemView.findViewById(R.id.tv_address_place);
        }
    }
    public void update(List<Adress> tea1 ){
        this.mData  = tea1;
        notifyDataSetChanged();
    }
    public void SetOnclickLister(OnItemClickListerner itemClickListerner){

        this.itemClickListerner = itemClickListerner;

    }

    OnItemClickListerner itemClickListerner;
    public interface OnItemClickListerner {
        void onClikner(View view,int position);
    }
}
