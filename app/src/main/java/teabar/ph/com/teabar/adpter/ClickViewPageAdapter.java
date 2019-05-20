package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseFragment;


public class ClickViewPageAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;
    final int PAGE_COUNT=5;
    private Context context;
    int type1;
    public ClickViewPageAdapter(FragmentManager fm, List<BaseFragment> fragments, Context context,int type1) {
        super(fm);
        this.fragments = fragments;
        this.context=context;
        this.type1 = type1;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public View getCustomView(int position){
        View view= LayoutInflater.from(context).inflate(R.layout.item_memu,null);
        ImageView iv= (ImageView) view.findViewById(R.id.tab_iv);
        TextView tv= (TextView) view.findViewById(R.id.tab_tv);
        if ( type1== 0) {
        switch (position){
            case 0:
                //drawable代码在文章最后贴出
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon1));
                tv.setText(R.string.menu_tea);
                break;
            case 1:
                 iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon2));
                tv.setText(R.string.menu_equ);
                break;
            case 2:
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon3));
                tv.setText(R.string.menu_sq);
                break;
            case 3:
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon4));
                tv.setText(R.string.menu_sc);
                break;
            case 4:
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon5));
                tv.setText(R.string.menu_wd);
                break;
            }
        }else {
            switch (position){
                case 0:
                    //drawable代码在文章最后贴出
                    iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon1));
                    tv.setText(R.string.menu_tea);
                    break;
                case 1:
                    iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon2));
                    tv.setText(R.string.menu_equ);
                    break;

                case 2:
                    iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon4));
                    tv.setText(R.string.menu_sc);
                    break;
                case 3:
                    iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon5));
                    tv.setText(R.string.menu_wd);
                    break;
            }
        }
        return view;
    }


}
