package teabar.ph.com.teabar.adpter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.ImagePagerActivity;
import teabar.ph.com.teabar.activity.social.AllCommentActivity;
import teabar.ph.com.teabar.bean.CircleItem;
import teabar.ph.com.teabar.bean.CommentConfig;
import teabar.ph.com.teabar.bean.CommentItem;
import teabar.ph.com.teabar.bean.PhotoInfo;
import teabar.ph.com.teabar.mvp.presenter.CirclePresenter;
import teabar.ph.com.teabar.util.GlideCircleTransform;
import teabar.ph.com.teabar.util.UrlUtils;
import teabar.ph.com.teabar.widgets.CircleVideoView;
import teabar.ph.com.teabar.widgets.ExpandTextView;
import teabar.ph.com.teabar.widgets.MultiImageView;


/**
 * Created by yiwei on 16/5/17.
 */
public class CircleAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

    public final static int TYPE_HEAD = 0;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    int curPlayIndex=-1;
    public boolean isOpen =false;
    private CirclePresenter  presenter;
    private Context context;
    private  OnItemClickListener onItemClickListener;
    public void setCirclePresenter(CirclePresenter presenter){
        this.presenter = presenter;
    }
    List<CircleItem> mDatas = new ArrayList<>();
    LinearLayoutManager linearLayout ;
    public CircleAdapter2(Context context , LinearLayoutManager linearLayout){
        this.context = context;
//        this.mDatas = list;
        this.linearLayout = linearLayout;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEAD;
        }
        int itemType = 0;
         itemType = CircleViewHolder.TYPE_IMAGE;
        return itemType;
    }


    public void setData(List mDatas) {
//        this.mDatas = mDatas;
//        notifyDataSetChanged();
        notifyData(mDatas);
    }
    public void setData1(List mDatas) {
        this.mDatas = mDatas;
//        notifyDataSetChanged();

    }
    public void notifyData(List<CircleItem> poiItemList) {
        if (poiItemList != null) {
            int previousSize = mDatas.size();
             mDatas.clear();
            notifyItemRangeRemoved(0, previousSize);
            mDatas.addAll(poiItemList);
            notifyItemRangeInserted(0, poiItemList.size());
            if (previousSize==0){
                linearLayout.scrollToPosition(0);
            }else {
                if (poiItemList.size()%10==0){
                    linearLayout.scrollToPosition(poiItemList.size()-10);
                }else {
                    linearLayout.scrollToPosition(poiItemList.size()-((poiItemList.size()%10)+1));
                }

            }

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == TYPE_HEAD){
            View headView = LayoutInflater.from(context).inflate(R.layout.head_circle, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_social1, parent, false);

            if(viewType == CircleViewHolder.TYPE_URL){
                viewHolder = new URLViewHolder(view);
            }else if(viewType == CircleViewHolder.TYPE_IMAGE){
                viewHolder = new ImageViewHolder(view);
            }else if(viewType == CircleViewHolder.TYPE_VIDEO){
                viewHolder = new VideoViewHolder(view);
            }
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holders,final int position) {


        if(getItemViewType(position)==TYPE_HEAD){
            //HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        }else{

            final int circlePosition = position - HEADVIEW_SIZE;
            final CircleViewHolder holder = (CircleViewHolder) holders;
            final CircleItem circleItem = (CircleItem) mDatas.get(circlePosition);
            final String circleId = circleItem.getId();
            String name = circleItem.getUser().getName();
            String headImg = circleItem.getUser().getHeadUrl();
            final String content = circleItem.getContent();
            String createTime = circleItem.getCreateTime();
            final boolean isOpen[] ={circleItem.isOpen()};
            final List<CommentItem> commentsDatas = circleItem.getComments();
            final int  ThumbsUp[]= {circleItem.getThumbsUp()};
            int  CommentNum = circleItem.getCommentNum();
            boolean hasComment = circleItem.hasComment();

            Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(context)).into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(createTime);
            holder.tv_social_num.setText(ThumbsUp[0]+"");
            holder.tv_social_talk.setText(CommentNum+"");
            if (CommentNum==0){
                holder.allMess.setVisibility(View.GONE);
                holder.iv_all_mes.setVisibility(View.GONE);
            }else {
                holder.allMess.setVisibility(View.VISIBLE);
                holder.iv_all_mes.setVisibility(View.VISIBLE);
            }

            holder.allMess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,AllCommentActivity.class);
                    intent.putExtra("contentId",circleId);
                    context.startActivity(intent);
                }
            });

            if (!isOpen[0]){
                holder.iv_social_no.setImageResource(R.mipmap.make_no1);
            }else {
                holder.iv_social_no.setImageResource(R.mipmap.make_yes1);

            }
            if(!TextUtils.isEmpty(content)){
                holder.contentTv.setExpand(circleItem.isExpand());
                holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                    @Override
                    public void statusChange(boolean isExpand) {
                        circleItem.setExpand(isExpand);
                    }
                });

                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);


            holder.iv_social_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);

                            if (isOpen[0]){
                                holder.iv_social_no.setImageResource(R.mipmap.make_no1);
                                isOpen[0]=false;
                                if (ThumbsUp[0]==0){
                                    holder.tv_social_num.setText(ThumbsUp[0]+"");
                                }else {
                                    holder.tv_social_num.setText(ThumbsUp[0]-1+"");
                                    ThumbsUp[0] = ThumbsUp[0]-1;
                                }
                                Log.e("DDDD", "onClick: -->"+circleItem.isOpen() );
                                circleItem.setOpen(false);
                                mDatas.set(circlePosition,circleItem);
                            }else {
                                holder.iv_social_no.setImageResource(R.mipmap.make_yes1);
                                isOpen[0]=true;
                                circleItem.setOpen(true);
                                holder.tv_social_num.setText(ThumbsUp[0]+1+"");
                                ThumbsUp[0] = ThumbsUp[0]+1;
                                Log.e("DDDD", "onClick: -->"+circleItem.isOpen() );
                                mDatas.set(circlePosition,circleItem);
                            }
                        }



            });


                if(hasComment){//处理评论列表
                    holder.commentList.setDatas(commentsDatas);
                    holder.commentList.setVisibility(View.VISIBLE);
                    holder.digCommentBody.setVisibility(View.VISIBLE);
                }else {
                    holder.commentList.setVisibility(View.GONE);
                    holder.digCommentBody.setVisibility(View.GONE);
                }
            holder.snsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
