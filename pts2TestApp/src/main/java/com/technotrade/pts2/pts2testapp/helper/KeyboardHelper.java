package com.technotrade.pts2.pts2testapp.helper;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.technotrade.pts2.pts2testapp.gui.fragment.KeyboardFragment;

import java.lang.ref.WeakReference;

/// <summary>
/// Manager class that responsible for work with PTS2 device
/// </summary>
public class KeyboardHelper {
    private final WeakReference<Context> mContextRef;
    private WeakReference<FragmentManager> mFragmentManager;
    private KeyboardFragment mKeyboardFragment;

    public KeyboardHelper(Context context) {
        Context appContext = context.getApplicationContext();
        mContextRef = new WeakReference<>(appContext);
        mKeyboardFragment = new KeyboardFragment();
    }

    public void showKeyboardFragment(@IdRes int containerViewId, FragmentManager fragmentManager) {
        if (mFragmentManager == null) {
            mFragmentManager = new WeakReference<>(fragmentManager);
        }

        hideAndRemoveKeyboard(fragmentManager);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.add(containerViewId, mKeyboardFragment, "Keyboard");
        ft.commit();
    }

    public void hideAndRemoveKeyboard(FragmentManager fragmentManager) {
        if (mFragmentManager == null) {
            mFragmentManager = new WeakReference<>(fragmentManager);
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prevKeyboardFragment = fragmentManager.findFragmentByTag("Keyboard");
        if (prevKeyboardFragment != null) {
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            ft.remove(prevKeyboardFragment);
            ft.commit();
        }
    }

    public void hideKeyboardFragment(FragmentManager fragmentManager) {
        if (mFragmentManager == null) {
            mFragmentManager = new WeakReference<>(fragmentManager);
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (mKeyboardFragment.isAdded()) {
            ft.hide(mKeyboardFragment);
        }

        ft.commit();
    }
}