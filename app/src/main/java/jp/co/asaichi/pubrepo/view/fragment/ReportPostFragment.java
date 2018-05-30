package jp.co.asaichi.pubrepo.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.AbstractAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportDetailAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.FragmentPostReportBinding;
import jp.co.asaichi.pubrepo.databinding.LayoutFooterBinding;
import jp.co.asaichi.pubrepo.databinding.LayoutItemReportDetailBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;
import jp.co.asaichi.pubrepo.utils.FetchAddressIntentService;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.SharedPreference;
import jp.co.asaichi.pubrepo.utils.Utils;
import jp.co.asaichi.pubrepo.view.activity.SelectTypeActivity;
import jp.co.asaichi.pubrepo.view.activity.WebActivity;
import jp.co.asaichi.pubrepo.view.custom.WorkaroundMapFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportPostFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, AbstractAdapter.ListItemInteractionListener {

    public static final int REQUEST_CODE_SELECT_TYPE_ACTIVITY = 333;
    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    public static final int REQUEST_PLACE_PICKER = 666;
    private final String GUIDE_PAGE = "ご利用ガイドのページから、よくあるケースをご確認いただけます。";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private final String TAG = ReportPostFragment.class.getName();
    // TODO: Rename and change types of parameters
    private String mParamKey;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private FragmentPostReportBinding mPostReportBinding;
    private ReportDetailAdapter mReportDetailAdapter;
    private ArrayList<String> mDatas;
    private int mReportType = 1; //default
    //map
    private GoogleMap mGoogleMap;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested = true;

    /**
     * The formatted location address.
     */
    private String mAddressOutput = "";

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private FirebaseUtils mFirebaseUtils;
    private ArrayList<String> mImageList;
    private Long mCreatedTimestamp = null;
    private boolean isEdit = true;
    private DataSnapshot mDataSnapshot;
    private SharedPreference mLocalData;

    public ReportPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ReportPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportPostFragment newInstance(String param1) {
        ReportPostFragment fragment = new ReportPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamKey = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPostReportBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_report, container, false);
        return mPostReportBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseUtils = new FirebaseUtils(getActivity());
        mPostReportBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.report_post);
        mPostReportBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        mPostReportBinding.mIncludeToolbar.mTextViewBack.setText(R.string.cancel);
        mPostReportBinding.mIncludeToolbar.mTextViewBack.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        mPostReportBinding.mIncludeToolbar.mTextViewRight.setText(R.string.post);
        mPostReportBinding.mIncludeToolbar.mTextViewRight.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        mPostReportBinding.mIncludeToolbar.mImageViewBack.setVisibility(View.GONE);
        mPostReportBinding.mIncludeToolbar.mLinearLayoutBack.setOnClickListener(v -> {
            getActivity().finish();
        });
        if (TextUtils.isEmpty(mParamKey)) {
            isEdit = false;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPostReportBinding.mRecyclerViewReportPost.setLayoutManager(layoutManager);
        mPostReportBinding.mRecyclerViewReportPost.setHasFixedSize(true);
        mDatas = new ArrayList<>();
        mImageList = new ArrayList<>();
        mReportDetailAdapter = new ReportDetailAdapter(getActivity(), mDatas, true);
        mPostReportBinding.mRecyclerViewReportPost.setAdapter(mReportDetailAdapter);
        mReportDetailAdapter.setItemInteractionListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mFragmentMap);
        mapFragment.getMapAsync(this);

        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.mFragmentMap))
                .setListener(() -> mPostReportBinding.mScrollViewReport.requestDisallowInterceptTouchEvent(true));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            if (TextUtils.isEmpty(mParamKey)) {
                getAddress();
            }
        }
        mResultReceiver = new AddressResultReceiver(new Handler());
        //type
        mPostReportBinding.mButtonChangeType.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectTypeActivity.class);
            intent.putExtra(Constants.EXTRA_REPORT_TYPE, mReportType);
            getActivity().startActivityForResult(intent, REQUEST_CODE_SELECT_TYPE_ACTIVITY);

        });
        //title
//        mPostReportBinding.mTextViewReportTitle.setOnClickListener(v -> {
//            mPostReportBinding.mLinearLayoutErrorTitle.setVisibility(View.GONE);
//            Intent intent = new Intent(getActivity(), ReportTitleActivity.class);
//            intent.putExtra(Constants.EXTRA_REPORT_TITILE, mPostReportBinding.mTextViewReportTitle.getText().toString().trim());
//            getActivity().startActivityForResult(intent, REQUEST_CODE_REPORT_TITLE_ACTIVITY);
//        });
        //Content
