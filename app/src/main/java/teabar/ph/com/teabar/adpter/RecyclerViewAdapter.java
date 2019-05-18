package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.device.MakeActivity;
import teabar.ph.com.teabar.pojo.Weather;


/**
 * Created by Administrator on 2018/7/23.
 */

public class RecyclerViewAdapter extends BaseRecyclerAdapter<Weather> {
    Context context;
    public RecyclerViewAdapter(Context mContext, int layoutResId, List<Weather> dataList) {
        super(mContext, layoutResId, dataList);
        this.context = mContext;
    }



    @Override
    protected void bindData(BaseViewHolder holder, Weather data, int position) {
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
        TextView tv_mail_name = holder.getView(R.id.tv_mail_name);
        tv_mail_name.setText(data.getWeek());
        TextView tv_weather_temp = holder.getView(R.id.tv_weather_temp);
        tv_weather_temp.setText(data.getTem());
        TextView tv_weather_sd =holder.getView(R.id.tv_weather_sd);
        tv_weather_sd .setText(data.getHumidity());
        TextView tv_weather_zl = holder.getView(R.id.tv_weather_zl);
        tv_weather_zl.setText(data.getAir_level());
        TextView tv_weather_tq = holder.getView(R.id.tv_weather_tq);
        tv_weather_tq.setText(data.getWea());
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,MakeActivity.class));
            }
        });
    }

    public void  refrashData(List<Weather> weathers){
        this.refrasData(weathers);
    }

}
