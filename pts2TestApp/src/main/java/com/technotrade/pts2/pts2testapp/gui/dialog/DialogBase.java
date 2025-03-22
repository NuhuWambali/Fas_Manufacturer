package com.technotrade.pts2.pts2testapp.gui.dialog;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public abstract class DialogBase extends DialogFragment {

    protected final String mTitle;
    protected final String mMessage;

    public DialogBase() {
        mTitle = "";
        mMessage = "";
    }

    public DialogBase(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setDimAmount(0.5f);
        }
    }
}