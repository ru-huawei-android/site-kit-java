package com.huawei.sitekit.java.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class AndroidUtils {

    public static void saveTextToClipboard(Context context, String message) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", message);
        clipboard.setPrimaryClip(clip);
    }
}
