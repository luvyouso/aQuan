package jp.co.asaichi.pubrepo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.co.asaichi.pubrepo.common.Constants;
import jp.co.asaichi.pubrepo.model.GoogleService;

/**
 * Created by nguyentu on 12/20/17.
 */

public class FirebaseUtils {

    private FirebaseAuth mAuthFirebase;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Context mContext;
    private ArrayList<File> mPathImage;
    private ArrayList<File> mPathImageThumbnail;
    private String mReportId;
    private EventListener mEventListener;
    private UploadImage mUploadImage;
    private ArrayList<String> mPathsThumbnail;

    public FirebaseUtils(Context context) {
        mContext = context;
        if (FirebaseApp.getApps(context).size() != 0) {
            mAuthFirebase = FirebaseAuth.getInstance();
            mAuthFirebase.setLanguageCode("ja");
            mFirebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(context));
            mStorageReference = FirebaseStorage.getInstance().getReference();
        }
    }


    /**
     * @param googleService
     */
    public void connectFirebase(GoogleService googleService) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(googleService.getApplicationId()) // Required for Analytics.
                .setApiKey(googleService.getApiKey()) // Required for Auth.
                .setDatabaseUrl(googleService.getDatabaseUrl()) // Required for RTDB.
                .setStorageBucket(googleService.getStorageBucket())
                .build();
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApplicationId("1:756940305187:android:c68a87e680fc5dcc") // Required for Analytics.
//                .setApiKey("AIzaSyCY4S9qRW2GgaUkAtKQeAk4o_pFt3Tz_f8") // Required for Auth.
//                .setDatabaseUrl("https://pubrepo-dev.firebaseio.com") // Required for RTDB.
//                .setStorageBucket("pubrepo-dev.appspot.com")
//                .build();

        // Initialize with secondary app.
        try {
            FirebaseApp.initializeApp(mContext /* Context */, options);
        } catch (Exception e) {
            try {
                FirebaseApp.initializeApp(mContext /* Context */, options, googleService.getName());
            } catch (Exception e1) {
                FirebaseApp.getInstance(googleService.getName());
            }
        }
    }

    /**
     * @param tableName
     * @param eventListener
     */
    public void getData(String tableName, EventListener eventListener) {
        mDatabaseReference = mFirebaseDatabase.getReference(tableName);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventListener.onCancelled(databaseError);
            }
        });
    }

    /**
     * @param tableName
     * @param eventListener
     */
    public void getDataAutoUpdate(String tableName, EventListener eventListener) {
        mDatabaseReference = mFirebaseDatabase.getReference(tableName);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventListener.onCancelled(databaseError);
            }
        });
    }

    /**
     * @param tableName
     * @param data
     */
    public void saveData(String tableName, Object data, String childKey) {
        mDatabaseReference = mFirebaseDatabase.getReference(tableName);
        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
//        String userId = mDatabaseReference.push().getKey();

        // pushing user to 'users' node using the userId
        if (!TextUtils.isEmpty(childKey)) {
            mDatabaseReference.child(childKey).setValue(data);
        } else {
            mDatabaseReference.push().setValue(data);
        }
    }

    /**
     * check login for firebase
     *
     * @return
     */
    public boolean isLogin() {
        FirebaseUser firebaseUser = mAuthFirebase.getCurrentUser();
        return firebaseUser != null;
    }


    /**
     * @param listPath
     */
    public void uploadImageToStorage(ArrayList<String> listPath, Long timestamp) throws IOException {
        mPathImage = new ArrayList<>();
        mPathImageThumbnail = new ArrayList<>();
        mPathsThumbnail = new ArrayList<>();
        mPathsThumbnail.clear();
        mPathImage.clear();
        mPathImageThumbnail.clear();
        int len = listPath.size();
        for (int i = 0; i < len; i++) {

            String pathFile = listPath.get(i);
            if (pathFile.contains("https://firebasestorage.googleapis.com")) {
                mPathsThumbnail.add(pathFile);
                if (mPathsThumbnail.size() == listPath.size()) {
                    mUploadImage.onData(new ArrayList<>(), mPathsThumbnail);
                }
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //Returns null, sizes are in the options variable
                BitmapFactory.decodeFile(listPath.get(i), options);
                String type = options.outMimeType;
                int quality = 100;
                if (type.equals(Constants.MIME_TYPE)) {
                    quality = 60;
                }
                //thumbnail
                String thumbnail = timestamp + "." + i + ".thumbnail";
                String imagePath = timestamp + "." + i;
                ResizerImage(Constants.TARGET_LENGTH_THUMBNAIL, quality, new File(pathFile),
                        listPath, thumbnail);
                ResizerImage(Constants.TARGET_LENGTH_DEFAULT, quality, new File(pathFile),
                        listPath, imagePath);
            }
        }
    }


    @SuppressLint("RxLeakedSubscription")
    private void ResizerImage(int targetLength, int quality, File image,
                              ArrayList<String> listPath,
                              String nameFile) {

        new Resizer(mContext)
                .setTargetLength(targetLength)
                .setQuality(quality)
                .setOutputFormat("JPEG")
                .setOutputDirPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .setSourceImage(image)
                .setOutputFilename(nameFile)
                .getResizedFileAsFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        mPathImage.add(file);
                        if (mPathImage.size() == ((listPath.size() - mPathsThumbnail.size()) * 2)) {
                            upLoadFileToFirebase();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }

    private void upLoadFileToFirebase() {
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> pathsThumbnail = new ArrayList<>();
        paths.clear();
        pathsThumbnail.clear();
        for (File file : mPathImage) {
            StorageReference photoRef = mStorageReference.child(Constants.PARAM_PHOTOS);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            photoRef.child(getReportId() + "/" + file.getName())
                    .putFile(Uri.parse("file://" + file), metadata)
                    .addOnProgressListener(taskSnapshot -> {

                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String content = downloadUrl.toString();
                        Log.e("path upload image", content);
                        if (content.length() > 0) {
                            if (content.contains("thumbnail")) {
                                pathsThumbnail.add(content);
                            } else {
                                paths.add(content);
                            }
                        }
                        if (paths.size() + pathsThumbnail.size() == (mPathImage.size())) {
                            if (mUploadImage != null) {
                                mPathsThumbnail.addAll(pathsThumbnail);
                                mUploadImage.onData(paths, mPathsThumbnail);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }


    /**
     * get user
     *
     * @return
     */
    public FirebaseUser getUser() {
        return mAuthFirebase.getCurrentUser();
    }

    public String getKey(String table) {
        mDatabaseReference = mFirebaseDatabase.getReference(table);
        return mDatabaseReference.push().getKey();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public FirebaseAuth getAuthFirebase() {
        return mAuthFirebase;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    public String getReportId() {
        return mReportId;
    }

    public void setReportId(String mReportId) {
        this.mReportId = mReportId;
    }


    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        this.mEventListener = eventListener;
    }

    public UploadImage getUploadImage() {
        return mUploadImage;
    }

    public void setUploadImage(UploadImage mUploadImage) {
        this.mUploadImage = mUploadImage;
    }

    public interface EventListener {
        void onDataChange(DataSnapshot dataSnapshot);

        void onCancelled(DatabaseError databaseError);
    }

    public interface UploadImage {
        void onData(ArrayList<String> pathList, ArrayList<String> pathThumbnails);
    }
}
