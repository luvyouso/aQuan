package jp.co.asaichi.pubrepo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.LayoutItemReportHistoryBinding;
import jp.co.asaichi.pubrepo.utils.Utils;


public class ReportHistoryAdapter extends AbstractAdapter {

    public ArrayList<DataSnapshot> mListData;
    private ListItemInteractionListener mItemInteractionListener;
    private Context mContext;

    public ReportHistoryAdapter(Context context, ArrayList<DataSnapshot> datas) {
        this.mListData = datas;
        this.mContext = context;
    }

    @Override
    public ReportHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutItemReportHistoryBinding itemReportHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_report_history, viewGroup, false);
        return new ReportHistoryViewHolder(itemReportHistoryBinding.getRoot(), itemReportHistoryBinding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportHistoryViewHolder viewHolder = (ReportHistoryViewHolder) holder;
        DataSnapshot dataSnapshot = mListData.get(position);
        for (DataSnapshot image : dataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {//TODO logic show
            Glide.with(mContext)
                    .load(image.child(Constants.PARAM_THUMB_URL).getValue())
                    .apply(RequestOptions
                            .centerCropTransform()
                            .error(R.drawable.icon_example))
                    .into(viewHolder.getReportHistoryBinding().mImageViewReportHistory);
            break;
        }
        //date
        viewHolder.getReportHistoryBinding().mTextViewDate.setText(Utils.dateFromMillisecond(Constants.DEFAULT_DATE_FORMAT, (Long) dataSnapshot.child(Constants.PARAM_CREATED_TIMESTAMP).getValue()));
        //address
//        viewHolder.getReportHistoryBinding().mTextViewAddress.setText(dataSnapshot.child(Constants.PARAM_ADDRESS).getValue().toString());
        //title
        viewHolder.getReportHistoryBinding().mTextViewTitle.setText(dataSnapshot.child(Constants.PARAM_TITLE).getValue().toString());
        //status
        if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 2) {
            viewHolder.getReportHistoryBinding().mTextViewStatus.setText(Constants.STATUS_2_WAITING);
            Glide.with(mContext)
                    .load(R.drawable.icon_noti_red)
                    .apply(RequestOptions
                            .centerInsideTransform()
                            .error(R.drawable.icon_example))
                    .into(viewHolder.getReportHistoryBinding().mImageViewStatus);

        } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 3) {
            viewHolder.getReportHistoryBinding().mTextViewStatus.setText(Constants.STATUS_3_DURING);
            Glide.with(mContext)
                    .load(R.drawable.icon_noti_yellow)
                    .apply(RequestOptions
                            .centerInsideTransform()
                            .error(R.drawable.icon_example))
                    .into(viewHolder.getReportHistoryBinding().mImageViewStatus);

        } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 4) {
            viewHolder.getReportHistoryBinding().mTextViewStatus.setText(Constants.STATUS_4_CORRESPONDING);
            Glide.with(mContext)
                    .load(R.drawable.icon_noti_green)
                    .apply(RequestOptions
                            .centerInsideTransform()
                            .error(R.drawable.icon_example))
                    .into(viewHolder.getReportHistoryBinding().mImageViewStatus);
        } else {
            viewHolder.getReportHistoryBinding().mTextViewStatus.setText(Constants.STATUS_1_UNRECOGNIZED);
            Glide.with(mContext)
                    .load(R.drawable.icon_noti_gray)
                    .apply(RequestOptions
                            .centerInsideTransform()
                            .error(R.drawable.icon_example))
                    .into(viewHolder.getReportHistoryBinding().mImageViewStatus);
        }
        //type
        int type = Integer.parseInt(dataSnapshot.child(Constants.PARAM_TYPE).getValue().toString());
        switch (type) {
            case 1:
                Glide.with(mContext)
                        .load(R.drawable.icon_type_repair)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .error(R.drawable.icon_example))
                        .into(viewHolder.getReportHistoryBinding().mImageViewType);
                viewHolder.getReportHistoryBinding().mTextViewType.setText(mContext.getText(R.string.repair));
                break;
            case 2:
                Glide.with(mContext)
                        .load(R.drawable.icon_type_withdraw)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .error(R.drawable.icon_example))
                        .into(viewHolder.getReportHistoryBinding().mImageViewType);
                viewHolder.getReportHistoryBinding().mTextViewType.setText(mContext.getText(R.string.remove));
                break;
            case 3:
                Glide.with(mContext)
                        .load(R.drawable.icon_type_prune)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .error(R.drawable.icon_example))
                        .into(viewHolder.getReportHistoryBinding().mImageViewType);
                viewHolder.getReportHistoryBinding().mTextViewType.setText(mContext.getText(R.string.pruning));
                break;
            case 4:
                Glide.with(mContext)
                        .load(R.drawable.icon_type_others)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .error(R.drawable.icon_example))
                        .into(viewHolder.getReportHistoryBinding().mImageViewType);
                viewHolder.getReportHistoryBinding().mTextViewType.setText(mContext.getText(R.string.others));
                break;
            default:
                break;
        }
//        Long status = (Long) dataSnapshot.child(Constants.PARAM_STATUS).getValue();

//        if (status == 2) {
//            viewHolder.getReportHistoryBinding().mImageViewType.setColorFilter(ContextCompat.getColor(mContext, R.color.red_validate));
//        } else if (status == 3) {
//            viewHolder.getReportHistoryBinding().mImageViewType.setColorFilter(ContextCompat.getColor(mContext, R.color.yellow));
//        } else {
//            viewHolder.getReportHistoryBinding().mImageViewType.setColorFilter(ContextCompat.getColor(mContext, R.color.green_haze));
//        }

        //like
        if (dataSnapshot.child(Constants.PARAM_LIKES).getValue() != null) {
            HashMap<String, Boolean> likes = (HashMap<String, Boolean>) dataSnapshot.child(Constants.PARAM_LIKES).getValue();
            int countLike = 0;
            for (String key : likes.keySet()) {
                if (likes.get(key)) {
                    countLike++;
                }
            }
            viewHolder.getReportHistoryBinding().mTextViewLike.setText(countLike + "");
        } else {
            viewHolder.getReportHistoryBinding().mTextViewLike.setText("0");
        }

        holder.itemView.setOnClickListener(v -> {
            if (mItemInteractionListener != null) {
                mItemInteractionListener.onInteraction(((ReportHistoryViewHolder) holder).getReportHistoryBinding(), holder.itemView, dataSnapshot, position);
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


    public static class ReportHistoryViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemReportHistoryBinding mReportHistoryBinding;

        public ReportHistoryViewHolder(View view, LayoutItemReportHistoryBinding itemReportHistoryBinding) {
            super(view);
            this.mReportHistoryBinding = itemReportHistoryBinding;
        }

        public LayoutItemReportHistoryBinding getReportHistoryBinding() {
            return mReportHistoryBinding;
        }
    }


}