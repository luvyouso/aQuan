package jp.co.asaichi.pubrepo.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.asaichi.pubrepo.BuildConfig;
import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.adapter.AbstractAdapter;
import jp.co.asaichi.pubrepo.adapter.ReportSelectAdapter;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.common.MyAplication;
import jp.co.asaichi.pubrepo.common.PopUpDlg;
import jp.co.asaichi.pubrepo.databinding.ActivitySelectionBinding;
import jp.co.asaichi.pubrepo.model.GoogleService;
import jp.co.asaichi.pubrepo.model.GoogleServiceModel;
import jp.co.asaichi.pubrepo.network.PubrepoApi;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectionActivity extends BaseActivity implements AbstractAdapter.ListItemInteractionListener {

    private ActivitySelectionBinding mSelectionBinding;
    private ArrayList<GoogleService> mDatas;
    private ReportSelectAdapter mSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_selection);
        mSelectionBinding.mIncludeToolbar.mTextViewTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_logo_select));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectionBinding.mRecyclerViewSelect.setLayoutManager(layoutManager);
        mSelectionBinding.mRecyclerViewSelect.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mSelectionBinding.mRecyclerViewSelect.addItemDecoration(dividerItemDecoration);
        mDatas = new ArrayList<>();
        new PubrepoApi().createServices()
                .getConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response<JsonObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Response<JsonObject> jsonObjectResponse) {
                        GoogleServiceModel googleServiceModel = new Gson().fromJson(jsonObjectResponse.body().toString(), GoogleServiceModel.class);
                        mDatas.addAll(googleServiceModel.getData());
                        mSelectAdapter.notifyDataSetChanged();
                    }
                });

        mSelectAdapter = new ReportSelectAdapter(this, mDatas);
        mSelectionBinding.mRecyclerViewSelect.setAdapter(mSelectAdapter);
        mSelectAdapter.setItemInteractionListener(this);

    }

    @Override
    public void onInteraction(ViewDataBinding viewDataBinding, View view, Object model, int position) {
        GoogleService item = (GoogleService) model;
        String nameJson = item.getGoogleServiceInfoJson().concat(".json");
        new PubrepoApi().createServices()
                .getConfigFirebase(nameJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response<JsonObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Response<JsonObject> jsonObjectResponse) {
                        try {
                            JSONObject data = new JSONObject(jsonObjectResponse.body().toString());
                            JSONObject projectInfo = data.getJSONObject("project_info");
                            JSONArray client = data.getJSONArray("client");

                            item.setDatabaseUrl(projectInfo.getString("firebase_url"));
                            item.setStorageBucket(projectInfo.getString("storage_bucket"));
                            for (int i = 0; i < client.length(); i++) {
                                JSONObject itemJson = client.getJSONObject(i);
                                String packageName = itemJson.getJSONObject("client_info").getJSONObject("android_client_info").getString("package_name");
                                if (packageName.equals(BuildConfig.APPLICATION_ID)) {
                                    item.setApplicationId(itemJson.getJSONObject("client_info").getString("mobilesdk_app_id"));
                                    JSONArray oauthClient = itemJson.getJSONArray("api_key");
                                    JSONObject itemOauthClient = oauthClient.getJSONObject(0);
                                    item.setApiKey(itemOauthClient.getString("current_key"));

                                    mLocalData.saveData(Constants.SHARED_PREFERENCES_SERVER, new Gson().toJson(item));
                                    String switchAera = mLocalData.getData(Constants.SHARED_PREFERENCES_SWITCH_AERA);
                                    String message = TextUtils.isEmpty(switchAera) ? "この自治体でよろしいですか？" : "自治体を変更するにはアプリケーションを落とす必要があります。アプリケーションを終了してよろしいですか？";
                                    String negativeButton = TextUtils.isEmpty(switchAera) ? "再選択" : "キャンセル";
                                    PopUpDlg popUpDlg = new PopUpDlg(SelectionActivity.this, true);
                                    popUpDlg.show(item.getName(), message, "はい", negativeButton, (dialogInterface, x) -> {
                                        FirebaseApp.getApps(MyAplication.getInstance()).clear();
                                        mFirebaseUtils.connectFirebase(item);
                                        if (TextUtils.isEmpty(switchAera)) {
                                            Intent intent = new Intent(SelectionActivity.this, FirstPageActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        } else {
                                            finishAffinity();
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                        }
                                    }, (dialogInterface, j) -> dialogInterface.cancel());
                                    break;
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
