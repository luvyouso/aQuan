package jp.co.asaichi.pubrepo.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.common.PopUpDlg;
import jp.co.asaichi.pubrepo.databinding.ActivitySignUpBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class SignUpActivity extends BaseActivity {

    private final String TAG = SignUpActivity.class.getName();
    private ActivitySignUpBinding mSignUpBinding;
    private HashMap<String, Object> mMyUser;
    private CallbackManager mCallbackManager;
    private final String TERM_OF_USER = "登録したときに利用規約に同意したものとみなします。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mSignUpBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mSignUpBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.signup);
        mSignUpBinding.mIncludeToolbar.mTextViewRight.setVisibility(View.INVISIBLE);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mSignUpBinding.mIncludeToolbar.mImageViewBack);
        mSignUpBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        //term of user
        SpannableString term = new SpannableString(TERM_OF_USER);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(SignUpActivity.this, WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, Constants.URL_TERMS);
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.terms_of_service));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.cerulean));
            }
        };
        term.setSpan(clickableSpan, 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSignUpBinding.mTextViewTermOfUser.setMovementMethod(LinkMovementMethod.getInstance());
        mSignUpBinding.mTextViewTermOfUser.setHighlightColor(Color.TRANSPARENT);
        mSignUpBinding.mTextViewTermOfUser.setText(term);

        mMyUser = new HashMap<>();
        mSignUpBinding.mButtonRegister.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            if (!Utils.isValidEmail(mSignUpBinding.mEditTextUser.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "メールアドレスを入力して下さい。", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            if (TextUtils.isEmpty(mSignUpBinding.mEditTextPassword.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "パスワードを入力して下さい。", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            mProgressDlg.show();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(mSignUpBinding.mEditTextUser.getText().toString().trim(),
                    mSignUpBinding.mEditTextPassword.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        mProgressDlg.hide();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseUtils.getUser();
                            saveUser(task, null);

                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            String errorMessage;
                            switch (errorCode) {
                                case "ERROR_INVALID_EMAIL":
                                    errorMessage = "メールアドレスの形が正しくありません。";
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    errorMessage = "メールアドレス、またはパスワードが違います。";
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    errorMessage = "パスワードの変更や退会等の重要な変更をするには再度ログインして下さい。";
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    errorMessage = "既に別の認証方法で登録されているメールアドレスです。";
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    errorMessage = "既に登録されているメールアドレスです。メールアドレスの確認用のメールを再送する場合はログインをして下さい。";
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    errorMessage = "トークンが無効になりました。再度ログインして下さい。";
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    errorMessage = "登録されていないメールアドレスです。";
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    errorMessage = "パスワードは6文字以上で設定して下さい。";
                                    break;
                                default:
                                    errorMessage = "認証でエラーが発生いたしました。" + errorCode;
                                    break;
                            }
                            mPopUpDlg.show("エラー", errorMessage, "", "閉じる",
                                    (dialogInterface, i) -> dialogInterface.cancel()
                                    , null);
                        }
                    });
        });

        mSignUpBinding.mRelativeLayoutLoginFB.setOnClickListener(v -> {
            FacebookSdk.sdkInitialize(this.getApplicationContext());

            mCallbackManager = CallbackManager.Factory.create();
            mProgressDlg.show();
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            mProgressDlg.hide();
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            mProgressDlg.hide();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            mProgressDlg.hide();
                            mPopUpDlg.show("エラー", exception.getMessage(), "", "閉じる", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }, null);
                        }
                    });

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    /**
     * login fb
     *
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");
                        saveUser(task, token.getUserId());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void saveUser(Task<AuthResult> task, String userID) {
        mMyUser.put(Constants.PARAM_CREATED_TIMESTAMP, System.currentTimeMillis() / 1000);
        mMyUser.put(Constants.PARAM_DELETED_FLG, 0);
        mMyUser.put(Constants.PARAM_DEVICE_TOKEN, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        mMyUser.put(Constants.PARAM_EMAIL, task.getResult().getUser().getEmail());
        mMyUser.put(Constants.PARAM_STATUS, 1);
        if (!TextUtils.isEmpty(userID)) {
            mMyUser.put(Constants.PARAM_FACEBOOK_ID, userID);
        }
        mFirebaseUtils.getFirebaseDatabase()
                .getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getUser().getUid())
                .updateChildren(mMyUser);
        // email sent
        FirebaseAuth.getInstance().setLanguageCode("ja");
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                PopUpDlg popUpDlg = new PopUpDlg(SignUpActivity.this, true);
                popUpDlg.show("", "メールアドレスを認証するためのメールを送信しました。", "", "閉じる",
                        null, (dialogInterface, i) -> {
                            //TODO add admin user
                            HashMap<String, Object> name = new HashMap<>();
                            name.put(Constants.PARAM_NAME, "");
                            mFirebaseUtils.getFirebaseDatabase()
                                    .getReference(Constants.PARAM_ADMIN_USERS)
                                    .child(mFirebaseUtils.getUser().getUid())
                                    .updateChildren(name);

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            dialogInterface.cancel();
                            finishAffinity();
                        });
            }
        });
    }
}
