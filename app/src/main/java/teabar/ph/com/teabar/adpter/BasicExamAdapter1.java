package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.ToastUtil;

public class BasicExamAdapter1 extends BaseAdapter{
    private Context context;
    private LayoutInflater mInflater;
    private List<String> list;
    private int layout;
    int select[] = new int[3] ;
    int noselect = -1;

    public BasicExamAdapter1(Context context, int layout) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list =  new ArrayList<String>();
        this.layout=layout;

    }

    public List<String> getList(){
        return list;
    }
    List<examOptions> list2 = new ArrayList<>();
    public void setItems(List<examOptions> list){
        list2.clear();
        list2 = list;
        List<String> list1 = new ArrayList<>();
        for (examOptions examOptions : list){
            list1.add(examOptions.getOptionTxt());
        }
        this.list = list1;
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

    public examOptions getMdata(int position){
        return list2.get(position);
    }
    public List<String> getStringList(){
        List<String> examOptions  = new ArrayList<>();
        for (int i=0;i<list2.size();i++){
            if (list2.get(i).isIsselect()){
                examOptions.add(list2.get(i).getOptionNum());
            }
        }
        return examOptions;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    public  void  setSelect(int position[]){
        this.select = position;
        notifyDataSetChanged();
    }
    public  void  setnoSelect(int position){
        this.noselect = position;
        notifyDataSetChanged();
    }
    boolean  Open;
    int chooseNum=0;
    public void refrashNum(){
        chooseNum=0;
    }
    boolean Unit= false;
    public void chooseUnit(boolean unit,int pos){
        if (unit){
            for (int i=0;i<list2.size();i++){
                if (i==list2.size()-1){
                    list2.get(i).setIsselect(true);
                }else {
                    list2.get(i).setIsselect(false);
                }
            }
            chooseNum = 1;
        }else {
            list2.get(list2.size()-1).setIsselect(false);
            list2.get(pos).setIsselect(true);
            chooseNum=0;
        }
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
           holder = new  ViewHolder();
            convertView = mInflater.inflate(
                    layout, null);
            holder.bt_basic_button = convertView.findViewById(R.id.bt_basic_button);
            convertView.setTag(holder);

        } else {

            holder = ( ViewHolder) convertView.getTag();
        }

            final String ee = getItem(position).toString();
            holder.bt_basic_button.setText(ee);
            Open = list2.get(position).isIsselect();
            final boolean isOpen[] = {Open};
            if (!isOpen[0]) {
                holder.bt_basic_button.setBackground(context.getDrawable(R.drawable.answer_button2));
                holder.bt_basic_button.setTextColor(context.getResources().getColor(R.color.login_black));

            } else {
                holder.bt_basic_button.setBackground(context.getResources().getDrawable(R.drawable.login_face));
                holder.bt_basic_button.setTextColor(context.getResources().getColor(R.color.white));
                chooseNum = chooseNum + 1;

            }
            final ViewHolder finalHolder1 = holder;
            holder.bt_basic_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                            if (isOpen[0]) {
                                finalHolder1.bt_basic_button.setBackground(context.getDrawable(R.drawable.answer_button2));
                                finalHolder1.bt_basic_button.setTextColor(context.getResources().getColor(R.color.login_black));
                                isOpen[0] = false;
                                chooseNum = chooseNum - 1;
                                itemClickListerner.onClikner(view, position);
                            } else {
                                if (chooseNum < 3) {
                                    finalHolder1.bt_basic_button.setBackground(context.getResources().getDrawable(R.drawable.login_face));
                                    finalHolder1.bt_basic_button.setTextColor(context.getResources().getColor(R.color.white));
                                    isOpen[0] = true;
                                    chooseNum = chooseNum + 1;
                                    itemClickListerner.onClikner(view, position);
                                } else {
                                    ToastUtil.showShort(context, context.getText(R.string.question_mostchoose).toString());
                                }

                            }



                }
            });

        //        if (clickTemp == position) {
//                      holder.evaluate_tv.setBackgroundResource(R.drawable.answer_button);
//                      holder.evaluate_tv.setTextColor(Color.parseColor("#ffffff"));
//          } else {
//
//                      holder.evaluate_tv.setBackgroundResource(R.drawable.answer_button1);
//                      holder.evaluate_tv.setTextColor(Color.parseColor("#101010"));
//          }
        return convertView;
    }
    public void SetOnclickLister(OnItemClickListerner itemClickListerner){
        this.itemClickListerner = itemClickListerner;
    }

    OnItemClickListerner itemClickListerner;
    public interface OnItemClickListerner {
        void onClikner(View view, int position);
    }
    private final class ViewHolder {
        private TextView bt_basic_button;
    }

    private int clickTemp = -1;
    public void setSelection(int position){
        clickTemp = position;
    }
}
