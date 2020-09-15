package com.huawei.sitekit.java.common;

import android.content.Context;
import android.net.Uri;

import com.huawei.agconnect.config.AGConnectServicesConfig;

public class Config {

    public static String getAgcApiKey(Context context) {
        String apiKey = AGConnectServicesConfig.fromContext(context).getString("client/api_key");
        return Uri.encode(apiKey);
    }
}
