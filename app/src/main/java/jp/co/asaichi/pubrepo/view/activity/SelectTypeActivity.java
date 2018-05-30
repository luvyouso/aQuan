package jp.co.asaichi.pubrepo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivitySelectTypeBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class SelectTypeActivity extends BaseActivity {
    private ActivitySelectTypeBinding mSelectTypeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectTypeBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_type);

        mSelectTypeBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.select_type);
        mSelectTypeBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mSelectTypeBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mSelectTypeBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mSelectTypeBinding.mIncludeToolbar.mTextViewRight.setText(R.string.decide);
        mSelectTypeBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(this, R.color.white));
        mSelectTypeBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mSelectTypeBinding.mIncludeToolbar.mImageViewBack);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt(Constants.EXTRA_REPORT_TYPE);
            switch (type) {
                case 1:
                    mSelectTypeBinding.mAppCompatCheckBoxTypeRepair.setChecked(true);
                    break;
                case 2:
                    mSelectTypeBinding.mAppCompatCheckBoxTypeWithDraw.setChecked(true);
                    break;
                case 3:
                    mSelectTypeBinding.mAppCompatCheckBoxTypePrune.setChecked(true);
                    break;
                case 4:
                    mSelectTypeBinding.mAppCompatCheckBoxTypeOthers.setChecked(true);
                    break;
                default:
                    break;
            }
        }
        mSelectTypeBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mSelectTypeBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            int positionCheck = 0;
            if (mSelectTypeBinding.mAppCompatCheckBoxTypeRepair.isChecked()) {
                positionCheck = 1;
            }
            if (mSelectTypeBinding.mAppCompatCheckBoxTypeWithDraw.isChecked()) {
                positionCheck = 2;
            }
            if (mSelectTypeBinding.mAppCompatCheckBoxTypePrune.isChecked()) {
                positionCheck = 3;
            }
            if (mSelectTypeBinding.mAppCompatCheckBoxTypeOthers.isChecked()) {
                positionCheck = 4;
            }
            Utils.hideSoftKeyboard(this);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.EXTRA_REPORT_TYPE, positionCheck);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

    }
}
