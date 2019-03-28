package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.TabItemBean;

/**
 *  主界面天气界面
 */

public class WetherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	/**上下文*/
	private Context myContext;
	/**集合*/
	private ArrayList<TabItemBean> listitemList;

	private int selectedPosition = -1;//选中的列表项

	/**
	 * 构造函数
	 */
	public WetherAdapter(Context context, ArrayList<TabItemBean> itemlist) {
		myContext = context;
		listitemList = itemlist;
	}

	/**
	 * 获取总的条目数
	 */
	@Override
	public int getItemCount() {
		return listitemList.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(myContext).inflate(R.layout.item_weather_bottom, parent, false);
		ItemViewHolder itemViewHolder = new ItemViewHolder(view);
		return itemViewHolder;
	}

	/**
	 * 声明grid列表项ViewHolder
	 */
	static class ItemViewHolder extends RecyclerView.ViewHolder {
		public ItemViewHolder(View view) {
			super(view);


			pic = (ImageView) view.findViewById(R.id.iv_item_pic);
			li_weather = view.findViewById(R.id.li_weather);
		}


		ImageView pic;
	 	LinearLayout li_weather;
	}

	/**
	 * 将数据绑定至ViewHolder
	 */
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {

		//判断属于列表项
		if (viewHolder instanceof ItemViewHolder) {
			TabItemBean tabItemBean = listitemList.get(index);
			final ItemViewHolder itemViewHold = ((ItemViewHolder) viewHolder);


			if(selectedPosition == index){
				itemViewHold.pic.setImageResource(R.mipmap.main_weather1);

			}else{
				itemViewHold.pic.setImageResource(R.mipmap.main_weather2);

			}

			//如果设置了回调，则设置点击事件
			if (mOnItemClickLitener != null) {
				itemViewHold.li_weather.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
						mOnItemClickLitener.onItemClick(itemViewHold.li_weather, position);
						setSelected(position);
					}
				});
			}

		}
	}

	/**更改选中的列表项下标值*/
	public void setSelected(int selected) {
		this.selectedPosition = selected;
		notifyDataSetChanged();
	}

	/**
	 * 添加Item--用于动画的展现
	 */
	public void addItem(int position, TabItemBean listitemBean) {
		listitemList.add(position, listitemBean);
		notifyItemInserted(position);
	}

	/**
	 * 删除Item--用于动画的展现
	 */
	public void removeItem(int position) {
		listitemList.remove(position);
		notifyItemRemoved(position);
	}

	/*=====================添加OnItemClickListener回调================================*/
	public interface OnItemClickLitener {
		void onItemClick(View view, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}
}
