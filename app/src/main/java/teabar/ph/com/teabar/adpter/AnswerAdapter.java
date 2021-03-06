package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;

//标签类相关适配器
public class AnswerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<String> list;
    private int layout;

    public AnswerAdapter(Context context, int layout) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list =  new ArrayList<String>();
        this.layout=layout;

    }

    public List<String> getList(){
        return list;
    }

    public void setItems(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    layout, null);
            holder.evaluate_tv = (TextView)convertView.findViewById(R.id.tv_search);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final String ee = getItem(position).toString();
        holder.evaluate_tv.setText(ee);

        if (clickTemp == position) {
                      holder.evaluate_tv.setBackgroundResource(R.drawable.answer_button);
                      holder.evaluate_tv.setTextColor(Color.parseColor("#ffffff"));
          } else {
                      holder.evaluate_tv.setBackgroundResource(R.drawable.answer_button1);
                      holder.evaluate_tv.setTextColor(Color.parseColor("#101010"));
          }
        return convertView;
    }

    private final class ViewHolder {
        private TextView evaluate_tv;
    }

          private int clickTemp = 0;
      public void setSelection(int position){
          clickTemp = position;
          notifyDataSetChanged();
      }
}