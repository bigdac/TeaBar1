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

public class MyquestionAdapter extends RecyclerView.Adapter<MyquestionAdapter.MyviewHolder> {

    private List<String> mData;
    private Context context;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public MyquestionAdapter(Context context , List<String> list ) {
        this.context = context;
        this.mData = list;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_myquestion,viewGroup,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, int position) {


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{

        TextView tv_plan_name,tv_tea_name,tv_ques_fs;
        public MyviewHolder(View itemView){
            super(itemView);

             tv_plan_name = itemView.findViewById(R.id.tv_plan_name);
             tv_tea_name = itemView.findViewById(R.id.tv_tea_name);
            tv_ques_fs = itemView.findViewById(R.id.tv_ques_fs);
        }
    }
}
