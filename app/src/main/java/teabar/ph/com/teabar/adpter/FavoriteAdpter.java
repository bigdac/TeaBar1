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

public class FavoriteAdpter extends RecyclerView.Adapter< FavoriteAdpter.MyViewHolder> {
    Context context;
    List<String> mData;

    public FavoriteAdpter(Context context, List<String> list ) {
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
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
//

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_favirate_yes;
        TextView tv_name,tv_title;
        Button bt_brew;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_favirate_yes = itemView.findViewById(R.id.iv_favirate_yes);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            bt_brew = itemView.findViewById(R.id.bt_brew);

        }
    }
}
