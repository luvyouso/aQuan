package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.ViewPagerAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityDetailImageBinding;
import jp.co.asaichi.pubrepo.utils.Utils;
import jp.co.asaichi.pubrepo.view.fragment.DetailImageFragment;

public class DetailImageActivity extends BaseActivity {

    private ActivityDetailImageBinding mDetailImageBinding;
    private ViewPagerAdapter mViewPagerAdapter;
    private ArrayList<String> mDatas;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mDetailImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_image);
//        mDetailImageBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.report_details);
//        mDetailImageBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//        mDetailImageBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
//        Glide.with(this)
//                .load(R.drawable.icon_back)
//                .into(mDetailImageBinding.mIncludeToolbar.mImageViewBack);
        mDetailImageBinding.mImageViewBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        mDatas = new ArrayList<>();
        if (bundle != null) {
            mDatas.addAll(bundle.getStringArrayList(Constants.EXTRA_REPORT_IMAGES));
            mPosition = bundle.getInt(Constants.EXTRA_REPORT_POSITION);
        }
        for (String s : mDatas) {
            mViewPagerAdapter.addFragment(DetailImageFragment.newInstance(s));
        }

        mDetailImageBinding.mViewPagerDetailImage.setAdapter(mViewPagerAdapter);
        mDetailImageBinding.mViewPagerDetailImage.setCurrentItem(mPosition);
    }
}
