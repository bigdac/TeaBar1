package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.question.QuestionScoreActivity;
import teabar.ph.com.teabar.pojo.ScoreRecords;

public class MyquestionAdapter extends RecyclerView.Adapter<MyquestionAdapter.MyviewHolder> {

    private List<ScoreRecords> mData;
    private Context context;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public MyquestionAdapter(Context context , List<ScoreRecords> list ) {
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
    public void onBindViewHolder(@NonNull final MyviewHolder myviewHolder, final int position) {
        String s = context.getText(R.string.question_title).toString();
        myviewHolder.tv_plan_name.setText(s+"  "+(position+1));
//        myviewHolder.tv_tea_name.setText(mData.get(position).getCreateTime());
        myviewHolder.tv_ques_fs.setText(mData.get(position).getGrades());
        myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,QuestionScoreActivity.class);
                intent.putExtra("ScoreRecords",mData.get(position));
                intent.putExtra("name",myviewHolder.tv_plan_name.getText().toString());
                context.startActivity(intent);

            }
        });

    }

    public  void setmDatas (List<ScoreRecords> scoreRecords){
         this.mData = scoreRecords;
         notifyDataSetChanged();
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
