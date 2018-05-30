package jp.co.asaichi.pubrepo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.LayoutItemCommentReportBinding;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.Utils;


public class ReportCommentAdapter extends AbstractAdapter {

    public ArrayList<DataSnapshot> mListData;
    private ListItemInteractionListener mItemInteractionListener;
    private Context mContext;
    private FirebaseUtils mFirebaseUtils;

    public ReportCommentAdapter(Context context, ArrayList<DataSnapshot> datas) {
        this.mListData = datas;
        this.mContext = context;
        mFirebaseUtils = new FirebaseUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutItemCommentReportBinding itemCommentReportBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_item_comment_report, viewGroup, false);
        return new ReportCommentViewHolder(itemCommentReportBinding.getRoot(), itemCommentReportBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportCommentViewHolder reportCommentViewHolder = (ReportCommentViewHolder) holder;
        DataSnapshot dataSnapshot = mListData.get(position);
        //title
        String time = Utils.dateFromMillisecond(Constants.DATE_FORMAT_DB, (Long) dataSnapshot.child(Constants.PARAM_CREATED_TIMESTAMP).getValue());
        String nameUser = dataSnapshot.child(Constants.PARAM_CREATED_USER_NAME).getValue() == null ? "" : dataSnapshot.child(Constants.PARAM_CREATED_USER_NAME).getValue().toString();
        String tempTime = time + "    担当  " + nameUser;
        reportCommentViewHolder.getCommentReportBinding().mTextViewTitle.setText(tempTime);

//        if (dataSnapshot.child(Constants.PARAM_CREATED_USER).getValue() != null) {
//            HashMap<String, Object> userHashMap = (HashMap<String, Object>) dataSnapshot.child(Constants.PARAM_CREATED_USER).getValue();
//            for (String key : userHashMap.keySet()) {
//                mFirebaseUtils.getFirebaseDatabase()
//                        .getReference(Constants.PARAM_USER)
//                        .child(key)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshots) {
//                                String name = "担当  " + dataSnapshots.child(Constants.PARAM_NAME).getValue() == null ? "" : dataSnapshots.child(Constants.PARAM_NAME).getValue().toString();
//                                reportCommentViewHolder.getCommentReportBinding().mTextViewTitle.setText(time + "   " + name);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//            }
//        }
        //content
        reportCommentViewHolder.getCommentReportBinding().mTextViewContent.setText(dataSnapshot.child(Constants.PARAM_CONTENT).getValue() == null ? "" : dataSnapshot.child(Constants.PARAM_CONTENT).getValue().toString());

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


    @Override
    public int getItemCount() {
        return mListData.size();
    }


    public static class ReportCommentViewHolder extends RecyclerView.ViewHolder {
        private LayoutItemCommentReportBinding mCommentReportBinding;

        public ReportCommentViewHolder(View view, LayoutItemCommentReportBinding itemCommentReportBinding) {
            super(view);
            this.mCommentReportBinding = itemCommentReportBinding;
        }

        public LayoutItemCommentReportBinding getCommentReportBinding() {
            return mCommentReportBinding;
        }
    }


}