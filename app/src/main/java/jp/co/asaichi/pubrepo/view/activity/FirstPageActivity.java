package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityFirstPageBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;
import me.iwf.photopicker.databinding.LayoutToolbarBinding;

public class FirstPageActivity extends BaseActivity {

    private final String TAG = FirstPageActivity.class.getName();
    private ActivityFirstPageBinding mFirstPageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstPageBinding = DataBindingUtil.setContentView(this, R.layout.activity_first_page);
        GoogleService googleService = new Gson().fromJson(mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER).toString(), GoogleService.class);
        mFirstPageBinding.mIncludeToolbar.mTextViewTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_logo_select));
        TextView mTextViewName = findViewById(R.id.mTextViewName); //not remove cache binding :(
        mTextViewName.setVisibility(View.VISIBLE);
        mTextViewName.setText(" @" + googleService.getName());


        mFirstPageBinding.mButtonMain.setOnClickListener(v -> {
            Intent intent = new Intent(FirstPageActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();

        });

        mFirstPageBinding.mButtonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(FirstPageActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        mFirstPageBinding.mButtonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(FirstPageActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

}
