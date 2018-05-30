package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.AbstractAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportCommentAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportDetailAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.common.MyAplication;
import jp.co.asaichi.pubrepo.databinding.ActivityReportDetailsBinding;
import jp.co.asaichi.pubrepo.utils.Utils;
import jp.co.asaichi.pubrepo.view.custom.WorkaroundMapFragment;

public class ReportDetailsActivity extends BaseActivity implements OnMapReadyCallback, AbstractAdapter.ListItemInteractionListener {

    private final String TAG = ReportDetailsActivity.class.getName();
    private final int DEFAULT_ZOOM = 15;
    private ActivityReportDetailsBinding mReportDetailsBinding;
    private ArrayList<String> mDatas;
    private ArrayList<String> mDatasImage;
    private ReportDetailAdapter mReportDetailAdapter;
    private GoogleMap mMapGoogleMap;
    private DataSnapshot mDataSnapshot;
    private String mKey;
    private ReportCommentAdapter mReportCommentAdapter;
    private ArrayList<DataSnapshot> mDataComments;
    private String mIdUserCreateReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReportDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_details);

        mReportDetailsBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.report_details);
        mReportDetailsBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        mReportDetailsBinding.mIncludeToolbar.mTextViewBack.setText(R.string.map);
        mReportDetailsBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(this, R.color.white));
        Glide.with(this)
                .load(R.drawable.icon_back)
                .into(mReportDetailsBinding.mIncludeToolbar.mImageViewBack);
        mReportDetailsBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(view -> {
            finish();
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mFragmentMap);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mReportDetailsBinding.mRecyclerViewReportDetail.setLayoutManager(layoutManager);
        mReportDetailsBinding.mRecyclerViewReportDetail.setHasFixedSize(true);

        mReportDetailsBinding.mRecyclerViewReportComment.setLayoutManager(new LinearLayoutManager(this));
        mReportDetailsBinding.mRecyclerViewReportComment.setHasFixedSize(true);
        mDatas = new ArrayList<>();
        mDatasImage = new ArrayList<>();
        mDataComments = new ArrayList<>();

        if (bundle != null) {
            mKey = bundle.getString(Constants.EXTRA_REPORT_DETAIL);
            mFirebaseUtils.getFirebaseDatabase()
                    .getReference(Constants.PARAM_REPORTS)
                    .child(mKey)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mDataSnapshot = dataSnapshot;
                            mDatas.clear();
                            mDataComments.clear();
                            //user create
                            for (DataSnapshot createUsers : dataSnapshot.child(Constants.PARAM_CREATED_USER).getChildren()) {
                                mIdUserCreateReport = createUsers.getKey();
                                break;
                            }
                            //status
                            String status = "";
                            if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 2) {
                                status = Constants.STATUS_2_WAITING;
                                Glide.with(MyAplication.getInstance())
                                        .load(R.drawable.icon_pin_red)
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(mReportDetailsBinding.mImageViewStatus);

                            } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 3) {
                                status = Constants.STATUS_3_DURING;
                                Glide.with(MyAplication.getInstance())
                                        .load(R.drawable.icon_pin_yellow)
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(mReportDetailsBinding.mImageViewStatus);

                            } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 4) {
                                status = Constants.STATUS_4_CORRESPONDING;
                                Glide.with(MyAplication.getInstance())
                                        .load(R.drawable.icon_pin_green)
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(mReportDetailsBinding.mImageViewStatus);
                            } else {
                                status = Constants.STATUS_1_UNRECOGNIZED;
                                Glide.with(MyAplication.getInstance())
                                        .load(R.drawable.icon_pin_gray)
                                        .apply(RequestOptions
                                                .centerInsideTransform()
                                                .error(R.drawable.icon_error))
                                        .into(mReportDetailsBinding.mImageViewStatus);
                            }

                            String convertStatus = Constants.STATUS + status;
                            SpannableString ssStatus = new SpannableString(convertStatus);
                            ssStatus.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.text_gray)), 0, 2, 0);
                            mReportDetailsBinding.mTextViewStatus.setText(ssStatus);

                            //type
                            int type = Integer.parseInt(dataSnapshot.child(Constants.PARAM_TYPE).getValue().toString());
                            CharSequence statusType = "";
                            switch (type) {
                                case 1:
                                    Glide.with(MyAplication.getInstance())
                                            .load(R.drawable.icon_type_repair)
                                            .apply(RequestOptions
                                                    .centerInsideTransform()
                                                    .error(R.drawable.icon_error))
                                            .into(mReportDetailsBinding.mImageViewType);
                                    statusType = getText(R.string.repair);
                                    break;
                                case 2:
                                    Glide.with(MyAplication.getInstance())
                                            .load(R.drawable.icon_type_withdraw)
                                            .apply(RequestOptions
                                                    .centerInsideTransform()
                                                    .error(R.drawable.icon_error))
                                            .into(mReportDetailsBinding.mImageViewType);
                                    statusType = getText(R.string.remove);
                                    break;
                                case 3:
                                    Glide.with(MyAplication.getInstance())
                                            .load(R.drawable.icon_type_prune)
                                            .apply(RequestOptions
                                                    .centerInsideTransform()
                                                    .error(R.drawable.icon_error))
                                            .into(mReportDetailsBinding.mImageViewType);
                                    statusType = getText(R.string.pruning);
                                    break;
                                case 4:
                                    Glide.with(MyAplication.getInstance())
                                            .load(R.drawable.icon_type_others)
                                            .apply(RequestOptions
                                                    .centerInsideTransform()
                                                    .error(R.drawable.icon_error))
                                            .into(mReportDetailsBinding.mImageViewType);
                                    statusType = getText(R.string.others);
                                    break;
                                default:
                                    break;
                            }
                            String convertStatusType = Constants.STATUS_TYPE + statusType;
                            SpannableString ssType = new SpannableString(convertStatusType);
                            ssType.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.text_gray)), 0, 2, 0);
                            mReportDetailsBinding.mTextViewType.setText(ssType);

