package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.view.PlanProgressBar;

public class MyplanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TOP = 0;
    private static final int TYPE_BUTTON = 1;
    private List<String> mDatas = new ArrayList<>();
    private Context mContext;
    public MyplanAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mDatas = list;
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (position==2){
            return 1;
        }else {
            return 0;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // 个人计划卡片
        if (  position== TYPE_TOP ) {
          MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_myplan,viewGroup,false));
            return holder;
        }
        // 健康计划卡片
        if ( position== TYPE_BUTTON ) {
            MyViewHolder2 holder = new MyViewHolder2(LayoutInflater.from(mContext).inflate(R.layout.item_myhealthp,viewGroup,false));
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  holder, int position) {
        if (holder instanceof MyViewHolder) {
            if (position == 0) {
                ((MyViewHolder)holder).pl_progress.setProgress(30);
            }

            if (position == 1) {
                ((MyViewHolder)holder).pl_progress.setProgress(5);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_plan_pic;
            TextView tv_plan_name, tv_tea_name, tv_plan_day;
            PlanProgressBar pl_progress;

            public MyViewHolder(View itemView) {
                super(itemView);
                iv_plan_pic = itemView.findViewById(R.id.iv_plan_pic);
                tv_plan_name = itemView.findViewById(R.id.tv_plan_name);
                tv_tea_name = itemView.findViewById(R.id.tv_tea_name);
                tv_plan_day = itemView.findViewById(R.id.tv_plan_day);
                pl_progress = itemView.findViewById(R.id.pl_progress);

            }
        }
    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView iv_plan_pic;
        TextView tv_plan_name, tv_tea_name, tv_plan_day;
        PlanProgressBar pl_progress;

        public MyViewHolder2(View itemView) {
            super(itemView);
            iv_plan_pic = itemView.findViewById(R.id.iv_plan_pic);
            tv_plan_name = itemView.findViewById(R.id.tv_plan_name);
            tv_tea_name = itemView.findViewById(R.id.tv_tea_name);
            tv_plan_day = itemView.findViewById(R.id.tv_plan_day);
            pl_progress = itemView.findViewById(R.id.pl_progress);

        }
    }
}
