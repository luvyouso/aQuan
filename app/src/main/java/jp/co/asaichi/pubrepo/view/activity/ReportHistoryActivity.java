package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.AbstractAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportHistoryAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityReportHistoryBinding;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;

public class ReportHistoryActivity extends BaseActivity implements AbstractAdapter.ListItemInteractionListener {
    private ArrayList<DataSnapshot> mDatas;
    private ReportHistoryAdapter mReportHistoryAdapter;
    private ActivityReportHistoryBinding mReportHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);
        mReportHistoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_history);
        mReportHistoryBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.report_history);
        mReportHistoryBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportHistoryBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mReportHistoryBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mReportHistoryBinding.mIncludeToolbar.mImageViewBack);
        mReportHistoryBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            finish();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReportHistoryBinding.mRecyclerViewReportHistory.setLayoutManager(layoutManager);
        mReportHistoryBinding.mRecyclerViewReportHistory.setHasFixedSize(true);
        mDatas = new ArrayList<>();
        mReportHistoryAdapter = new ReportHistoryAdapter(this, mDatas);
        mReportHistoryBinding.mRecyclerViewReportHistory.setAdapter(mReportHistoryAdapter);
        mReportHistoryAdapter.setItemInteractionListener(this);
        mFirebaseUtils.getDataAutoUpdate(Constants.PARAM_REPORTS, new FirebaseUtils.EventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatas.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    HashMap<String, Object> hashMapData = (HashMap<String, Object>) item.getValue();
                    HashMap<String, Object> keys = (HashMap<String, Object>) hashMapData.get(Constants.PARAM_CREATED_USER);
                    for (String key : keys.keySet()) {
                        if (key.equals(mFirebaseUtils.getUser().getUid())) {
                            Long active = hashMapData.get(Constants.PARAM_ACTIVE) == null ? 0 : (Long) hashMapData.get(Constants.PARAM_ACTIVE);
                            Long status = hashMapData.get(Constants.PARAM_STATUS) == null ? 0 : (Long) hashMapData.get(Constants.PARAM_STATUS);
                            if (active == 1 && 0 < status && status < 5) {
                                mDatas.add(item);
                            }
                        }
                    }
                }
                mReportHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position) {
        DataSnapshot dataSnapshot = (DataSnapshot) model;
        Long active = dataSnapshot.child(Constants.PARAM_ACTIVE).getValue() == null ? 0 : (Long) dataSnapshot.child(Constants.PARAM_ACTIVE).getValue();
        Long status = (Long) dataSnapshot.child(Constants.PARAM_STATUS).getValue();
        if (active == 1) {
            if (status == 1 || status == 0) {
                Intent intent = new Intent(ReportHistoryActivity.this, ReportPostActivity.class);
                intent.putExtra(Constants.EXTRA_REPORT_EDIT, dataSnapshot.getKey());
                startActivity(intent);
            } else {
                Intent intent = new Intent(ReportHistoryActivity.this, ReportDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_REPORT_DETAIL, dataSnapshot.getKey());
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ReportHistoryActivity.this, ReportPostActivity.class);
            intent.putExtra(Constants.EXTRA_REPORT_EDIT, dataSnapshot.getKey());
            startActivity(intent);
        }
    }
}
