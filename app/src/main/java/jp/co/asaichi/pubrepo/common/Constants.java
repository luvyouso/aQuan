package jp.co.asaichi.pubrepo.common;


public class Constants {

    public static final String BASE_URL = "https://pubrepo-conf.firebaseapp.com/";


    // Http response status code
    public static final int HTTP_SUCCESS_CODE = 200;
    public static final int HTTP_BAD_REQUEST_CODE = 400;
    public static final int HTTP_UNAUTHORIZED_CODE = 401;
    public static final int HTTP_FORBIDDEN_CODE = 403;
    public static final int HTTP_NOT_FOUND_CODE = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR_CODE = 500;
    public static final int HTTP_SERVICE_UNAVAILABLE_CODE = 503;
    public static final int HTTP_GATEWAY_TIMEOUT_CODE = 504;

    //message
    public static final String HTTP_BAD_REQUEST = "パース不可能なリクエストボディが来た場合に応答";
    public static final String HTTP_UNAUTHORIZED = "認証がされていない、もしくは不正なトークンの場合に応答";
    public static final String HTTP_FORBIDDEN = "認証はされているが、認可されていないリソースへのリクエストに応答";
    public static final String HTTP_NOT_FOUND = "存在しないリソースへのリクエストに応答";
    public static final String HTTP_INTERNAL_SERVER_ERROR = "サーバ内部にエラーが発生した場合に応答";
    public static final String HTTP_SERVICE_UNAVAILABLE = "サービスが一時的に過負荷やメンテナンスで使用不可能である場合に応答";
    public static final String HTTP_GATEWAY_TIMEOUT = "サーバからの適切なレスポンスがなくタイムアウトした場合に応答";

    // KEYs
    public static final String SHARED_PREFERENCES_KEY = "pubrepo_shared_preference_key";
    public static final String SHARED_PREFERENCES_CATEGORY_LIST = "pubrepo_shared_preference_category_list";
    public static final String SHARED_PREFERENCES_USER_LOGIN_SUCCESSFUL = "pubrepo_shared_preference_user_login_successful";
    public static final String SHARED_PREFERENCES_CURRENT_ITEM_ACCOUNT = "pubrepo_shared_preference_current_item_account";
    public static final String SHARED_PREFERENCES_SETTING_APP = "pubrepo_shared_preference_setting_app";
    public static final String SHARED_PREFERENCES_SERVER = "shared_preferences_server";
    public static final String SHARED_PREFERENCES_SWITCH_AERA = "SHARED_PREFERENCES_switch_aera";

    //putExtra
    public static final String EXTRA_TYPE_WEB = "pubrepo_extra_type_web";
    public static final String EXTRA_REPORT_DETAIL = "pubrepo_extra_report_detail";
    public static final String EXTRA_REPORT_TYPE = "pubrepo_extra_report_type";
    public static final String EXTRA_REPORT_TITILE = "pubrepo_extra_report_title";
    public static final String EXTRA_REPORT_CONTENT = "pubrepo_extra_report_content";
    public static final String EXTRA_REPORT_EDIT = "pubrepo_extra_report_edit";
    public static final String EXTRA_REPORT_IMAGES = "pubrepo_extra_report_image";
    public static final String EXTRA_REPORT_POSITION = "pubrepo_extra_report_position";

    //API
    public static final String API_CONFIG = "config/pubrepo.json";
    public static final String API_CONFIG_FIREBASE = "config/firebase/json/";

    // API Parameter
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_DATA = "data";

    public static final String PARAM_PAGE = "page";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_NEW = "news";
    public static final String PARAM_USER = "users";
    public static final String PARAM_DEVICE_TOKEN = "device_token";
    public static final String PARAM_REPORTS = "reports";
    public static final String PARAM_PHOTOS = "images";
    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_ADDRESS = "address";
    public static final String PARAM_CREATED_TIMESTAMP = "created_timestamp";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_CREATED_USER = "created_user";
    public static final String PARAM_DELETED_FLG = "deleted_flg";
    public static final String PARAM_COMMENTS = "comments";
    public static final String PARAM_CREATED_USER_NAME = "created_user_name";
    public static final String PARAM_LIKES = "likes";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_FACEBOOK_ID = "facebook_id";
    public static final String PARAM_NOTI_LIST = "noti_list";
    public static final String PARAM_LIKE_NUM = "like_num";
    public static final String PARAM_UPDATED_TIMESTAMP = "updated_timestamp";
    public static final String PARAM_READ_FLG = "read_flg";
    public static final String PARAM_THUMB_URL = "thumb_url";
    public static final String PARAM_URL = "url";
    public static final String PARAM_CONTRIBUTOR = "contributor";
    public static final String PARAM_ACTIVE = "active";
    public static final String PARAM_ADMIN_USERS = "admin-users";


    //URL
    public static final String URL_QUESTION = "https://pubrepo.review-cq.com/webview/faq.html";
    public static final String URL_GUIDE = "https://pubrepo.review-cq.com/webview/guide.html";
    public static final String URL_TERMS = "https://pubrepo.review-cq.com/webview/agreement.html";
    public static final String URL_POLICY = "https://pubrepo.review-cq.com/webview/privacy_policy.html";

    //    case screen position
    public static final int SCREEN_HOME = 0;
    public static final int SCREEN_ACCOUNT = 1;
    public static final int SCREEN_NOTIFICATION = 2;
    public static final int SCREEN_MENU = 3;

    public static final String BLANK = "";
    public static final String LIMIT_DEFAULT = "10";
    public static final int PAGE_DEFAULT = 1;
    public static final String FOLLOW = "follow";
    public static final String UNFOLLOW = "unfollow";
    public static final String BLOCK = "block";
    public static final String UNBLOCK = "unblock";
    public static final String TYPE_LOGIN_TWITTER = "3";
    public static final String LIKE = "like";
    public static final String UNLIKE = "unlike";


    // Data format
    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT_DB_SS = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT_DB = "yyyy/MM/dd HH:mm";
    public static final String JAP_DATE_FORMAT = "yyyy MM月dd日";

    //Map
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String MIME_TYPE = "image/jpeg";
    public static final int TARGET_LENGTH_THUMBNAIL = 180;
    public static final int TARGET_LENGTH_DEFAULT = 960;

    public static final String COMMENT_TYPE_1 = "に気になる！されました。";
    public static final String COMMENT_TYPE_2 = "に担当者からのコメントが付きました。";
    public static final String COMMENT_TYPE_3 = "の対応状況が「対応待ち」に変更されました。";
    public static final String COMMENT_TYPE_4 = "の対応状況が「対応中」に変更されました。";
    public static final String COMMENT_TYPE_5 = "の対応状況が「対応済み」に変更されました。";

    public static final String STATUS_1_UNRECOGNIZED = "未承認";
    public static final String STATUS_2_WAITING = "対応待ち";
    public static final String STATUS_3_DURING = "対応中";
    public static final String STATUS_4_CORRESPONDING = "対応済み";

    public static final String STATUS = "状況 \n";
    public static final String STATUS_TYPE = "種別 \n";

}
