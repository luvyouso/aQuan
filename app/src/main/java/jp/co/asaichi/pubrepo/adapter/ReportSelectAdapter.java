package jp.co.asaichi.pubrepo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.databinding.LayoutItemSelectBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;


public class ReportSelectAdapter extends AbstractAdapter {

    public ArrayList<GoogleService> mListData;
    private ListItemInteractionListener mItemInteractionListener;
    private Context mContext;

    public ReportSelectAdapter(Context context, ArrayList<GoogleService> datas) {
        this.mListData = datas;
        this.mContext = context;
    }

    @Override
    public ReportSelectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutItemSelectBinding itemSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_select, viewGroup, false);
        return new ReportSelectViewHolder(itemSelectBinding.getRoot(), itemSelectBinding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportSelectViewHolder viewHolder = (ReportSelectViewHolder) holder;
        GoogleService selectableItem = mListData.get(position);

        viewHolder.getItemSelectBinding().mCheckedTextView.setText(selectableItem.getName());
        viewHolder.getItemSelectBinding().mCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemInteractionListener != null) {
                    mItemInteractionListener.onInteraction(viewHolder.getItemSelectBinding(), viewHolder.getItemSelectBinding().mCheckedTextView, selectableItem, position);
                }
            }
        });
    }


    /**
     * @param listener
     */
    public void setItemInteractionListener(ListItemInteractionListener listener) {
        this.mItemInteractionListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListData.size();

    }


    public static class ReportSelectViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemSelectBinding mItemSelectBinding;

        public ReportSelectViewHolder(View view, LayoutItemSelectBinding itemSelectBinding) {
            super(view);
            this.mItemSelectBinding = itemSelectBinding;
        }

        public LayoutItemSelectBinding getItemSelectBinding() {
            return mItemSelectBinding;
        }
    }


}