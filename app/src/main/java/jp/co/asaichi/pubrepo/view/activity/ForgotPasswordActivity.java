package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.databinding.ActivityForgotPasswordBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class ForgotPasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding mForgotPasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        mForgotPasswordBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.password_reissue);
        mForgotPasswordBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mForgotPasswordBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mForgotPasswordBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mForgotPasswordBinding.mIncludeToolbar.mImageViewBack);
        mForgotPasswordBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mForgotPasswordBinding.mButtonSend.setOnClickListener(v -> {
            if (!Utils.isEmailValid(mForgotPasswordBinding.mEditTextEmail.getText().toString().trim())) {
                mForgotPasswordBinding.mEditTextEmail.setError("Please, input email!");
                return;
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = mForgotPasswordBinding.mEditTextEmail.getText().toString().trim();
            auth.setLanguageCode("JP");
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            }
                        }
                    });
        });
    }
}
