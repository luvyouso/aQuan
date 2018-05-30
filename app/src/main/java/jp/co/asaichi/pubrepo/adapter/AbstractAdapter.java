/**
 * AbstractAdapter
 * <p>
 * This abstract class using as a base for any adapter that want to use RecyclerView
 *
 * @author Briswell - Do Anh Tuan
 * @version 1.0  2016-08-01
 */

package jp.co.asaichi.pubrepo.adapter;


import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class AbstractAdapter extends RecyclerView.Adapter {

    public interface ListItemInteractionListener {

        void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position);
    }
}
