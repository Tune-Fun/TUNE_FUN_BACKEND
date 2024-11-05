package com.tune_fun.v1.common.config;

public class Uris {

    private Uris() {
    }


    public static final String MESSAGE_CODES = "/message-codes";
    public static final String CUSTOM_RESPONSE_EXAMPLE = "/custom-response-example";
    public static final String CUSTOM_EXCEPTION_RESPONSE_EXAMPLE = "/custom-exception-response-example";

    public static final String TEMP_ROOT = "/temp";

    public static final String TEMP_ACCOUNT_RESET = TEMP_ROOT + "/account";

    public static final String ACTUATOR = "/actuator";
    public static final String SERVICE_WORKER_JS = "/service-worker.js";

    public static final String API_V1_ROOT = "/v1";

    public static final String REST_NAME_ID = "/{id}";
    public static final String REST_NAME_UUID = "/{uuid}";

    public static final String DEFAULT_LOGIN_PAGE = "/login";
    public static final String CHECK_USERNAME_DUPLICATE = API_V1_ROOT + "/check-username-duplicate";
    public static final String CHECK_EMAIL_DUPLICATE = API_V1_ROOT + "/check-email-duplicate";

    public static final String CHECK_PASSWORD_MATCH = API_V1_ROOT + "/check-password-match";
    public static final String FIND_USERNAME = API_V1_ROOT + "/find-username";
    public static final String REGISTER = API_V1_ROOT + "/register";
    public static final String LOGIN = API_V1_ROOT + "/login";
    public static final String CANCEL_ACCOUNT = API_V1_ROOT + "/account-cancellation";
    public static final String OAUTH2_AUTHORIZATION_ROOT = "/oauth2/authorization";
    public static final String OAUTH2_GOOGLE_ROOT = OAUTH2_AUTHORIZATION_ROOT + "/google";
    public static final String OAUTH2_APPLE_ROOT = OAUTH2_AUTHORIZATION_ROOT + "/apple";
    public static final String OAUTH2_INSTAGRAM_ROOT = OAUTH2_AUTHORIZATION_ROOT + "/instagram";
    public static final String OAUTH2_LOGIN_GOOGLE = OAUTH2_GOOGLE_ROOT + "?mode=login";
    public static final String OAUTH2_LINK_GOOGLE = OAUTH2_GOOGLE_ROOT + "?mode=link";
    public static final String OAUTH2_UNLINK_GOOGLE = OAUTH2_GOOGLE_ROOT + "?mode=unlink";
    public static final String OAUTH2_LOGIN_APPLE = OAUTH2_APPLE_ROOT + "?mode=login";
    public static final String OAUTH2_LINK_APPLE = OAUTH2_APPLE_ROOT + "?mode=link";
    public static final String OAUTH2_UNLINK_APPLE = OAUTH2_APPLE_ROOT + "?mode=unlink";
    public static final String OAUTH2_LOGIN_INSTAGRAM = OAUTH2_INSTAGRAM_ROOT + "?mode=login";
    public static final String OAUTH2_LINK_INSTAGRAM = OAUTH2_INSTAGRAM_ROOT + "?mode=link";
    public static final String OAUTH2_UNLINK_INSTAGRAM = OAUTH2_INSTAGRAM_ROOT + "?mode=unlink";
    public static final String LOGOUT = API_V1_ROOT + "/logout";
    public static final String REFRESH = API_V1_ROOT + "/refresh";
    public static final String SET_NEW_PASSWORD = API_V1_ROOT + "/set-new-password";
    public static final String UPDATE_NICKNAME = API_V1_ROOT + "/update-nickname";

    public static final String ACCOUNT_ROOT = API_V1_ROOT + "/accounts";
    public static final String ARTIST_ROOT = API_V1_ROOT + "/artist";

    public static final String FORGOT_PASSWORD_SEND_OTP = API_V1_ROOT + "/forgot-password/send-otp";
    public static final String ACCOUNT_CANCELLATION_SEND_OTP = API_V1_ROOT + "/account-cancellation/send-otp";

    public static final String CHECK_EMAIL_VERIFIED = API_V1_ROOT + "/check-email-verified";
    public static final String EMAIL_ROOT = ACCOUNT_ROOT + "/email";
    public static final String VERIFY_EMAIL = EMAIL_ROOT + "/verify";

    public static final String VERIFY_OTP = API_V1_ROOT + "/otp/verify";
    public static final String VERIFY_OTP_WITH_AUTHORIZATION = API_V1_ROOT + "/otp/verify-with-authorization";
    public static final String RESEND_OTP = API_V1_ROOT + "/otp/resend";

    public static final String VOTE_ROOT = API_V1_ROOT + "/votes";
    public static final String VOTE_PAPER_ROOT = VOTE_ROOT + "/paper";

    public static final String VOTE_PAPER_CHOICE = VOTE_PAPER_ROOT + "/choice/{votePaperId}";
    public static final String VOTE_PAPER_COUNT = VOTE_PAPER_ROOT + "/count/{votePaperId}";

    public static final String UPDATE_VOTE_PAPER_DELIVERY_DATE = VOTE_PAPER_ROOT + "/{votePaperId}/delivery-date";
    public static final String UPDATE_VOTE_PAPER_VIDEO_URL = VOTE_PAPER_ROOT + "/{votePaperId}/video-url";

    public static final String REGISTER_VOTE = VOTE_ROOT + "/{votePaperId}" + "/register" + "/{voteChoiceId}";

