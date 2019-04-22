package teabar.ph.com.teabar.util.chat.interfaces;

import android.view.View;
import android.view.ViewGroup;

import teabar.ph.com.teabar.util.chat.data.PageEntity;


public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}
