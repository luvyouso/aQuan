package jp.co.asaichi.pubrepo.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.common.MyAplication;
import jp.co.asaichi.pubrepo.common.PopUpDlg;
import jp.co.asaichi.pubrepo.databinding.FragmentReportMapBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.SharedPreference;
import jp.co.asaichi.pubrepo.utils.Utils;
import jp.co.asaichi.pubrepo.view.activity.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final String TAG = ReportMapFragment.class.getName();
    private final int DEFAULT_ZOOM = 14;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private GoogleMap mMapGoogleMap;
    private FragmentReportMapBinding mReportMapBinding;
    private boolean mLocationPermissionGranted;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Marker mSelectedMarker;
    private BottomSheetBehavior mBottomSheetBehavior;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    private FirebaseUtils mFirebaseUtils;
    private ArrayList<DataSnapshot> mDataSnapshots;
    private DataSnapshot mDataSnapshot;
    private ImageView mImageViewRight;
    private PopUpDlg mPopUpDlg;
    private SharedPreference mLocalData;
    private GoogleService mGoogleServiceData;

    public ReportMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportMapFragment newInstance(String param1, String param2) {
        ReportMapFragment fragment = new ReportMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mReportMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_map, container, false);
        return mReportMapBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocalData = SharedPreference.getInstance(getActivity());
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationRequest();

        buildLocationSettingsRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mFragmentMap);
        mapFragment.getMapAsync(this);
        mGoogleServiceData = new Gson().fromJson(mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER).toString(), GoogleService.class);
        mReportMapBinding.mIncludeToolbar.mTextViewTitle.setText(getText(R.string.report_map) + "@" + mGoogleServiceData.getName());
        mReportMapBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        mBottomSheetBehavior = BottomSheetBehavior.from(mReportMapBinding.layoutBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
//                        Toast.makeText(getActivity(), "show", Toast.LENGTH_LONG).show();
                        break;
                    default:
//                        Toast.makeText(getActivity(), "click", Toast.LENGTH_LONG).show();
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mPopUpDlg = new PopUpDlg(getActivity(), true);

        mReportMapBinding.layoutBottomSheet.setOnClickListener(v -> {
            if (mListener != null) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.onFragmentInteractionMap(mReportMapBinding.layoutBottomSheet, mDataSnapshot);
            }
        });
        mImageViewRight = view.findViewById(R.id.mImageViewRight);
        mImageViewRight.setVisibility(View.VISIBLE);
        mImageViewRight.setOnClickListener(v -> {
            mImageViewRight.setVisibility(View.GONE);
            if (mListener != null) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                FirebaseAuth.getInstance().setLanguageCode("ja_JP");
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (!user.isEmailVerified()) {
                            mPopUpDlg.show("", "投稿には登録されたメールアドレスを確認する必要があります。\n確認用のメールを再送しますか？",
                                    "はい", "キャンセル", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            FirebaseAuth.getInstance().setLanguageCode("ja_JP");
                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new PopUpDlg(getActivity(), true).show("エラー", "確認用メールを再送しました。", "", "閉じる", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                            }
                                                        }, null);
                                                    } else {
                                                        new PopUpDlg(getActivity(), true).show("エラー", "error", "", "閉じる", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                            }
                                                        }, null);
                                                    }
                                                }
                                            });
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                            mImageViewRight.setVisibility(View.VISIBLE);
                            return;
                        }
                        mFirebaseUtils.getFirebaseDatabase().getReference(Constants.PARAM_USER)
                                .child(mFirebaseUtils.getAuthFirebase().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshots) {
                                if (dataSnapshots.child(Constants.PARAM_NAME).getValue() == null) {
                                    PopUpDlg popUpDlg = new PopUpDlg(getActivity(), true);
                                    popUpDlg.show("エラー", "レポートを投稿する場合「プロフィール編集」で投稿者名を入力して下さい。", "", "閉じる", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    }, null);
                                    mImageViewRight.setVisibility(View.VISIBLE);
                                } else {
                                    mImageViewRight.setVisibility(View.GONE);
                                    mListener.onFragmentInteractionMap(mImageViewRight, null);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
        mFirebaseUtils = new FirebaseUtils(getActivity());
        mDataSnapshots = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.hideSoftKeyboard(getActivity());
        if (mImageViewRight.getVisibility() == View.GONE) {
            mImageViewRight.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMapGoogleMap = googleMap;
        // Prompt the user for permission.
        getLocationPermission();

    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");
                    // Get the current location of the device and set the position of the map.
                    getDeviceLocation();
                })
                .addOnFailureListener(getActivity(), e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                    }
                });
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public void getDeviceLocation() {
        GoogleService googleService = new Gson().fromJson(mLocalData.getData(Constants.SHARED_PREFERENCES_SERVER), GoogleService.class);
        LatLng latLng = new LatLng(googleService.getDefaultLatitude(), googleService.getDefaultLongitude());
        mMapGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        mFirebaseUtils.getDataAutoUpdate(Constants.PARAM_REPORTS, new FirebaseUtils.EventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshots.clear();
                for (DataSnapshot field : dataSnapshot.getChildren()) {
                    Long active = field.child(Constants.PARAM_ACTIVE).getValue() == null ? 0 : (Long) field.child(Constants.PARAM_ACTIVE).getValue();
                    Long type = (Long) field.child(Constants.PARAM_TYPE).getValue();
                    Long status = (Long) field.child(Constants.PARAM_STATUS).getValue();
                    if (active == 1) {
                        if (status == 1 || status == 0) {
                            continue;
                        }
                        mDataSnapshots.add(field);
                        // Add a default marker, because the user hasn't selected a place.

                        int markerImage = R.drawable.icon_nodification;

                        if (type == 1) {
                            if (status == 2) {
                                markerImage = R.drawable.repair_red_copy;
                            } else if (status == 3) {
                                markerImage = R.drawable.repair_yellow_copy;
                            } else {
                                markerImage = R.drawable.repair_green_copy;
                            }
                        } else if (type == 2) {

                            if (status == 2) {
                                markerImage = R.drawable.withdraw_red_copy;
                            } else if (status == 3) {
                                markerImage = R.drawable.withdraw_yellow_copy;
                            } else {
                                markerImage = R.drawable.withdraw_green_copy;
                            }
                        } else if (type == 3) {
                            if (status == 2) {
                                markerImage = R.drawable.prune_red_copy;
                            } else if (status == 3) {
                                markerImage = R.drawable.prune_yellow_copy;
                            } else {
                                markerImage = R.drawable.prune_green_copy;
                            }
                        } else if (type == 4) {
                            if (status == 2) {
                                markerImage = R.drawable.others_red_copy;
                            } else if (status == 3) {
                                markerImage = R.drawable.others_yellow_copy;
                            } else {
                                markerImage = R.drawable.others_green_copy;
                            }
                        }
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng((Double) field.child(Constants.PARAM_LATITUDE).getValue(),
                                        (Double) field.child(Constants.PARAM_LONGITUDE).getValue()))
                                .icon(BitmapDescriptorFactory.fromResource(markerImage));
                        Marker marker1 = mMapGoogleMap.addMarker(marker);
                        marker1.setTag(field.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMapGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {

                mMapGoogleMap.setMyLocationEnabled(true);
                mMapGoogleMap.setOnMapClickListener(this);
                mMapGoogleMap.setOnMarkerClickListener(this);

                mMapGoogleMap.setMyLocationEnabled(true);
                mMapGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Kick off the process of building the LocationCallback, LocationRequest, and
                // LocationSettingsRequest objects.
                createLocationRequest();

                buildLocationSettingsRequest();

                startLocationUpdates();

            } else {
                mMapGoogleMap.setMyLocationEnabled(true);
                mMapGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (null != mSelectedMarker) {
//            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_nodification));
        }
        mSelectedMarker = null;
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (DataSnapshot dataSnapshot : mDataSnapshots) {
            if (dataSnapshot.getKey().equals(marker.getTag())) {
                mDataSnapshot = dataSnapshot;
                //image
                for (DataSnapshot image : dataSnapshot.child(Constants.PARAM_IMAGES).getChildren()) {
                    Glide.with(this)
                            .load(image.child(Constants.PARAM_THUMB_URL).getValue())
                            .apply(RequestOptions
                                    .centerCropTransform()
                                    .error(R.drawable.icon_example))
                            .into(mReportMapBinding.mImageViewReport);
                    break;
                }

                //date
                mReportMapBinding.mTextViewDate.setText(Utils.dateFromMillisecond(Constants.DEFAULT_DATE_FORMAT, (Long) dataSnapshot.child(Constants.PARAM_CREATED_TIMESTAMP).getValue()));
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
                                    mReportMapBinding.mTextViewUser.setText(
                                            dataSnapshots == null || dataSnapshots.getValue() == null ? "" :
                                                    dataSnapshots.getValue().toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                //address
//                mReportMapBinding.mTextViewAddress.setText(dataSnapshot.child(Constants.PARAM_ADDRESS).getValue().toString());
                //title
                mReportMapBinding.mTextViewTitle.setText(dataSnapshot.child(Constants.PARAM_TITLE).getValue().toString());
                //status
                if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 2) {
                    mReportMapBinding.mTextViewStatus.setText(Constants.STATUS_2_WAITING);
                    Glide.with(MyAplication.getInstance())
                            .load(R.drawable.icon_pin_red)
                            .apply(RequestOptions
                                    .centerInsideTransform()
                                    .error(R.drawable.icon_error))
                            .into(mReportMapBinding.mImageViewStatus);

                } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 3) {
                    mReportMapBinding.mTextViewStatus.setText(Constants.STATUS_3_DURING);
                    Glide.with(MyAplication.getInstance())
                            .load(R.drawable.icon_pin_yellow)
                            .apply(RequestOptions
                                    .centerInsideTransform()
                                    .error(R.drawable.icon_error))
                            .into(mReportMapBinding.mImageViewStatus);
                } else if (Integer.parseInt(dataSnapshot.child(Constants.PARAM_STATUS).getValue().toString()) == 4) {
                    mReportMapBinding.mTextViewStatus.setText(Constants.STATUS_4_CORRESPONDING);
                    Glide.with(MyAplication.getInstance())
                            .load(R.drawable.icon_pin_green)
                            .apply(RequestOptions
                                    .centerInsideTransform()
                                    .error(R.drawable.icon_error))
                            .into(mReportMapBinding.mImageViewStatus);
                } else {
                    mReportMapBinding.mTextViewStatus.setText(Constants.STATUS + Constants.STATUS_1_UNRECOGNIZED);
                    Glide.with(MyAplication.getInstance())
                            .load(R.drawable.icon_noti_gray)
                            .apply(RequestOptions
                                    .centerInsideTransform()
                                    .error(R.drawable.icon_error))
                            .into(mReportMapBinding.mImageViewStatus);
                }
                //type
                int type = Integer.parseInt(dataSnapshot.child(Constants.PARAM_TYPE).getValue().toString());
//                Long status = (Long) dataSnapshot.child(Constants.PARAM_STATUS).getValue();
                switch (type) {
                    case 1:
                        Glide.with(this)
                                .load(R.drawable.icon_type_repair)
                                .apply(RequestOptions
                                        .centerInsideTransform()
                                        .error(R.drawable.icon_example))
                                .into(mReportMapBinding.mImageViewType);
                        mReportMapBinding.mTextViewType.setText(getText(R.string.repair));
                        break;
                    case 2:
                        Glide.with(this)
                                .load(R.drawable.icon_type_withdraw)
                                .apply(RequestOptions
                                        .centerInsideTransform()
                                        .error(R.drawable.icon_example))
                                .into(mReportMapBinding.mImageViewType);
                        mReportMapBinding.mTextViewType.setText(getText(R.string.remove));
                        break;
                    case 3:
                        Glide.with(this)
                                .load(R.drawable.icon_type_prune)
                                .apply(RequestOptions
                                        .centerInsideTransform()
                                        .error(R.drawable.icon_example))
                                .into(mReportMapBinding.mImageViewType);
                        mReportMapBinding.mTextViewType.setText(getText(R.string.pruning));
                        break;
                    case 4:
                        Glide.with(this)
                                .load(R.drawable.icon_type_others)
                                .apply(RequestOptions
                                        .centerInsideTransform()
                                        .error(R.drawable.icon_example))
                                .into(mReportMapBinding.mImageViewType);
                        mReportMapBinding.mTextViewType.setText(getText(R.string.others));
                        break;
                    default:
                        break;
                }

//                if (status == 2) {
//                    mReportMapBinding.mImageViewType.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red_validate));
//                } else if (status == 3) {
//                    mReportMapBinding.mImageViewType.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow));
//                } else {
//                    mReportMapBinding.mImageViewType.setColorFilter(ContextCompat.getColor(getActivity(), R.color.green_haze));
//                }

                //like
                if (dataSnapshot.child(Constants.PARAM_LIKES).getValue() != null) {
                    HashMap<String, Boolean> likes = (HashMap<String, Boolean>) dataSnapshot.child(Constants.PARAM_LIKES).getValue();
                    int countLike = 0;
                    for (String key : likes.keySet()) {
                        if (likes.get(key)) {
                            countLike++;
                        }
                    }
                    mReportMapBinding.mTextViewLike.setText(countLike + "");
                } else {
                    mReportMapBinding.mTextViewLike.setText("0");
                }
                break;
            }
        }
        if (null != mSelectedMarker) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_nodification));
        }
        mSelectedMarker = marker;
//        mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_nodification));
        return false;
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

        void onFragmentInteractionMap(View view, DataSnapshot dataSnapshot);
    }
}
