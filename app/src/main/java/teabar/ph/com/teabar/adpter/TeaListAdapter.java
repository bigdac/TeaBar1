package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;

public class TeaListAdapter extends RecyclerView.Adapter<TeaListAdapter.MyViewHolder> {

    private List<String> mDatas = new ArrayList<>();
    private Context mContext;
    TeaAdapter teaAdapter;
    public TeaListAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mDatas = list;
        teaAdapter = new TeaAdapter(context,list);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tealist1,viewGroup,false));
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        switch (position){
            case 1:
                myViewHolder.tv_tea_name.setText("水果口味");
                break;
            case 2:
                myViewHolder.tv_tea_name.setText("功能茶");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_tea_list1;
        TextView tv_tea_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_tea_list1 = itemView.findViewById(R.id.rv_tea_list1);
            tv_tea_name = itemView.findViewById(R.id.tv_tea_name);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
             rv_tea_list1.setLayoutManager(linearLayoutManager);
             rv_tea_list1.setAdapter(teaAdapter);
        }
    }


}