//                            Long status = (Long) dataSnapshot.child(Constants.PARAM_STATUS).getValue();
//                            if (status == 2) {
//                                mReportDetailsBinding.mImageViewType.setColorFilter(ContextCompat.getColor(ReportDetailsActivity.this, R.color.red_validate));
//                            } else if (status == 3) {
//                                mReportDetailsBinding.mImageViewType.setColorFilter(ContextCompat.getColor(ReportDetailsActivity.this, R.color.yellow));
//                            } else {
//                                mReportDetailsBinding.mImageViewType.setColorFilter(ContextCompat.getColor(ReportDetailsActivity.this, R.color.green_haze));
//                            }

                            //like
                            int countLike = 0;
                            if (dataSnapshot.child(Constants.PARAM_LIKES).getValue() == null) {
                                countLike = 0;
                            } else {
                                HashMap<String, Boolean> likes = (HashMap<String, Boolean>) dataSnapshot.child(Constants.PARAM_LIKES).getValue();
                                for (String key : likes.keySet()) {
                                    if (likes.get(key)) {
                                        countLike++;
                                    }
                                }
                                if (mFirebaseUtils.getUser() != null && mFirebaseUtils.getUser().getUid() != null
                                        && likes.get(mFirebaseUtils.getUser().getUid()) != null && likes.get(mFirebaseUtils.getUser().getUid())) {
                                    mReportDetailsBinding.mTextViewLike.setTypeface(null, Typeface.BOLD);
                                    mReportDetailsBinding.mTextViewLike.setTextColor(ContextCompat.getColor(ReportDetailsActivity.this, R.color.orange));
                                }
                            }
                            String contentLike = "気になる!\n" + countLike;
                            SpannableString ss = new SpannableString(contentLike);
                            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.text_gray)), 0, 5, 0);
                            mReportDetailsBinding.mTextViewLike.setText(ss);

                            //date
                            mReportDetailsBinding.mTextViewDate.setText(Utils.dateFromMillisecond(Constants.DEFAULT_DATE_FORMAT, (Long) dataSnapshot.child(Constants.PARAM_CREATED_TIMESTAMP).getValue()));
                            //user name
                            HashMap<String, Object> userHashMap = (HashMap<String, Object>) dataSnapshot.child(Constants.PARAM_CREATED_USER).getValue();
                            for (String key : userHashMap.keySet()) {
                                mFirebaseUtils.getFirebaseDatabase()
                                        .getReference(Constants.PARAM_USER)
                                        .child(key)
                                        .child(Constants.PARAM_NAME)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshots) {
                                                mReportDetailsBinding.mTextViewNameUser.setText(
                                                        dataSnapshots == null || dataSnapshots.getValue() == null ? "" :
                                                                dataSnapshots.getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                            //title
                            mReportDetailsBinding.mTextViewTitle.setText(dataSnapshot.child(Constants.PARAM_TITLE).getValue() == null ? "" : dataSnapshot.child(Constants.PARAM_TITLE).getValue().toString());
                            //content
                            mReportDetailsBinding.mTextViewContent.setText(dataSnapshot.child(Constants.PARAM_CONTENT).getValue() == null ? "" : dataSnapshot.child(Constants.PARAM_CONTENT).getValue().toString());
                            //image
                            mDatas.clear();
                            mDatasImage.clear();
                            for (DataSnapshot image : dataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {//TODO logic show
                                Long statusImage = Long.valueOf(image.child(Constants.PARAM_STATUS).getValue() == null ? 0 : (Long) image.child(Constants.PARAM_STATUS).getValue());
                                if (statusImage == 1) {
                                    mDatas.add(image.child(Constants.PARAM_THUMB_URL).getValue().toString());
                                    mDatasImage.add(image.child(Constants.PARAM_URL).getValue().toString());
                                }
                            }

                            if (mDatas.size() > 0) {
                                Collections.sort(mDatas);
                            }
                            if (mDatasImage.size() > 0) {
                                Collections.sort(mDatasImage);
                            }
                            if (mDatas != null) {
                                mReportDetailAdapter = new ReportDetailAdapter(ReportDetailsActivity.this, mDatas, false);
                                mReportDetailsBinding.mRecyclerViewReportDetail.setAdapter(mReportDetailAdapter);
                                mReportDetailAdapter.setItemInteractionListener(ReportDetailsActivity.this);
                            }
                            //address
                            mReportDetailsBinding.mTextViewAddress.setText(dataSnapshot.child(Constants.PARAM_ADDRESS).getValue().toString());
                            //map
                            // Add a default marker, because the user hasn't selected a place.
                            MarkerOptions marker = new MarkerOptions()
                                    .position(new LatLng((Double) dataSnapshot.child(Constants.PARAM_LATITUDE).getValue(),
                                            (Double) dataSnapshot.child(Constants.PARAM_LONGITUDE).getValue()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shape_copy));
                            mMapGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng((Double) dataSnapshot.child(Constants.PARAM_LATITUDE).getValue(),
                                            (Double) dataSnapshot.child(Constants.PARAM_LONGITUDE).getValue()), DEFAULT_ZOOM));
                            mMapGoogleMap.addMarker(marker);

                            //comment
                            if (dataSnapshot.child(Constants.PARAM_COMMENTS).getValue() != null) {
                                for (DataSnapshot item : dataSnapshot.child(Constants.PARAM_COMMENTS).getChildren()) {
                                    mDataComments.add(item);
                                }
                            }
                            if (mDataComments.size() > 0) {
                                mReportCommentAdapter = new ReportCommentAdapter(ReportDetailsActivity.this, mDataComments);
                                mReportDetailsBinding.mRecyclerViewReportComment.setAdapter(mReportCommentAdapter);
                                mReportDetailsBinding.mTextViewLabelComment.setVisibility(View.VISIBLE);
                            } else {
                                mReportDetailsBinding.mTextViewLabelComment.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mFragmentMap))
                    .setListener(() -> mReportDetailsBinding.mScrollViewReport.requestDisallowInterceptTouchEvent(true));
        }

        mReportDetailsBinding.mLinearLayoutLike.setOnClickListener(v -> {
            if (mFirebaseUtils.getUser() == null || mFirebaseUtils.getUser().getUid() == null) {
                mPopUpDlg.show("", "気になる！をするにはユーザー登録が必要です。", "", "閉じる",
                        (dialogInterface, i) -> dialogInterface.cancel()
                        , null);
                return;
            }
            //like
            HashMap<String, Object> likes = (HashMap<String, Object>) mDataSnapshot.child(Constants.PARAM_LIKES).getValue();
            if (likes == null) {
                likes = new HashMap<>();
                likes.put(mFirebaseUtils.getUser().getUid(), true);

            } else if (likes.get(mFirebaseUtils.getUser().getUid()) == null) {
                likes.put(mFirebaseUtils.getUser().getUid(), true);
                mReportDetailsBinding.mTextViewLike.setTypeface(null, Typeface.BOLD);
                mReportDetailsBinding.mTextViewLike.setTextColor(ContextCompat.getColor(ReportDetailsActivity.this, R.color.orange));
            } else {
                boolean like = ((boolean) likes.get(mFirebaseUtils.getUser().getUid())) == true ? false : true;
                likes.put(mFirebaseUtils.getUser().getUid(), like);
                if (like) {
                    mReportDetailsBinding.mTextViewLike.setTypeface(null, Typeface.BOLD);
                    mReportDetailsBinding.mTextViewLike.setTextColor(ContextCompat.getColor(ReportDetailsActivity.this, R.color.orange));
                } else {

                    mReportDetailsBinding.mTextViewLike.setTypeface(null, Typeface.NORMAL);
                    mReportDetailsBinding.mTextViewLike.setTextColor(ContextCompat.getColor(ReportDetailsActivity.this, R.color.black));
                }
            }
            mFirebaseUtils.getFirebaseDatabase()
                    .getReference(Constants.PARAM_REPORTS)
                    .child(mKey)
                    .child(Constants.PARAM_LIKES)
                    .updateChildren(likes);

            //notification
            if (!mIdUserCreateReport.equals(mFirebaseUtils.getUser().getUid())) {
                HashMap<String, Object> notification = new HashMap<>();
                notification.put(Constants.PARAM_CONTENT, Constants.COMMENT_TYPE_1);//type = 1;
                notification.put(Constants.PARAM_TYPE, 1);
                notification.put(Constants.PARAM_CREATED_TIMESTAMP, System.currentTimeMillis() / 1000);
                notification.put(Constants.PARAM_READ_FLG, false);//default false
                HashMap<String, Boolean> user = new HashMap<>();
                user.put(mFirebaseUtils.getUser().getUid(), true);
                notification.put(Constants.PARAM_USER, user);
                HashMap<String, Boolean> report = new HashMap<>();
                report.put(mKey, true);
                notification.put(Constants.PARAM_REPORTS, report);

                mFirebaseUtils.getFirebaseDatabase()
                        .getReference(Constants.PARAM_USER)
                        .child(mIdUserCreateReport)
                        .child(Constants.PARAM_NOTI_LIST)
                        .push()
                        .setValue(notification)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(">>", e.toString());
                            }
                        });
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapGoogleMap = googleMap;
    }

    @Override
    public void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position) {
        switch (view.getId()) {
            case R.id.mImageViewReport:
                Intent intent = new Intent(ReportDetailsActivity.this, DetailImageActivity.class);
                intent.putStringArrayListExtra(Constants.EXTRA_REPORT_IMAGES, mDatasImage);
                intent.putExtra(Constants.EXTRA_REPORT_POSITION, position);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}