//        mPostReportBinding.mTextViewReportContent.setOnClickListener(v -> {
//            mPostReportBinding.mLinearLayoutErrorContent.setVisibility(View.GONE);
//            Intent intent = new Intent(getActivity(), ReportContentActivity.class);
//            intent.putExtra(Constants.EXTRA_REPORT_CONTENT, mPostReportBinding.mTextViewReportContent.getText().toString().trim());
//            getActivity().startActivityForResult(intent, REQUEST_CODE_REPORT_CONTENT_ACTIVITY);
//        });

        //save
        mPostReportBinding.mIncludeToolbar.mTextViewRight.setOnClickListener(v -> {
            boolean isCheck = true;
            if (TextUtils.isEmpty(mPostReportBinding.mTextViewReportTitle.getText().toString().trim())) {
                mPostReportBinding.mLinearLayoutErrorTitle.setVisibility(View.VISIBLE);
                isCheck = false;
            }
            if (TextUtils.isEmpty(mPostReportBinding.mTextViewReportContent.getText().toString().trim())) {
                mPostReportBinding.mLinearLayoutErrorContent.setVisibility(View.VISIBLE);
                isCheck = false;
            }
            if (mDatas.size() == 0) {
                mPostReportBinding.mLinearLayoutErrorImage.setVisibility(View.VISIBLE);
                mPostReportBinding.mLinearLayoutReflection.setVisibility(View.VISIBLE);
                isCheck = false;
            }
            if (isCheck) {
                mPostReportBinding.mProgressBarDialog.setVisibility(View.VISIBLE);
                try {
                    Long time = System.currentTimeMillis() / 1000;
                    String apiKey = mFirebaseUtils.getKey(Constants.PARAM_REPORTS);
                    if (TextUtils.isEmpty(mParamKey)) {
                        mParamKey = apiKey;
                    }
                    mFirebaseUtils.setReportId(mParamKey);
                    mFirebaseUtils.setUploadImage(new FirebaseUtils.UploadImage() {

                        @Override
                        public void onData(ArrayList<String> pathList, ArrayList<String> pathThumbnails) {
                            Map<String, Object> reportMap = new HashMap<>();
                            reportMap.put(Constants.PARAM_ADDRESS, mAddressOutput);
                            reportMap.put(Constants.PARAM_CONTENT, mPostReportBinding.mTextViewReportContent.getText().toString().trim());
                            reportMap.put(Constants.PARAM_TITLE, mPostReportBinding.mTextViewReportTitle.getText().toString().trim());
                            reportMap.put(Constants.PARAM_CREATED_TIMESTAMP, mCreatedTimestamp == null ? time : mCreatedTimestamp);
                            reportMap.put(Constants.PARAM_UPDATED_TIMESTAMP, time);
                            HashMap<String, Boolean> userId = new HashMap<>();
                            userId.put(mFirebaseUtils.getUser().getUid() + "", true);
                            reportMap.put(Constants.PARAM_CREATED_USER, userId);

                            reportMap.put(Constants.PARAM_LATITUDE, mLastLocation.getLatitude());
                            reportMap.put(Constants.PARAM_LONGITUDE, mLastLocation.getLongitude());
                            reportMap.put(Constants.PARAM_TYPE, mReportType);

                            if (!isEdit) {
                                reportMap.put(Constants.PARAM_STATUS, 1);
                                reportMap.put(Constants.PARAM_ACTIVE, 1);
                            }
                            for (String s : mImageList) {
                                pathList.add(s);
                            }
                            if (mDatas.size() > 0) {
                                Collections.sort(pathList);
                            }
                            if (pathThumbnails.size() > 0) {
                                Collections.sort(pathThumbnails);
                            }

                            HashMap<String, Object> imageMap = new HashMap<>();
                            for (int i = 0; i < pathThumbnails.size(); i++) {
                                HashMap<String, Object> itemMap = new HashMap<>();
                                String contributor = "user";
                                Long status = Long.valueOf(1);
                                Long time = System.currentTimeMillis() / 1000;
                                if (isEdit) {
                                    for (DataSnapshot item : mDataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {
                                        String url = item.child(Constants.PARAM_URL).getValue().toString();
                                        if (url.equals(pathList.get(i))) {
                                            contributor = item.child(Constants.PARAM_CONTRIBUTOR).getValue().toString();
                                            status = (Long) item.child(Constants.PARAM_STATUS).getValue();
                                            time = (Long) item.child(Constants.PARAM_CREATED_TIMESTAMP).getValue();
                                        }
                                    }
                                }

                                itemMap.put(Constants.PARAM_CONTRIBUTOR, contributor);//TODO hashcode
                                itemMap.put(Constants.PARAM_CREATED_TIMESTAMP, time);
                                itemMap.put(Constants.PARAM_THUMB_URL, pathThumbnails.get(i));
                                itemMap.put(Constants.PARAM_URL, pathList.get(i));
                                itemMap.put(Constants.PARAM_STATUS, status);

                                imageMap.put(mFirebaseUtils.getKey(Constants.PARAM_REPORTS), itemMap);
                            }
                            reportMap.put(Constants.PARAM_IMAGES, imageMap);
//                            mFirebaseUtils.saveData(Constants.PARAM_REPORTS, reportMap, mParamKey);
                            mFirebaseUtils.getFirebaseDatabase()
                                    .getReference(Constants.PARAM_REPORTS)
                                    .child(mParamKey)
                                    .updateChildren(reportMap);
                            mPostReportBinding.mProgressBarDialog.setVisibility(View.GONE);

                            //update reports of user create
                            HashMap<String, Object> reports = new HashMap<>();
                            reports.put(mParamKey, true);
                            mFirebaseUtils.getFirebaseDatabase()
                                    .getReference(Constants.PARAM_USER)
                                    .child(mFirebaseUtils.getUser().getUid())
                                    .child(Constants.PARAM_REPORTS)
                                    .updateChildren(reports);

                            if (mListener != null) {
                                mListener.onFinishApi();
                            }
                        }
                    });
                    mFirebaseUtils.uploadImageToStorage(mDatas, time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mPostReportBinding.mButtonReflectLocationMap.setOnClickListener(v -> {
                getAddress();
        });

        //edit
        editReport();

        //default
        updateType(1);

        mLocalData = SharedPreference.getInstance(getActivity());
        SpannableString term = new SpannableString(GUIDE_PAGE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                GoogleService googleService = new Gson().fromJson(mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER), GoogleService.class);
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_WEB, googleService.getGuidUrl());
                intent.putExtra(Constants.EXTRA_REPORT_TITILE, getText(R.string.user_guide));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(getActivity(), R.color.cerulean));
            }
        };
        term.setSpan(clickableSpan, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPostReportBinding.mTextViewWebView.setMovementMethod(LinkMovementMethod.getInstance());
        mPostReportBinding.mTextViewWebView.setHighlightColor(Color.TRANSPARENT);
        mPostReportBinding.mTextViewWebView.setText(term);

    }

    private void editReport() {
        if (TextUtils.isEmpty(mParamKey)) {
            return;
        }
        mFirebaseUtils.getFirebaseDatabase()
                .getReference(Constants.PARAM_REPORTS)
                .child(mParamKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDataSnapshot = dataSnapshot;
                        //type
                        int type = Integer.parseInt(dataSnapshot.child(Constants.PARAM_TYPE).getValue().toString());
                        updateType(type);
                        //created_timestamp
                        mCreatedTimestamp = Long.valueOf(dataSnapshot.child(Constants.PARAM_CREATED_TIMESTAMP).getValue().toString());
                        //title
                        mPostReportBinding.mTextViewReportTitle.setText(dataSnapshot.child(Constants.PARAM_TITLE).getValue().toString());
                        //content
                        mPostReportBinding.mTextViewReportContent.setText(dataSnapshot.child(Constants.PARAM_CONTENT).getValue().toString());
                        //image
                        for (DataSnapshot image : dataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {//TODO logic show
                            mDatas.add(image.child(Constants.PARAM_THUMB_URL).getValue().toString());
                            mImageList.add(image.child(Constants.PARAM_URL).getValue().toString());
                        }
                        mReportDetailAdapter.notifyDataSetChanged();
                        //address
//                        mPostReportBinding.mTextViewAddress.setText(dataSnapshot.child(Constants.PARAM_ADDRESS).getValue().toString());
                        //map
                        // Add a default marker, because the user hasn't selected a place.
                        mGoogleMap.clear();
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng((Double) dataSnapshot.child(Constants.PARAM_LATITUDE).getValue(),
                                        (Double) dataSnapshot.child(Constants.PARAM_LONGITUDE).getValue()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shape_copy));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng((Double) dataSnapshot.child(Constants.PARAM_LATITUDE).getValue(),
                                        (Double) dataSnapshot.child(Constants.PARAM_LONGITUDE).getValue()), 15));
                        mGoogleMap.addMarker(marker);

                        mLastLocation = new Location("pubrepo");
                        mLastLocation.setLongitude((Double) dataSnapshot.child(Constants.PARAM_LONGITUDE).getValue());
                        mLastLocation.setLatitude((Double) dataSnapshot.child(Constants.PARAM_LATITUDE).getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.hideSoftKeyboard(getActivity());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * @param listPath
     */
    public void updateImage(ArrayList<String> listPath) {
        mDatas.clear();
        for (String s : listPath) {
            mDatas.add(s);
        }
        mReportDetailAdapter.notifyDataSetChanged();
    }

    /**
     * @param type
     */
    public void updateType(int type) {
        mReportType = type;
        switch (type) {
            case 1:
                mPostReportBinding.mImageViewReportType.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_type_repair));
                mPostReportBinding.mTextViewReportType.setText(R.string.repair);
                break;
            case 2:
                mPostReportBinding.mImageViewReportType.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_type_withdraw));
                mPostReportBinding.mTextViewReportType.setText(R.string.remove);
                break;
            case 3:
                mPostReportBinding.mImageViewReportType.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_type_prune));
                mPostReportBinding.mTextViewReportType.setText(R.string.pruning);
                break;
            case 4:
                mPostReportBinding.mImageViewReportType.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_type_others));
                mPostReportBinding.mTextViewReportType.setText(R.string.others);
                break;
            default:
                break;
        }

    }

    /**
     * @param title
     */
    public void updateTitle(String title) {
        mPostReportBinding.mTextViewReportTitle.setText(title);
    }

    /**
     * @param content
     */
    public void updateContent(String content) {
        mPostReportBinding.mTextViewReportContent.setText(content);
    }

    /**
     * @param place
     */
    public void updateMap(Place place) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            MarkerOptions marker = new MarkerOptions()
                    .position(place.getLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shape_copy));
            mGoogleMap.addMarker(marker);
