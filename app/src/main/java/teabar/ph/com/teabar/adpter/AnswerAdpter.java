package teabar.ph.com.teabar.adpter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.pojo.Question;
import teabar.ph.com.teabar.pojo.examOptions;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.util.Utils;


public class AnswerAdpter extends RecyclerView.Adapter< AnswerAdpter.MyViewHolder> {
    Context context;
    List<examOptions> mData;
    String multiple,selectnum;
    int number;
    public AnswerAdpter(Context context, List<examOptions> list ,int number ) {
        this.context = context;
        this.mData = list;
        this.number = number;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_answer,viewGroup,false));
        return holder;
    }
    int chooseNum=0;
    boolean Open = false;
    boolean isUnique = false;
    List<String> answerList = new ArrayList<>();
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

        myViewHolder.tv_search.setText(mData.get(position).getOptionTxt());
        Open = mData.get(position).isIsselect();
        final boolean isOpen[] ={Open};
        if (isOpen[0]){
            myViewHolder.iv_question_choose.setImageResource(R.mipmap.set_xz1);

        }else {
            myViewHolder.iv_question_choose.setImageResource(R.mipmap.set_xz2);

        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(multiple)|| "0".equals(multiple)){
                    answerList.clear();
                    String answer = mData.get(position).getOptionNum();
                    answerList.add(answer);
                    List<examOptions> optionsList = new ArrayList<>();
                    for (int i =0;i<mData.size();i++){
                        optionsList.add(mData.get(i));
                        if (i==position){
                            optionsList.get(i).setIsselect(true);
                        }else {
                            optionsList.get(i).setIsselect(false);
                        }
                    }
                    updatemData(optionsList);

                }else {
                    if ("1".equals(mData.get(position).getUnique1())){
                        List<examOptions> optionsList = new ArrayList<>();
                        answerList.clear();
                        if (!isUnique) {
                            String answer = mData.get(position).getOptionNum();
                            answerList.add(answer);
                            for (int i = 0; i < mData.size(); i++) {
                                optionsList.add(mData.get(i));
                                if (i == position) {
                                    optionsList.get(i).setIsselect(true);
                                } else {
                                    optionsList.get(i).setIsselect(false);
                                }
                            }
                            isUnique = true;

                        }else {
                            for (int i = 0; i < mData.size(); i++) {
                                optionsList.add(mData.get(i));
                                if (i == position) {
                                    optionsList.get(i).setIsselect(false);
                                }
                            }
                            isUnique = false;
                        }
                        chooseNum = 0;
                        updatemData(optionsList);
                    }else {
                    int maxChoose = Integer.valueOf(selectnum);
                    if (!isUnique){
                         if (isOpen[0]){
                        myViewHolder.iv_question_choose.setImageResource(R.mipmap.set_xz2);
                        isOpen[0]=false;
                        chooseNum = chooseNum-1;
                        String answer = mData.get(position).getOptionNum();
                        answerList.remove(answer);
                         }else {
                        if (chooseNum<maxChoose){
                            isOpen[0]=true;
                            chooseNum = chooseNum+1;
                            myViewHolder.iv_question_choose.setImageResource(R.mipmap.set_xz1);
                            String answer = mData.get(position).getOptionNum();
                            answerList.add(answer);
                        }else {
                            ToastUtil.showShort(context,"超过選擇个数");
                        }

                        }
                    }else {
                        ToastUtil.showShort(context,"请先取消冲突选项");
                    }
                    }
                }

                itemClickListerner.onClikner(view,position,Utils.listToString(answerList),mData.get(position).getExamId());
            }
        });

    }

    public void cleanAnswerList(){
        answerList.clear();
        chooseNum =0;
        isUnique = false;
    }
    public void SetOnclickLister( OnItemClickListerner itemClickListerner){
        this.itemClickListerner = itemClickListerner;
    }

    OnItemClickListerner itemClickListerner;
    public interface OnItemClickListerner {
        void onClikner(View view,int position,String answer,int examId);
    }
    public void  updatemData(List<examOptions> mData ){
        this.mData= mData;
        notifyDataSetChanged();
    }
    public void  setmData(List<examOptions> mData,String multiple,String selectnum){
        this.mData= mData;
        this.multiple = multiple;
        this.selectnum = selectnum;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_question_choose;
        TextView tv_search ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_question_choose = itemView.findViewById(R.id.iv_question_choose);
            tv_search = itemView.findViewById(R.id.tv_search);
        }
    }
}
