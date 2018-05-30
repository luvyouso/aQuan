package jp.co.asaichi.pubrepo.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.common.PopUpDlg;
import jp.co.asaichi.pubrepo.databinding.ActivityUnsubscribeBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class UnsubscribeActivity extends BaseActivity {

    private ActivityUnsubscribeBinding mUnsubscribeBinding;
    private boolean isUnsubscribe = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnsubscribeBinding = DataBindingUtil.setContentView(this, R.layout.activity_unsubscribe);
        mUnsubscribeBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.unsubscribe);
        mUnsubscribeBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mUnsubscribeBinding.mIncludeToolbar.mTextViewBack.setText(R.string.menu);
        mUnsubscribeBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mUnsubscribeBinding.mIncludeToolbar.mImageViewBack);
        mUnsubscribeBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mUnsubscribeBinding.mButtonUnsubscribe.setOnClickListener(v -> {
            if (isUnsubscribe) {
                PopUpDlg popUpDlg = new PopUpDlg(this, false);
                popUpDlg.show("退会", "登録されたユーザー情報を削除します。", "退会する", "キャンセル",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mUnsubscribeBinding.mTextViewUnsubseribe.setText("ご利用ありがとうございました。 \n アプリを利用して、引き続きレポートの閲覧");
                                mUnsubscribeBinding.mButtonUnsubscribe.setText("レポートを見る");
                                mUnsubscribeBinding.mIncludeToolbar.mLinearLayoutBack.setVisibility(View.GONE);
                                isUnsubscribe = false;
                                mFirebaseUtils.getFirebaseDatabase() //update status 0 is login not login
                                        .getReference(Constants.PARAM_USER)
                                        .child(mFirebaseUtils.getUser().getUid())
                                        .child(Constants.PARAM_STATUS)
                                        .setValue(0);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            } else {
                //mFirebaseUtils.getUser().delete();
                mFirebaseUtils.getFirebaseDatabase() //update status 0 is login not login
                        .getReference(Constants.PARAM_USER)
                        .child(mFirebaseUtils.getUser().getUid())
                        .child(Constants.PARAM_STATUS)
                        .setValue(0)
                        .addOnCompleteListener(voi -> {
                            mFirebaseUtils.logout();
                            Intent intent = new Intent(UnsubscribeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        });
            }
        });
    }
}