//            mPostReportBinding.mTextViewAddress.setText(place.getAddress());
            mLastLocation = new Location("pubrepo");
            mLastLocation.setLongitude(place.getLatLng().longitude);
            mLastLocation.setLatitude(place.getLatLng().latitude);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mGoogleMap.clear();
        mLastLocation = new Location("pubrepo");
        mLastLocation.setLongitude(latLng.longitude);
        mLastLocation.setLatitude(latLng.latitude);
        startIntentService(mLastLocation);
        // Move the camera instantly to Sydney with a zoom of 15.
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shape_copy));
        mGoogleMap.addMarker(marker);
    }


    /** MAP**/

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mGoogleMap.clear();
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }
                        mLastLocation = location;
                        LatLng SYDNEY = new LatLng(location.getLatitude(), location.getLongitude());
                        // Move the camera instantly to Sydney with a zoom of 15.
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shape_copy));
                        mGoogleMap.addMarker(marker);
                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            return;
                        }
                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService(mLastLocation);
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }


    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position) {
        if (viewDataBinding instanceof LayoutFooterBinding) {
            if (mListener != null) {
                mPostReportBinding.mLinearLayoutErrorImage.setVisibility(View.GONE);
                mPostReportBinding.mLinearLayoutReflection.setVisibility(View.GONE);
                mListener.onFragmentInteractionReportPostSelectImage(mDatas);
            }
        } else if (viewDataBinding instanceof LayoutItemReportDetailBinding) {
            String path = (String) model;
            mDatas.remove(position);
            mReportDetailAdapter.notifyDataSetChanged();
            if (!path.contains("https://firebasestorage.googleapis.com")) return;
            for (String s : mImageList) {
                String nameImage = URLUtil.guessFileName(s, null, null);
                String[] params = nameImage.split(Pattern.quote("."));
                if (params[1].equals(URLUtil.guessFileName(path, null, null).split(Pattern.quote("."))[1])) {
                    mImageList.remove(s);
                    break;
                }
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteractionReportPostSelectImage(ArrayList<String> datas);

        void onFinishApi();
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            mPostReportBinding.mTextViewAddress.setText(mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }

}
