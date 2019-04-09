package teabar.ph.com.teabar.widgets.videolist.visibility.scroll;




import teabar.ph.com.teabar.widgets.videolist.visibility.items.ListItem;

/**
 * This interface is used by {@link  }.
 * Using this class to get {@link  }
 *
 * @author Wayne
 */
public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
