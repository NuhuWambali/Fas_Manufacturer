package com.technotrade.pts2.pts2testapp.helper;

import android.view.View;
import android.view.ViewGroup;

public class ViewHelper {
    public static void updateMarginsInDp(View view, int leftDp, int topDp, int rightDp, int bottomDp) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        float density = view.getContext().getResources().getDisplayMetrics().density;
        layoutParams.setMargins(
            leftDp != -1 ? (int) (leftDp * density) : layoutParams.leftMargin,
            topDp != -1 ? (int) (topDp * density) : layoutParams.topMargin,
            rightDp != -1 ? (int) (rightDp * density) : layoutParams.rightMargin,
            bottomDp != -1 ? (int) (bottomDp * density) : layoutParams.bottomMargin
        );
    }
}