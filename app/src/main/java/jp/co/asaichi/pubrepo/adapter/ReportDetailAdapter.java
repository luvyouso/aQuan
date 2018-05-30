package jp.co.asaichi.pubrepo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.databinding.LayoutFooterBinding;
import jp.co.asaichi.pubrepo.databinding.LayoutItemReportDetailBinding;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ReportDetailAdapter extends AbstractAdapter {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public ArrayList<String> mListData;
    private AbstractAdapter.ListItemInteractionListener mItemInteractionListener;
    private Context mContext;
    private boolean isEdit;

    public ReportDetailAdapter(Context context, ArrayList<String> datas, boolean isEdit) {
        this.mListData = datas;
        this.mContext = context;
        this.isEdit = isEdit;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_FOOTER) {
            LayoutFooterBinding iteFooterBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_footer, viewGroup, false);
            return new ReportFooterViewHolder(iteFooterBinding.getRoot(), iteFooterBinding);

        } else if (viewType == TYPE_ITEM) {
            LayoutItemReportDetailBinding itemReportDetailBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_report_detail, viewGroup, false);
            return new ReportDetailViewHolder(itemReportDetailBinding.getRoot(), itemReportDetailBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReportDetailViewHolder) {
            ReportDetailViewHolder viewHolder = (ReportDetailViewHolder) holder;
            Glide.with(mContext)
                    .load(mListData.get(position))
                    .apply(RequestOptions
                            .centerCropTransform()
                            .error(R.mipmap.ic_launcher))
                    .into(viewHolder.getReportDetailBinding().mImageViewReport);
            viewHolder.getReportDetailBinding().mImageViewReport.setOnClickListener(v -> {
                if (mItemInteractionListener != null) {
                    mItemInteractionListener.onInteraction(viewHolder.getReportDetailBinding(), viewHolder.getReportDetailBinding().mImageViewReport, mListData.get(position), position);
                }
            });
            //remove image
            if (isEdit) {
                viewHolder.getReportDetailBinding().mImageViewRemove.setVisibility(View.VISIBLE);
                viewHolder.getReportDetailBinding().mImageViewRemove.setOnClickListener(v -> {
                    if (mItemInteractionListener != null) {
                        mItemInteractionListener.onInteraction(viewHolder.getReportDetailBinding(), viewHolder.getReportDetailBinding().mImageViewRemove, mListData.get(position), position);
                    }
                });
            } else {
                viewHolder.getReportDetailBinding().mImageViewRemove.setVisibility(View.GONE);
            }
        } else if (holder instanceof ReportFooterViewHolder) {
            ReportFooterViewHolder footerViewHolder = (ReportFooterViewHolder) holder;
            Glide.with(mContext)
                    .load(R.drawable.icon_select_image)
                    .apply(RequestOptions
                            .centerInsideTransform()
                            .error(R.mipmap.ic_launcher))
                    .into(footerViewHolder.getLayoutFooterBinding().mImageViewReportFooter);
            footerViewHolder.getLayoutFooterBinding().mImageViewReportFooter.setOnClickListener(v -> {
                if (mItemInteractionListener != null) {
                    mItemInteractionListener.onInteraction(footerViewHolder.getLayoutFooterBinding(), footerViewHolder.getLayoutFooterBinding().mImageViewReportFooter, null, position);
                }
            });
        }


    }


    /**
     * @param listener
     */
    public void setItemInteractionListener(ListItemInteractionListener listener) {
        this.mItemInteractionListener = listener;
    }

    private boolean isPositionFooter(int position) {
        return position == mListData.size();
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }


    @Override
    public int getItemCount() {
        if (isEdit)
            return mListData.size() + 1;
        else
            return mListData.size();

    }


    public static class ReportDetailViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemReportDetailBinding mReportDetailBinding;

        public ReportDetailViewHolder(View view, LayoutItemReportDetailBinding itemReportDetailBinding) {
            super(view);
            this.mReportDetailBinding = itemReportDetailBinding;
        }

        public LayoutItemReportDetailBinding getReportDetailBinding() {
            return mReportDetailBinding;
        }
    }

    public static class ReportFooterViewHolder extends RecyclerView.ViewHolder {
        private LayoutFooterBinding mLayoutFooterBinding;

        public ReportFooterViewHolder(View view, LayoutFooterBinding itemFooterBinding) {
            super(view);
            this.mLayoutFooterBinding = itemFooterBinding;
        }

        public LayoutFooterBinding getLayoutFooterBinding() {
            return mLayoutFooterBinding;
        }
    }


}