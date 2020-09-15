package com.huawei.sitekit.java.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

public class AndroidUtils {

    public static void changeFilterVisible(ConstraintLayout parent, View child) {
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) child.getLayoutParams();
        int height = layoutParams.height;
        int newConstrainedHeight = 0;
        if (height == 0)
            newConstrainedHeight = ConstraintSet.WRAP_CONTENT;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parent);
        constraintSet.constrainHeight(child.getId(), newConstrainedHeight);

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(300);

        TransitionManager.beginDelayedTransition(parent, autoTransition);
        constraintSet.applyTo(parent);
    }

    public static void saveTextToClipboard(Context context, String message) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", message);
        clipboard.setPrimaryClip(clip);
    }
}
