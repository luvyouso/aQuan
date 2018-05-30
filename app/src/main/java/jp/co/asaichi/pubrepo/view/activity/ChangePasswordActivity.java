package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.databinding.ActivityChangePasswordBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding mChangePasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        mChangePasswordBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.change_password);
        mChangePasswordBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mChangePasswordBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mChangePasswordBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mChangePasswordBinding.mIncludeToolbar.mTextViewRight.setText(R.string.decide);
        mChangePasswordBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(this, R.color.white));
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mChangePasswordBinding.mIncludeToolbar.mImageViewBack);
        mChangePasswordBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mChangePasswordBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            if (TextUtils.isEmpty(mChangePasswordBinding.mEditTextPassword.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "新しいパスワードを入力して下さい", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(mChangePasswordBinding.mEditTextPassword.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            finish();
                        }
                    });

        });

    }
}
