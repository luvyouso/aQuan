package jp.co.asaichi.pubrepo.network;

import java.util.concurrent.TimeUnit;

import jp.co.asaichi.pubrepo.common.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by nguyentu on 9/21/17.
 */

public class PubrepoApi {

    public static ApiServices createServices() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(httpLoggingInterceptor);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .client(httpClient.build())
                .build();
        return retrofit.create(ApiServices.class);
    }
}
