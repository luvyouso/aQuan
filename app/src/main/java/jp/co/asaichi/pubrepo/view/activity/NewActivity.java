package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.ReportNewAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityNoteBinding;
import jp.co.asaichi.pubrepo.model.NewModel;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.Utils;

public class NewActivity extends BaseActivity {

    private final String TAG = NewActivity.class.getName();
    private ActivityNoteBinding mNoteBinding;
    private ArrayList<NewModel> mDatas;
    private ReportNewAdapter mReportNewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_note);
        mNoteBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.note);
        mNoteBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mNoteBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mNoteBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mNoteBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mNoteBinding.mIncludeToolbar.mImageViewBack);
        mNoteBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNoteBinding.mRecyclerViewNew.setLayoutManager(layoutManager);
        mNoteBinding.mRecyclerViewNew.setHasFixedSize(true);
        mDatas = new ArrayList<>();
        mReportNewAdapter = new ReportNewAdapter(this, mDatas);
        mNoteBinding.mRecyclerViewNew.setAdapter(mReportNewAdapter);

        mFirebaseUtils.getData(Constants.PARAM_NEW, new FirebaseUtils.EventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewModel newModel = snapshot.getValue(NewModel.class);
                    mDatas.add(newModel);
                }
                mReportNewAdapter.notifyDataSetChanged();
                mNoteBinding.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
