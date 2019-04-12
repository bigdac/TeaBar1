package teabar.ph.com.teabar.adpter.viewholder;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.widgets.CommentListView;
import teabar.ph.com.teabar.widgets.ExpandTextView;
import teabar.ph.com.teabar.widgets.PraiseListView;
import teabar.ph.com.teabar.widgets.SnsPopupWindow;
import teabar.ph.com.teabar.widgets.videolist.model.VideoLoadMvpView;
import teabar.ph.com.teabar.widgets.videolist.widget.TextureVideoView;

/**
 * Created by yiw on 2016/8/16.
 */
public abstract class CircleViewHolder extends RecyclerView.ViewHolder implements VideoLoadMvpView {

    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    public int viewType;

    public ImageView headIv,iv_social_no;
    public TextView nameTv;
    public TextView urlTipTv,tv_social_num,tv_social_talk;
    /** 动态的内容 */
    public ExpandTextView contentTv;
    public TextView timeTv;

    public ImageView snsBtn;


    public LinearLayout digCommentBody;//


    /** 评论列表 */
    public CommentListView commentList;
    // ===========================
    public SnsPopupWindow snsPopupWindow;

    public CircleViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);

        initSubView(viewType, viewStub);

        headIv = (ImageView) itemView.findViewById(R.id.headIv);
        nameTv = (TextView) itemView.findViewById(R.id.nameTv);

        iv_social_no = itemView.findViewById(R.id.iv_social_no);

        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        urlTipTv = (TextView) itemView.findViewById(R.id.urlTipTv);
        timeTv = (TextView) itemView.findViewById(R.id.timeTv);

        snsBtn = (ImageView) itemView.findViewById(R.id.snsBtn);
        tv_social_num = itemView.findViewById(R.id.tv_social_num);
        tv_social_talk = itemView.findViewById(R.id.tv_social_talk);

        digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
        commentList = (CommentListView)itemView.findViewById(R.id.commentList);

        snsPopupWindow = new SnsPopupWindow(itemView.getContext());

    }

    public abstract void initSubView(int viewType, ViewStub viewStub);

    @Override
    public TextureVideoView getVideoView() {
        return null;
    }

    @Override
    public void videoBeginning() {

    }

    @Override
    public void videoStopped() {

    }

    @Override
    public void videoPrepared(MediaPlayer player) {

    }

    @Override
    public void videoResourceReady(String videoPath) {

    }
}
