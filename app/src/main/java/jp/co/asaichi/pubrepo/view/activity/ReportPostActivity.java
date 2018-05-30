package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.view.fragment.ReportPostFragment;
import me.iwf.photopicker.PhotoPicker;

public class ReportPostActivity extends BaseActivity {

    private final int REQUEST_CODE_CHOOSE_AVATAR = 222;
    private final int REQUEST_CODE_SELECT_TYPE_ACTIVITY = 333;
    private final String TAG = ReportPostActivity.class.getName();
    private Fragment mFragment;
    private String mKeyEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mKeyEdit = bundle.getString(Constants.EXTRA_REPORT_EDIT);
        }
        mFragment = ReportPostFragment.newInstance(mKeyEdit);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mContainer, mFragment, ReportPostFragment.class.getName()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_AVATAR:
                    List<String> photos = null;
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    }
                    if (photos != null) {
                        if (mFragment instanceof ReportPostFragment) {
                            ((ReportPostFragment) mFragment).updateImage((ArrayList<String>) photos);
                        }
                    }
                    break;
                case ReportPostFragment.REQUEST_CODE_SELECT_TYPE_ACTIVITY:
                    if (mFragment instanceof ReportPostFragment) {
                        ((ReportPostFragment) mFragment).updateType(data.getIntExtra(Constants.EXTRA_REPORT_TYPE, 0));
                    }
                    break;
                case ReportPostFragment.REQUEST_PLACE_PICKER:
                    final Place place = PlacePicker.getPlace(data, ReportPostActivity.this);
                    if (mFragment instanceof ReportPostFragment) {
                        ((ReportPostFragment) mFragment).updateMap(place);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onFragmentInteractionReportPostSelectImage(ArrayList<String> datas) {
        PhotoPicker.builder()
                .setSelected(datas)
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this, REQUEST_CODE_CHOOSE_AVATAR);
    }

    @Override
    public void onFinishApi() {
        finish();
    }
}
