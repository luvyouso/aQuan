package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityWebBinding;

public class WebActivity extends BaseActivity {

    private ActivityWebBinding mWebBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebBinding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        mWebBinding.mWebViewContent.getSettings().setJavaScriptEnabled(true);
        mWebBinding.mWebViewContent.getSettings().setDomStorageEnabled(true);
        mWebBinding.mWebViewContent.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebBinding.mWebViewContent.getSettings().setLoadWithOverviewMode(true);
        mWebBinding.mWebViewContent.getSettings().setDisplayZoomControls(true);
        mWebBinding.mWebViewContent.getSettings().setDomStorageEnabled(true);
        mWebBinding.mWebViewContent.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            mWebBinding.mWebViewContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebBinding.mWebViewContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebBinding.mWebViewContent.setWebChromeClient(new WebChromeClient());
        mWebBinding.mWebViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mWebBinding.mWebViewContent.setVisibility(View.GONE);
                mWebBinding.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebBinding.mWebViewContent.setVisibility(View.VISIBLE);
                mWebBinding.mProgressBar.setVisibility(View.GONE);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String url = bundle.getString(Constants.EXTRA_TYPE_WEB);
            String title = bundle.getString(Constants.EXTRA_REPORT_TITILE);
            mWebBinding.mIncludeToolbar.mTextViewTitle.setText(title);
            mWebBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
            mWebBinding.mWebViewContent.loadUrl(url);
        }

        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mWebBinding.mIncludeToolbar.mImageViewBack);
        mWebBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mWebBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mWebBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(v -> {
            finish();
        });

    }
}
