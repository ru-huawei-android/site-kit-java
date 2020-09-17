package com.huawei.sitekit.java.common;

import android.content.Context;
import android.net.Uri;

import com.huawei.agconnect.config.AGConnectServicesConfig;

public class Config {

    public final static String DEFAULT_LOCATION_TYPE = "ALL";
    public final static String DEFAULT_COUNTRY_CODE = "Us";
    public final static String DEFAULT_LANGUAGE = "en";
    public final static int DEFAULT_PAGE_COUNT = 10;

    private final static String API_KEY_PATH = "client/api_key";

    public static String getAgcApiKey(Context context) {
        String apiKey = AGConnectServicesConfig.fromContext(context).getString(API_KEY_PATH);
        return Uri.encode(apiKey);
    }
}
