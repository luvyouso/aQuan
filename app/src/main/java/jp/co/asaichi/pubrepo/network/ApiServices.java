package jp.co.asaichi.pubrepo.network;

import com.google.gson.JsonObject;

import jp.co.asaichi.pubrepo.common.Constants;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ApiServices {

    @GET(Constants.API_CONFIG)
    Observable<Response<JsonObject>> getConfig();


    @GET(Constants.API_CONFIG_FIREBASE + "{name}")
    Observable<Response<JsonObject>> getConfigFirebase(@Path("name") String nameJson);

}
