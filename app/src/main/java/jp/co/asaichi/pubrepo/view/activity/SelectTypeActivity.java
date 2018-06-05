package jp.co.asaichi.pubrepo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivitySelectTypeBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class SelectTypeActivity extends BaseActivity {
    private ActivitySelectTypeBinding mSelectTypeBinding;
    private int mPositionCheck = 1;

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
            clearImage();
            switch (type) {
                case 1:
                    setImage(mSelectTypeBinding.mImageViewTypeRepair, R.drawable.type_repair_big_active);
                    break;
                case 2:
                    setImage(mSelectTypeBinding.mImageViewTypeWithDraw, R.drawable.type_withdraw_big_active);
                    break;
                case 3:
                    setImage(mSelectTypeBinding.mImageViewTypePrune, R.drawable.type_prune_big_active);
                    break;
                case 4:
                    setImage(mSelectTypeBinding.mImageViewTypeOthers, R.drawable.type_others_big_active);
                    break;
                default:
                    break;
            }
        }

        mSelectTypeBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mSelectTypeBinding.mLinearLayoutTypeRepair.setOnClickListener(v -> {
            clearImage();
            setImage(mSelectTypeBinding.mImageViewTypeRepair, R.drawable.type_repair_big_active);
            mPositionCheck = 1;
        });

        mSelectTypeBinding.mLinearLayoutTypeWithDraw.setOnClickListener(v -> {
            clearImage();
            setImage(mSelectTypeBinding.mImageViewTypeWithDraw, R.drawable.type_withdraw_big_active);
            mPositionCheck = 2;
        });

        mSelectTypeBinding.mLinearLayoutTypePrune.setOnClickListener(v -> {
            clearImage();
            setImage(mSelectTypeBinding.mImageViewTypePrune, R.drawable.type_prune_big_active);
            mPositionCheck = 3;
        });

        mSelectTypeBinding.mLinearLayoutTypeOthers.setOnClickListener(v -> {
            clearImage();
            setImage(mSelectTypeBinding.mImageViewTypeOthers, R.drawable.type_others_big_active);
            mPositionCheck = 4;
        });

        mSelectTypeBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.EXTRA_REPORT_TYPE, mPositionCheck);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

    }

    private void clearImage() {
        setImage(mSelectTypeBinding.mImageViewTypeRepair, R.drawable.type_repair_big);
        setImage(mSelectTypeBinding.mImageViewTypeWithDraw, R.drawable.type_withdraw_big);
        setImage(mSelectTypeBinding.mImageViewTypePrune, R.drawable.type_prune_big);
        setImage(mSelectTypeBinding.mImageViewTypeOthers, R.drawable.type_others_big);
    }

    private void setImage(ImageView imageView, int drawable) {
        Glide.with(getApplicationContext())
                .load(drawable)
                .apply(RequestOptions
                        .centerCropTransform()
                        .error(R.drawable.icon_error))
                .into(imageView);
    }
}
