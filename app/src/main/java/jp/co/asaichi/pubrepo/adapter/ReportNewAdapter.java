package jp.co.asaichi.pubrepo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.LayoutItemNewBinding;
import jp.co.asaichi.pubrepo.model.NewModel;
import jp.co.asaichi.pubrepo.utils.Utils;


public class ReportNewAdapter extends AbstractAdapter {

    public ArrayList<NewModel> mListData;
    private ListItemInteractionListener mItemInteractionListener;
    private Context mContext;

    public ReportNewAdapter(Context context, ArrayList<NewModel> datas) {
        this.mListData = datas;
        this.mContext = context;
    }

    @Override
    public ReportNewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutItemNewBinding itemNewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_new, viewGroup, false);
        return new ReportNewViewHolder(itemNewBinding.getRoot(), itemNewBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportNewViewHolder viewHolder = (ReportNewViewHolder) holder;
        NewModel item = mListData.get(position);
        //title
        viewHolder.getItemNewBinding().mTextViewTitle.setText(item.getTitle());
        //date
        viewHolder.getItemNewBinding().mTextViewDate.setText(Utils.dateFromMillisecond(Constants.DEFAULT_DATE_FORMAT, item.getPublish_date()));
        //content
        viewHolder.getItemNewBinding().mTextViewContent.setText(item.getContent());

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


    public static class ReportNewViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemNewBinding mItemNewBinding;

        public ReportNewViewHolder(View view, LayoutItemNewBinding itemNewBinding) {
            super(view);
            this.mItemNewBinding = itemNewBinding;
        }

        public LayoutItemNewBinding getItemNewBinding() {
            return mItemNewBinding;
        }
    }


}