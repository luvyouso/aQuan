package jp.co.asaichi.pubrepo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityReportContentBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class ReportContentActivity extends BaseActivity {

    private ActivityReportContentBinding mReportContentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReportContentBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_content);
        mReportContentBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.content_input);
        mReportContentBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportContentBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mReportContentBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportContentBinding.mIncludeToolbar.mTextViewRight.setText(R.string.save);
        mReportContentBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.GONE);
        mReportContentBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportContentBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mReportContentBinding.mEditTextReportContent.setText(bundle.getString(Constants.EXTRA_REPORT_CONTENT));
        }

        mReportContentBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.EXTRA_REPORT_CONTENT, mReportContentBinding.mEditTextReportContent.getText().toString().trim());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }
}
