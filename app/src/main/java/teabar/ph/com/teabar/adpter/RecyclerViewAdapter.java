package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.MakeActivity;


/**
 * Created by Administrator on 2018/7/23.
 */

public class RecyclerViewAdapter extends BaseRecyclerAdapter<String> {
    Context context;
    public RecyclerViewAdapter(Context mContext, int layoutResId, List<String> dataList) {
        super(mContext, layoutResId, dataList);
        this.context = mContext;
    }




    @Override
    protected void bindData(BaseViewHolder holder, String data, int position) {
//        RelativeLayout rl_mail = holder.itemView.findViewById(R.id.rl_mail);
//        TextView iv_mail_pic = holder.getView(R.id.iv_mail_pic);
////        RelativeLayout rl_mail = holder.getView(R.id.rl_mail);
//        iv_mail_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.startActivity(new Intent(context,MakeActivity.class));
//            }
//        });

        Button bt_make = holder.getView(R.id.bt_make);
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,MakeActivity.class));
            }
        });
    }
}
