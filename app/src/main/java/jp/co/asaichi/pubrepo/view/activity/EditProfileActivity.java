package jp.co.asaichi.pubrepo.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends BaseActivity {

    private ActivityEditProfileBinding mEditProfileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        mEditProfileBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.edit_profile);
        mEditProfileBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mEditProfileBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mEditProfileBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        mEditProfileBinding.mIncludeToolbar.mTextViewRight.setText(R.string.decide);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mEditProfileBinding.mIncludeToolbar.mImageViewBack);
        mEditProfileBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(this, R.color.white));
        mEditProfileBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            finish();
        });
        mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getAuthFirebase().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot item) {
                mEditProfileBinding.mEditTextName.setText(
                        item.child(Constants.PARAM_NAME).getValue() == null ? "" : item.child(Constants.PARAM_NAME).getValue().toString());
                mEditProfileBinding.mAppCompatCheckBox.setChecked(
                        item.child(Constants.PARAM_STATUS).getValue() == null ||
                                Integer.parseInt(item.child(Constants.PARAM_STATUS).getValue().toString()) == 1
                                ? true : false);
//                mEditProfileBinding.mEditTextAddress.setText(
//                        item.child(Constants.PARAM_ADDRESS).getValue() == null ? "" : item.child(Constants.PARAM_ADDRESS).getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEditProfileBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mEditProfileBinding.mEditTextName.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "投稿者名を入力して下さい", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            HashMap<String, Object> name = new HashMap<>();
            name.put(Constants.PARAM_NAME, mEditProfileBinding.mEditTextName.getText().toString().trim());
            mFirebaseUtils.getFirebaseDatabase()
                    .getReference(Constants.PARAM_ADMIN_USERS)
                    .child(mFirebaseUtils.getUser().getUid())
                    .updateChildren(name);

            mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                    .child(mFirebaseUtils.getAuthFirebase().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot item) {
                            mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                                    .child(mFirebaseUtils.getAuthFirebase().getUid())
                                    .child(Constants.PARAM_NAME).setValue(mEditProfileBinding.mEditTextName.getText().toString().trim());
                            mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                                    .child(mFirebaseUtils.getAuthFirebase().getUid())
                                    .child(Constants.PARAM_STATUS).setValue(mEditProfileBinding.mAppCompatCheckBox.isChecked() ? 1 : 0);

                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });
    }
}
