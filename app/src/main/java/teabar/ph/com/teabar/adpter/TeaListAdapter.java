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
import teabar.ph.com.teabar.pojo.Tea;

/**
 * 茶列表適配器
 *
 */
public class TeaListAdapter extends RecyclerView.Adapter<TeaListAdapter.MyViewHolder> {

    private List<String> mDatas = new ArrayList<>();
    private List<Tea> teaList1 = new ArrayList<>();//草本口味茶列表
    private List<Tea> teaList2 = new ArrayList<>();//水果口味茶列表
    private List<Tea> teaList3 = new ArrayList<>();//功能茶列表
    private Context mContext;
    TeaAdapter teaAdapter1,teaAdapter2,teaAdapter3;//分別對應草本口味，水果口味，功能茶適配器
    public TeaListAdapter(Context context, List<String> list,List<Tea> tea1,List<Tea> tea2,List<Tea> tea3) {
        this.mContext = context;
        this.mDatas = list;
        teaAdapter1 = new TeaAdapter(context,tea1);
        teaAdapter2 = new TeaAdapter(context,tea2);
        teaAdapter3 = new TeaAdapter(context,tea3);
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
            case 0:
                teaAdapter1.update(teaList1);
                myViewHolder.rv_tea_list1.setAdapter(teaAdapter1);
                break;
            case 1:
                myViewHolder.tv_tea_name.setText(R.string.tea_kouwei_sg);
                teaAdapter2.update(teaList2);
                myViewHolder.rv_tea_list1.setAdapter(teaAdapter2);
                break;
            case 2:
                myViewHolder.tv_tea_name.setText(R.string.tea_kouwei_gn);
                teaAdapter3.update(teaList3);
                myViewHolder.rv_tea_list1.setAdapter(teaAdapter3);
                break;
        }

    }

    public void update(List<Tea> tea1,List<Tea> tea2,List<Tea> tea3){
        this.teaList1 = tea1;
        this.teaList2 = tea2;
        this.teaList3 = tea3;
        notifyDataSetChanged();
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
        }
    }


}
