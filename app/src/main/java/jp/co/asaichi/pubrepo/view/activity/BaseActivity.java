package jp.co.asaichi.pubrepo.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.common.PopUpDlg;
import jp.co.asaichi.pubrepo.common.ProgressDlg;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.SharedPreference;
import jp.co.asaichi.pubrepo.view.fragment.MenuFragment;
import jp.co.asaichi.pubrepo.view.fragment.ReportMapFragment;
import jp.co.asaichi.pubrepo.view.fragment.ReportPostFragment;

/**
 * Created by nguyentu on 11/23/17.
 */

public class BaseActivity extends AppCompatActivity implements
        ReportMapFragment.OnFragmentInteractionListener,
        ReportPostFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener {

    protected SharedPreference mLocalData;
    protected FirebaseUtils mFirebaseUtils;
    protected ProgressDlg mProgressDlg;
    protected PopUpDlg mPopUpDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalData = SharedPreference.getInstance(this);
        mFirebaseUtils = new FirebaseUtils(this);
        mProgressDlg = new ProgressDlg(this);
        mPopUpDlg = new PopUpDlg(this, true);
    }

    @Override
    public void onFinishApi() {

    }

    @Override
    public void onFragmentInteractionReportPostSelectImage(ArrayList<String> datas) {

    }

    @Override
    public void onFragmentInteractionMenu(View view) {

    }


    @Override
    public void onFragmentInteractionMap(View view, DataSnapshot dataSnapshot) {

    }
}
