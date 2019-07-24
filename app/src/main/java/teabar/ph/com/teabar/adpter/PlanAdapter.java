package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.view.PlanProgressBar;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.MyviewHolder> {

    private List<String> mData;
    private Context context;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public PlanAdapter(Context context , List<String> list ) {
        this.context = context;
        this.mData = list;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_myplan,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, int position) {



        if (position==0){
            myviewHolder.pl_progress.setProgress(100);
            myviewHolder.tv_plan_day.setText("Week 10/10");
        }

        if (position==1){
            myviewHolder.pl_progress.setProgress(10);
            myviewHolder.tv_plan_day.setText("Week 1/10");
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        ImageView iv_plan_pic;
        TextView tv_plan_name,tv_tea_name,tv_plan_day;
        PlanProgressBar pl_progress;

        public MyviewHolder(View itemView){
            super(itemView);
            iv_plan_pic = itemView.findViewById(R.id.iv_plan_pic);
            tv_plan_name = itemView.findViewById(R.id.tv_plan_name);
            tv_tea_name = itemView.findViewById(R.id.tv_tea_name);
            tv_plan_day = itemView.findViewById(R.id.tv_plan_day);
            pl_progress = itemView.findViewById(R.id.pl_progress);

        }
    }
}
