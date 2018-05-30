package jp.co.asaichi.pubrepo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.ActivityMainBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;
import jp.co.asaichi.pubrepo.utils.Utils;
import jp.co.asaichi.pubrepo.view.fragment.MenuFragment;
import jp.co.asaichi.pubrepo.view.fragment.ReportMapFragment;

public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getName();
    private ActivityMainBinding mMainBinding;
    private Fragment mFragment;
    private GoogleService mGoogleServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //Default view show on app
        mFragment = new ReportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mContainer, mFragment, ReportMapFragment.class.getName()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.hideSoftKeyboard(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case ReportMapFragment.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        if (mFragment instanceof ReportMapFragment) {
//                            ((ReportMapFragment) mFragment).getDeviceLocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");

                        break;
                }
                break;

            default:
                break;
        }
    }

    /**
     * handle click menu item
     *
     * @param view
     */
    public void onClick(View view) {
        Class fragmentClass = null;
        Bundle bundle = null;
        mFragment = null;
        switch (view.getId()) {
            case R.id.mImageViewMapMarker:
                bundle = new Bundle();
                fragmentClass = ReportMapFragment.class;
                mMainBinding.mImageViewMapMarker.setColorFilter(ContextCompat.getColor(this, R.color.yellow_lemon));
                mMainBinding.mImageViewMenu.setColorFilter(ContextCompat.getColor(this, R.color.white));

                break;
            case R.id.mImageViewMenu:
                bundle = new Bundle();
                fragmentClass = MenuFragment.class;
                mMainBinding.mImageViewMapMarker.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mMainBinding.mImageViewMenu.setColorFilter(ContextCompat.getColor(this, R.color.yellow_lemon));
                break;
            default:
                break;
        }
        if (fragmentClass != null) {
            try {
                mFragment = (Fragment) fragmentClass.newInstance();
                if (bundle != null) {
                    mFragment.setArguments(bundle);
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mContainer, mFragment, fragmentClass.getName()).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFragmentInteractionMap(View view, DataSnapshot dataSnapshot) {
        Intent intent;
        switch (view.getId()) {
            case R.id.layout_bottom_sheet:
                if (dataSnapshot != null && dataSnapshot.getKey() != null) {
                    intent = new Intent(MainActivity.this, ReportDetailsActivity.class);
                    intent.putExtra(Constants.EXTRA_REPORT_DETAIL, dataSnapshot.getKey());
                    startActivity(intent);
                }
                break;
            case R.id.mImageViewRight:
                if (!mFirebaseUtils.isLogin()) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(MainActivity.this, ReportPostActivity.class);
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }


    @Override
    public void onFragmentInteractionMenu(View view) {
        Intent intent;
        mGoogleServer = new Gson().fromJson(mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER), GoogleService.class);
        switch (view.getId()) {
            case R.id.mItemMenuNotification:
                intent = new Intent(MainActivity.this, ReportNotificationActivity.class);
                startActivity(intent);

                break;
            case R.id.mItemMenuEditProfile:
                intent = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(intent);

                break;
            case R.id.mItemMenuChangePassword:
                intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);

                break;
            case R.id.mItemMenuHistory:
                intent = new Intent(MainActivity.this, ReportHistoryActivity.class);
                startActivity(intent);

                break;
            case R.id.mItemMenuQuestion:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, mGoogleServer.getFaqUrl());
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.question));
                startActivity(intent);

                break;
            case R.id.mItemMenuUserQuide:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, mGoogleServer.getGuidUrl());
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.user_guide));
                startActivity(intent);
                break;
            case R.id.mItemMenuTermOfService:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, mGoogleServer.getAgreementUrl());
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.terms_of_service));
                startActivity(intent);
                break;
            case R.id.mItemMenuNote:
                intent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(intent);
                break;
            case R.id.mItemMenuUnsubscribe:
                intent = new Intent(MainActivity.this, UnsubscribeActivity.class);
                startActivity(intent);
                break;
            case R.id.mItemMenuPrivacy:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, mGoogleServer.getPrivacyPolicyUrl());
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.privacy_policy));
                startActivity(intent);
                break;
            case R.id.mItemMenuNew:
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

}
