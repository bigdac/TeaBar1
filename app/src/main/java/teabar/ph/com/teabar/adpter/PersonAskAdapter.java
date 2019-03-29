package teabar.ph.com.teabar.adpter;

import android.app.MediaRouteButton;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.util.ToastUtil;

public class PersonAskAdapter extends RecyclerView.Adapter<PersonAskAdapter.MyviewHolder> {

    private List<String> mData;
    private Context context;
    private EqupmentInformAdapter.OnItemClickListener onItemClickListener;

    public PersonAskAdapter(Context context , List<String> list ) {
        this.context = context;
        this.mData = list;
        initData();
    }
    TextView textView;
    int i = 0 ;
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       MyviewHolder holder = new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_personask,viewGroup,false));



       initAutoLL();
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
        ImageView iv_talk_pic;
        TextView tv_talk_name,tv_talk_time,tv_message;
        public MyviewHolder(View itemView){
            super(itemView);
            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }
    }

    //    最外层的竖直线性布局
    private LinearLayout ll_parent;

    //    绘制自动换行的线性布局
    private void initAutoLL() {
//        每一行的布局，初始化第一行布局
        LinearLayout rowLL = new LinearLayout(context);
        LinearLayout.LayoutParams rowLP =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        float rowMargin = dipToPx(10);
        rowLP.setMargins(0, (int) rowMargin, 0, 0);
        rowLL.setLayoutParams(rowLP);
        boolean isNewLayout = false;
        float maxWidth = getScreenWidth() - dipToPx(30);
//        剩下的宽度
        float elseWidth = maxWidth;
        LinearLayout.LayoutParams textViewLP =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLP.setMargins((int) dipToPx(8), 0, 0, 0);
        for ( i = 0; i < datas.size(); i++) {
//            若当前为新起的一行，先添加旧的那行
//            然后重新创建布局对象，设置参数，将isNewLayout判断重置为false
            if (isNewLayout) {
                ll_parent.addView(rowLL);
                rowLL = new LinearLayout(context);
                rowLL.setLayoutParams(rowLP);
                isNewLayout = false;
            }

//            计算是否需要换行
           TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ask, null);
            textView.setText(datas.get(i));
            textView.measure(0, 0);
//            若是一整行都放不下这个文本框，添加旧的那行，新起一行添加这个文本框
            if (maxWidth < textView.getMeasuredWidth()) {
                ll_parent.addView(rowLL);
                rowLL = new LinearLayout(context);
                rowLL.setLayoutParams(rowLP);
                rowLL.addView(textView);
                isNewLayout = true;
                continue;
            }
//            若是剩下的宽度小于文本框的宽度（放不下了）
//            添加旧的那行，新起一行，但是i要-1，因为当前的文本框还未添加
            if (elseWidth < textView.getMeasuredWidth()) {
                isNewLayout = true;
                i--;
//                重置剩余宽度
                elseWidth = maxWidth;
                continue;
            } else {
//                剩余宽度减去文本框的宽度+间隔=新的剩余宽度
                elseWidth -= textView.getMeasuredWidth() + dipToPx(8);
                if (rowLL.getChildCount() == 0) {
                    rowLL.addView(textView);
                } else {
                    textView.setLayoutParams(textViewLP);
                    rowLL.addView(textView);
                }
            }
        }
//        添加最后一行，但要防止重复添加
        ll_parent.removeView(rowLL);
        ll_parent.addView(rowLL);
    }

    //    dp转px
    private float dipToPx(int dipValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                context.getResources().getDisplayMetrics());
    }

    //  获得评论宽度
    private float getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    //    数据
    ArrayList<String> datas = new ArrayList<>();

    //    初始化数据
    private void initData() {
        datas.add("作 家");
        datas.add("段 子 手");
        datas.add("软 文 作 者");
        datas.add("摄 影 爱 好 者");
        datas.add("画 家");
        datas.add("哦 我还很喜欢音乐");
        datas.add("还 有 其 他 七 七 八 八 的 我 就 不 说 了");
        datas.add("老 师");
    }


}