    public static final String LIKE_ROOT = API_V1_ROOT + "/likes";

    public static final String LIKE_VOTE_PAPER = LIKE_ROOT + "/{votePaperId}";

    public static final String FOLLOW_ROOT = API_V1_ROOT + "/follows";

    public static final String FOLLOWING = FOLLOW_ROOT + "/following";

    public static final String SEARCH_ROOT = API_V1_ROOT + "/search";


    public static final String SWAGGER_UI_ROOT = "/swagger-ui";
    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_DOCS = "/docs/com.tune_fun-open-api-3.0.1.json";

    public static final String SWAGGER_INDEX_HTML = SWAGGER_UI_ROOT + "/index.html";
    public static final String SWAGGER_INDEX_CSS = SWAGGER_UI_ROOT + "/index.css";
    public static final String SWAGGER_BUNDLE_JS = SWAGGER_UI_ROOT + "/swagger-ui-bundle.js";
    public static final String SWAGGER_BUNDLE_JS_MAP = SWAGGER_UI_ROOT + "/swagger-ui-bundle.js.map";
    public static final String SWAGGER_CSS = SWAGGER_UI_ROOT + "/swagger-ui.css";
    public static final String SWAGGER_CSS_MAP = SWAGGER_UI_ROOT + "/swagger-ui.css.map";
    public static final String SWAGGER_STANDALONE_PRESET_JS = SWAGGER_UI_ROOT + "/swagger-ui-standalone-preset.js";
    public static final String SWAGGER_STANDALONE_PRESET_JS_MAP = SWAGGER_UI_ROOT + "/swagger-ui-standalone-preset.js.map";
    public static final String SWAGGER_INITIALIZER_JS = SWAGGER_UI_ROOT + "/swagger-initializer.js";
    public static final String SWAGGER_FAVICON = SWAGGER_UI_ROOT + "/favicon-32x32.png";

    public static final String SWAGGER_API_DOCS = "/v3/api-docs";
    public static final String SWAGGER_API_DOCS_SWAGGER_CONFIG = SWAGGER_API_DOCS + "/swagger-config";
    public static final String SWAGGER_API_DOCS_JSON = "/docs/com.tune_fun-open-api-3.0.1.json";

    public static final String API_DOC_HTML = "/static/docs/index.html";
    public static final String FAVICON = "/favicon.ico";
    public static final String HEALTH_CHECK = "/health-check";

    public static final String[] NOT_LOGGING_URIS = {
            DEFAULT_LOGIN_PAGE,
            SERVICE_WORKER_JS,
            SWAGGER_UI,
            SWAGGER_INDEX_HTML,
            SWAGGER_INDEX_CSS,
            SWAGGER_BUNDLE_JS,
            SWAGGER_BUNDLE_JS_MAP,
            SWAGGER_CSS,
            SWAGGER_CSS_MAP,
            SWAGGER_STANDALONE_PRESET_JS,
            SWAGGER_STANDALONE_PRESET_JS_MAP,
            SWAGGER_INITIALIZER_JS,
            SWAGGER_API_DOCS,
            SWAGGER_API_DOCS_SWAGGER_CONFIG,
            SWAGGER_FAVICON,
            SWAGGER_API_DOCS_JSON,
            API_DOC_HTML,
            FAVICON,
            HEALTH_CHECK,
            MESSAGE_CODES,
            CUSTOM_RESPONSE_EXAMPLE,
            CUSTOM_EXCEPTION_RESPONSE_EXAMPLE
    };
    public static final String[] PERMIT_ALL_URIS = {
            TEMP_ACCOUNT_RESET,
            "/error",

            ACTUATOR,
            DEFAULT_LOGIN_PAGE,
            SERVICE_WORKER_JS,
            CHECK_USERNAME_DUPLICATE,
            CHECK_EMAIL_DUPLICATE,
            FIND_USERNAME,
            REGISTER,
            LOGIN,
            OAUTH2_GOOGLE_ROOT,
            OAUTH2_APPLE_ROOT,
            OAUTH2_INSTAGRAM_ROOT,
            REFRESH,
            FORGOT_PASSWORD_SEND_OTP,
            RESEND_OTP,
            VERIFY_OTP,
            SWAGGER_UI,
            SWAGGER_INDEX_HTML,
            SWAGGER_DOCS,
            SWAGGER_INDEX_CSS,
            SWAGGER_BUNDLE_JS,
            SWAGGER_BUNDLE_JS_MAP,
            SWAGGER_CSS,
            SWAGGER_CSS_MAP,
            SWAGGER_STANDALONE_PRESET_JS,
            SWAGGER_STANDALONE_PRESET_JS_MAP,
            SWAGGER_INITIALIZER_JS,
            SWAGGER_API_DOCS,
            SWAGGER_API_DOCS_SWAGGER_CONFIG,
            SWAGGER_FAVICON,
            SWAGGER_API_DOCS_JSON,
            API_DOC_HTML,
            FAVICON,
            HEALTH_CHECK,
            MESSAGE_CODES,
            CUSTOM_RESPONSE_EXAMPLE,
            CUSTOM_EXCEPTION_RESPONSE_EXAMPLE
    };

    public static final String[] ARTIST_URIS = {
            UPDATE_VOTE_PAPER_DELIVERY_DATE
    };

    public static final String[] ADMIN_URIS = {
    };


}
