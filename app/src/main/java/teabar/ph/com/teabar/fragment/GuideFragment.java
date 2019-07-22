package teabar.ph.com.teabar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.login.LoginActivity;

public class GuideFragment extends Fragment {
    View view;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guild, container, false);
        ImageView imageView =  view.findViewById(R.id.iv_guide_pic);
        ImageView iv_guide_text = view.findViewById(R.id.iv_guide_text);
        // 步骤2:获取某一值
        if(type==0){
          imageView .setImageResource( R.mipmap.guide1);
          iv_guide_text.setImageResource(R.mipmap.w1);
        }
        else if(type==1) {
            imageView .setImageResource( R.mipmap.guide2);
            iv_guide_text.setImageResource(R.mipmap.h1);
        }
        else if(type==2) {
            imageView .setImageResource( R.mipmap.guide3);
            view.findViewById(R.id.tv_guild_button).setVisibility(View.VISIBLE);
            iv_guide_text.setImageResource(R.mipmap.l1);
            view.findViewById(R.id.tv_guild_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }

        return view;
    }
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