//                    snsPopupWindow.showPopupWindow(view);
                    if(presenter != null){
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = circlePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if(holder instanceof URLViewHolder){
                        String linkImg = circleItem.getLinkImg();
                        String linkTitle = circleItem.getLinkTitle();
                        Glide.with(context).load(linkImg).into(((URLViewHolder)holder).urlImageIv);
                        ((URLViewHolder)holder).urlContentTv.setText(linkTitle);
                        ((URLViewHolder)holder).urlBody.setVisibility(View.VISIBLE);
//                        ((URLViewHolder)holder).urlTipTv.setVisibility(View.VISIBLE);
                    }

                    break;
                case CircleViewHolder.TYPE_IMAGE:// 处理图片
                    if(holder instanceof ImageViewHolder){
                        final List<PhotoInfo> photos = circleItem.getPhotos();
                        if (photos != null && photos.size() > 0) {
                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.VISIBLE);
                            ((ImageViewHolder)holder).multiImageView.setList(photos);
                            ((ImageViewHolder)holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //imagesize是作为loading时的图片size
                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                    List<String> photoUrls = new ArrayList<String>();
                                    for(PhotoInfo photoInfo : photos){
                                        photoUrls.add(photoInfo.url);
                                    }
                                    ImagePagerActivity.startImagePagerActivity(( context), photoUrls, position, imageSize);


                                }
                            });
                        } else {
                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.GONE);
                        }
                    }

                    break;
                case CircleViewHolder.TYPE_VIDEO:
                    if(holder instanceof VideoViewHolder){
                        ((VideoViewHolder)holder).videoView.setVideoUrl(circleItem.getVideoUrl());
                        ((VideoViewHolder)holder).videoView.setVideoImgUrl(circleItem.getVideoImgUrl());//视频封面图片
                        ((VideoViewHolder)holder).videoView.setPostion(position);
                        ((VideoViewHolder)holder).videoView.setOnPlayClickListener(new CircleVideoView.OnPlayClickListener() {
                            @Override
                            public void onPlayClick(int pos) {
                                curPlayIndex = pos;
                            }
                        });
                    }

                    break;
                default:
                    break;
            }
        }
    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

    }
    @Override
    public int getItemCount() {
        return mDatas.size()+1;//有head需要加1
    }


    public List<CircleItem> getDates1(){
        return mDatas;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }



}
