package jp.co.asaichi.pubrepo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.LayoutItemReportNotificationBinding;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.Utils;


public class ReportNotificationAdapter extends AbstractAdapter {

    public ArrayList<DataSnapshot> mListData;
    private ListItemInteractionListener mItemInteractionListener;
    private Context mContext;
    private FirebaseUtils mFirebaseUtils;

    public ReportNotificationAdapter(Context context, ArrayList<DataSnapshot> datas) {
        this.mListData = datas;
        this.mContext = context;
        mFirebaseUtils = new FirebaseUtils(context);
    }

    @Override
    public ReportNotificationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutItemReportNotificationBinding itemReportNotifcationBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_report_notification, viewGroup, false);
        return new ReportNotificationViewHolder(itemReportNotifcationBinding.getRoot(), itemReportNotifcationBinding);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportNotificationViewHolder viewHolder = (ReportNotificationViewHolder) holder;
        DataSnapshot item = mListData.get(position);
        //image && title
        HashMap<String, Object> maps = new HashMap<>();
        maps = (HashMap<String, Object>) item.child(Constants.PARAM_REPORTS).getValue();
        for (String key : maps.keySet()) {
            mFirebaseUtils.getFirebaseDatabase()
                    .getReference(Constants.PARAM_REPORTS)
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> dataImages = new ArrayList<>();
                            for (DataSnapshot item : dataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {
                                dataImages.add(item.child(Constants.PARAM_THUMB_URL).getValue().toString());
                            }
                            //image
                            if (dataImages.size() == 0) {
                                Glide.with(mContext)
                                        .load(R.drawable.icon_error)
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(viewHolder.getReportNotificationBinding().mImageViewReportNotification);
                            } else {
                                Glide.with(mContext)
                                        .load(dataImages.get(0))
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(viewHolder.getReportNotificationBinding().mImageViewReportNotification);
                            }
                            //title
                            viewHolder.getReportNotificationBinding().mTextViewTitle.setText(dataSnapshot.child(Constants.PARAM_TITLE).getValue() == null ? "" : dataSnapshot.child(Constants.PARAM_TITLE).getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            break;
        }


        //date
        viewHolder.getReportNotificationBinding().mTextViewDate.setText(Utils.dateFromMillisecond(Constants.DEFAULT_DATE_FORMAT, (Long) item.child(Constants.PARAM_CREATED_TIMESTAMP).getValue()));
        //content
        String content;
        switch (Integer.parseInt(item.child(Constants.PARAM_TYPE).getValue().toString())) {
            case 1:
                content = Constants.COMMENT_TYPE_1;
                break;
            case 2:
                content = Constants.COMMENT_TYPE_2;
                break;
            case 3:
                content = Constants.COMMENT_TYPE_3;
                break;
            case 4:
                content = Constants.COMMENT_TYPE_4;
                break;
            case 5:
                content = Constants.COMMENT_TYPE_5;
                break;
            default:
                content = Constants.COMMENT_TYPE_1;
                break;
        }
        viewHolder.getReportNotificationBinding().mTextViewContent.setText(content);

        viewHolder.itemView.setOnClickListener(v -> {
            if (mItemInteractionListener != null) {
                mItemInteractionListener.onInteraction(viewHolder.getReportNotificationBinding(), viewHolder.itemView, item, position);
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


    public static class ReportNotificationViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemReportNotificationBinding mReportNotificationBinding;

        public ReportNotificationViewHolder(View view, LayoutItemReportNotificationBinding itemReportNotificationBinding) {
            super(view);
            this.mReportNotificationBinding = itemReportNotificationBinding;
        }

        public LayoutItemReportNotificationBinding getReportNotificationBinding() {
            return mReportNotificationBinding;
        }
    }


}