package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.AbstractAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportNotificationAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityReportNotificationBinding;

public class ReportNotificationActivity extends BaseActivity implements AbstractAdapter.ListItemInteractionListener {

    private ActivityReportNotificationBinding mReportNotificationBinding;
    private ArrayList<DataSnapshot> mDatas;
    private ReportNotificationAdapter mReportNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReportNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_notification);
        mReportNotificationBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.report_notification);
        mReportNotificationBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportNotificationBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mReportNotificationBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mReportNotificationBinding.mIncludeToolbar.mImageViewBack);
        mReportNotificationBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            finish();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReportNotificationBinding.mRecyclerViewReportNotification.setLayoutManager(layoutManager);
        mReportNotificationBinding.mRecyclerViewReportNotification.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        mReportNotificationBinding.mRecyclerViewReportNotification.addItemDecoration(dividerItemDecoration);
        mDatas = new ArrayList<>();
        mReportNotificationAdapter = new ReportNotificationAdapter(this, mDatas);
        mReportNotificationBinding.mRecyclerViewReportNotification.setAdapter(mReportNotificationAdapter);
        mReportNotificationAdapter.setItemInteractionListener(this);

        mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getUser().getUid())
                .child(Constants.PARAM_NOTI_LIST)
                .orderByChild(Constants.PARAM_READ_FLG).equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshots) {
                        mDatas.clear();
                        for (DataSnapshot item : dataSnapshots.getChildren()) {
                            mDatas.add(item);
                        }
                        mReportNotificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position) {
        DataSnapshot dataSnapshot = mDatas.get(position);
        mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getUser().getUid())
                .child(Constants.PARAM_NOTI_LIST)
                .child(dataSnapshot.getKey())
                .child(Constants.PARAM_READ_FLG)
                .setValue(true);
        HashMap<String, Object> maps = new HashMap<>();
        maps = (HashMap<String, Object>) dataSnapshot.child(Constants.PARAM_REPORTS).getValue();
        for (String key : maps.keySet()) {
            Intent intent = new Intent(ReportNotificationActivity.this, ReportDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_REPORT_DETAIL, key);
            startActivity(intent);
            break;
        }
    }
}
