package jp.co.asaichi.pubrepo.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import jp.co.asaichi.pubrepo.BuildConfig;
import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.databinding.FragmentMenuBinding;
import jp.co.asaichi.pubrepo.utils.FirebaseUtils;
import jp.co.asaichi.pubrepo.utils.SharedPreference;
import jp.co.asaichi.pubrepo.view.activity.LoginActivity;
import jp.co.asaichi.pubrepo.view.activity.SelectionActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentMenuBinding mMenuBinding;
    private FirebaseUtils mFirebaseUtils;
    private SharedPreference mLocalData;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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
        mMenuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        return mMenuBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseUtils = new FirebaseUtils(getActivity());
        mLocalData = SharedPreference.getInstance(getActivity());
        mMenuBinding.mIncludeToolbar.mTextViewTitle.setText(R.string.menu);
        mMenuBinding.mIncludeToolbar.mTextViewTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        mMenuBinding.mItemMenuNotification.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuNotification);
            }
        });
        mMenuBinding.mItemMenuEditProfile.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuEditProfile);
            }
        });
        mMenuBinding.mItemMenuChangePassword.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuChangePassword);
            }
        });
        mMenuBinding.mItemMenuHistory.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuHistory);
            }
        });
        mMenuBinding.mItemMenuQuestion.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuQuestion);
            }
        });

        mMenuBinding.mItemMenuUserQuide.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuUserQuide);
            }
        });
        mMenuBinding.mItemMenuTermOfService.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuTermOfService);
            }
        });
        mMenuBinding.mItemMenuNote.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuNote);
            }
        });

        mMenuBinding.mItemMenuUnsubscribe.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuUnsubscribe);
            }
        });

        mMenuBinding.mItemMenuNew.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuNew);
            }
        });

        mMenuBinding.mItemMenuPrivacy.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteractionMenu(mMenuBinding.mItemMenuPrivacy);
            }
        });

        mMenuBinding.mTextViewLogout.setOnClickListener(v -> {
            if (mFirebaseUtils.isLogin()) {
                mFirebaseUtils.logout();
                onResume();
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        if (mFirebaseUtils.isLogin()) {
            mFirebaseUtils.getFirebaseDatabase()
                    .getReference(Constants.PARAM_USER)
                    .child(mFirebaseUtils.getUser().getUid())
                    .child(Constants.PARAM_NOTI_LIST)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                boolean isRead = (boolean) item.child(Constants.PARAM_READ_FLG).getValue();
                                if (!isRead) {
                                    count++;
                                }
                            }
                            if (count > 0) {
                                mMenuBinding.mItemMenuNotification.getLayoutItemMenuBinding().mTextViewNotification.setVisibility(View.VISIBLE);
                                mMenuBinding.mItemMenuNotification.getLayoutItemMenuBinding().mTextViewNotification.setText(count + "");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        mMenuBinding.mTextViewSwitchArea.setOnClickListener(v -> {
            SharedPreference.getInstance(getActivity()).removeAllData();
            FirebaseAuth.getInstance().signOut();
            mLocalData.saveData(Constants.SHARED_PREFERENCES_SWITCH_AERA, Constants.SHARED_PREFERENCES_SWITCH_AERA);
            Intent intent = new Intent(getActivity(), SelectionActivity.class);
            startActivity(intent);
            getActivity().finishAffinity();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mFirebaseUtils.isLogin()) {
            mMenuBinding.mItemMenuNotification.setVisibility(View.GONE);
            mMenuBinding.mItemMenuEditProfile.setVisibility(View.GONE);
            mMenuBinding.mItemMenuChangePassword.setVisibility(View.GONE);
            mMenuBinding.mItemMenuHistory.setVisibility(View.GONE);
            mMenuBinding.mItemMenuUnsubscribe.setVisibility(View.GONE);
            mMenuBinding.mItemMenuNew.setVisibility(View.VISIBLE);
            mMenuBinding.mTextViewLogout.setText(getText(R.string.login));
            mMenuBinding.mTextViewLogout.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
        } else {
            mMenuBinding.mItemMenuNotification.setVisibility(View.VISIBLE);
            mMenuBinding.mItemMenuEditProfile.setVisibility(View.VISIBLE);
            mMenuBinding.mItemMenuChangePassword.setVisibility(View.VISIBLE);
            mMenuBinding.mItemMenuHistory.setVisibility(View.VISIBLE);
            mMenuBinding.mItemMenuUnsubscribe.setVisibility(View.VISIBLE);
            mMenuBinding.mItemMenuNew.setVisibility(View.GONE);
            mMenuBinding.mTextViewLogout.setText(getText(R.string.logout));
            mMenuBinding.mTextViewLogout.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        }
        updateLayoutVersionAndArea();

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

    private void updateLayoutVersionAndArea() {
        if (mFirebaseUtils.isLogin()) {
            mMenuBinding.mLinearLayoutVersion.setVisibility(View.GONE);
            mMenuBinding.viewGraySwitchArea.setVisibility(View.GONE);
            mMenuBinding.mTextViewSwitchArea.setVisibility(View.GONE);

        } else {
            mMenuBinding.viewGraySwitchArea.setVisibility(View.VISIBLE);
            mMenuBinding.mTextViewSwitchArea.setVisibility(View.VISIBLE);
            mMenuBinding.mLinearLayoutVersion.setVisibility(View.VISIBLE);
            mMenuBinding.mTextViewVersion.setText("バージョン: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
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

        void onFragmentInteractionMenu(View view);
    }
}
