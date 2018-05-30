package jp.co.asaichi.pubrepo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityReportTitleBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class ReportTitleActivity extends BaseActivity {

    private ActivityReportTitleBinding mReportTitleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReportTitleBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_title);
        mReportTitleBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.title_input);
        mReportTitleBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportTitleBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mReportTitleBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportTitleBinding.mIncludeToolbar.mTextViewRight.setText(R.string.save);
        mReportTitleBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.GONE);
        mReportTitleBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportTitleBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mReportTitleBinding.mEditTextReportTitle.setText(bundle.getString(Constants.EXTRA_REPORT_TITILE));
        }

        mReportTitleBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.EXTRA_REPORT_TITILE, mReportTitleBinding.mEditTextReportTitle.getText().toString().trim());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideSoftKeyboard(this);
    }
}
