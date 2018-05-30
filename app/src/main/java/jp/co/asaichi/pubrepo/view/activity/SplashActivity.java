package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivitySplashBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding mSplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent intent = null;
            String dataLocal = mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER);
            if (dataLocal != null) {
                GoogleService googleService = new Gson().fromJson(dataLocal, GoogleService.class);
                mFirebaseUtils.connectFirebase(googleService);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, FirstPageActivity.class);
                }

            } else {
                intent = new Intent(SplashActivity.this, SelectionActivity.class);
            }

            startActivity(intent);
            finishAffinity();
        }, 1000);
    }
}
