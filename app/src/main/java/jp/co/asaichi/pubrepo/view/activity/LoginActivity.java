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
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityLoginBinding;
import jp.co.asaichi.pubrepo.utils.Utils;

public class LoginActivity extends BaseActivity {

    private final String TAG = LoginActivity.class.getName();
    private ActivityLoginBinding mLoginBinding;
    private CallbackManager mCallbackManager;
    private HashMap<String, Object> mMyUser;
    private final String TERM_OF_USER = "ログインしたときに利用規約に同意したものとみなします。";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mLoginBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.login);
        mLoginBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mLoginBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mLoginBinding.mIncludeToolbar.mImageViewBack);
        mLoginBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(this);
            finish();
        });

        mLoginBinding.mButtonRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        //term of user
        SpannableString term = new SpannableString(TERM_OF_USER);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
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
        term.setSpan(clickableSpan, 9, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLoginBinding.mTextViewTermOfUser.setMovementMethod(LinkMovementMethod.getInstance());
        mLoginBinding.mTextViewTermOfUser.setHighlightColor(Color.TRANSPARENT);
        mLoginBinding.mTextViewTermOfUser.setText(term);

        mMyUser = new HashMap<>();
        mLoginBinding.mButtonLogin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mLoginBinding.mEditTextUser.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "メールアドレスを入力して下さい。", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            if (TextUtils.isEmpty(mLoginBinding.mEditTextPassword.getText().toString().trim())) {
                mPopUpDlg.show("エラー", "パスワードを入力して下さい。", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            mProgressDlg.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    mLoginBinding.mEditTextUser.getText().toString().trim(),
                    mLoginBinding.mEditTextPassword.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        Utils.hideSoftKeyboard(LoginActivity.this);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithEmail: success");
                            saveUser(task, null);
                            mProgressDlg.hide();
                        } else {
                            mProgressDlg.hide();
                            // If sign in fails, display a message to the user.
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            String errorMessage;
                            switch (errorCode) {
//                                case "ERROR_INVALID_CUSTOM_TOKEN":
//                                    Toast.makeText(this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
//                                    break;

//                                case "ERROR_CUSTOM_TOKEN_MISMATCH":
//                                    Toast.makeText(this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
//                                    break;

//                                case "ERROR_INVALID_CREDENTIAL":
//                                    Toast.makeText(this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
//                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    errorMessage = "メールアドレスの形が正しくありません。";
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    errorMessage = "メールアドレス、またはパスワードが違います。";
                                    break;

//                                case "ERROR_USER_MISMATCH":
//                                    Toast.makeText(this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
//                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    errorMessage = "パスワードの変更や退会等の重要な変更をするには再度ログインして下さい。";
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    errorMessage = "既に別の認証方法で登録されているメールアドレスです。";
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    errorMessage = "既に登録されているメールアドレスです。メールアドレスの確認用のメールを再送する場合はログインをして下さい。";
                                    break;

//                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
//                                    Toast.makeText(this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
//                                    break;

//                                case "ERROR_USER_DISABLED":
//                                    Toast.makeText(this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
//                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    errorMessage = "トークンが無効になりました。再度ログインして下さい。";
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    errorMessage = "登録されていないメールアドレスです。";
                                    break;

//                                case "ERROR_INVALID_USER_TOKEN":
//                                    Toast.makeText(this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
//                                    break;

//                                case "ERROR_OPERATION_NOT_ALLOWED":
//                                    Toast.makeText(this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
//                                    break;

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

        mLoginBinding.mRelativeLayoutLoginFB.setOnClickListener(v -> {
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
                            mPopUpDlg.show("", exception.getMessage(), "", "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }, null);
                        }
                    });

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

        });

        mLoginBinding.mTextViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
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
        Log.d(TAG, "handleFacebookAccessToken:" + token);

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
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
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
//        mFirebaseUtils.saveData(Constants.PARAM_USER, mMyUser, mFirebaseUtils.getUser().getUid());

        mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getAuthFirebase().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot item) {
                HashMap<String, Object> name = new HashMap<>();
                name.put(Constants.PARAM_NAME, item.child(Constants.PARAM_NAME).getValue() == null ? "" : item.child(Constants.PARAM_NAME).getValue().toString());
                mFirebaseUtils.getFirebaseDatabase()
                        .getReference(Constants.PARAM_ADMIN_USERS)
                        .child(mFirebaseUtils.getUser().getUid())
                        .updateChildren(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseUtils.getFirebaseDatabase()
                .getReference(Constants.PARAM_USER)
                .child(mFirebaseUtils.getUser().getUid())
                .updateChildren(mMyUser);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}
