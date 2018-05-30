package jp.co.asaichi.pubrepo.common;

/**
 * Created by nguyentu on 9/15/17.
 */

public class MyAplication extends android.support.multidex.MultiDexApplication {

    public static MyAplication mApplication;

    public static MyAplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        // Manually configure Firebase Options
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApplicationId("1:815127893987:android:c68a87e680fc5dcc") // Required for Analytics.
//                .setApiKey("AIzaSyCuCALWs8FcDBYwkNA36p_BuMc4iBY4CHQ") // Required for Auth.
//                .setDatabaseUrl("https://my-2018.firebaseio.com/") // Required for RTDB.
//                .build();
//
//        // Initialize with secondary app.
//        FirebaseApp.initializeApp(this /* Context */, options);

        mApplication = this;
    }


}
