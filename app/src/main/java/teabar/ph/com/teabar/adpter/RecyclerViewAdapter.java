package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;


/**
 * Created by Administrator on 2018/7/23.
 */

public class RecyclerViewAdapter extends BaseRecyclerAdapter<String> {

    public RecyclerViewAdapter(Context mContext, int layoutResId, List<String> dataList) {
        super(mContext, layoutResId, dataList);
    }

    @Override
    protected void bindData(BaseViewHolder holder, String data, int position) {
//        TextView tv_name=holder.getView(R.id.tv_name);
//        tv_name.setText(data);
    }
}
