package jp.co.asaichi.pubrepo.view.custom;

/**
 * Created by nguyentu on 6/8/17.
 */

public interface BaseView {

    void onStartApi();

    void onNextApi(String api, Object jsonObject);

    void onErrorApi(String api, Object e);

    void onFinishApi();

}
