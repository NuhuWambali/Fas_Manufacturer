package com.technotrade.pts2.pts2testapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/// <summary>
/// Settings class
/// </summary>
public class Settings implements Serializable {
    private final WeakReference<Context> mContextRef;
	private String mCurrency;

    public Settings(Context context) {
        Context appContext = context.getApplicationContext();
        mContextRef = new WeakReference<>(appContext);
    }

    /// <summary>
    /// Currency symbol getter and setter
    /// </summary>
    public String getCurrency() {
        return mCurrency;
    }
    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    /// <summary>
    /// Loading PTS2 settings using SharedPreferences
    /// </summary>
    public synchronized void loadAppSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContextRef.get());

        String currency = preferences.getString("currency", "$");
        setCurrency(currency);
    }
}