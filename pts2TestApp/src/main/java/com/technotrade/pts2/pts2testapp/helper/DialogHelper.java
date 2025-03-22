package com.technotrade.pts2.pts2testapp.helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.technotrade.pts2.pts2testapp.gui.dialog.DialogConfirmation;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogExclamation;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogProgress;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogWarning;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ConfirmationViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ExclamationViewModel;

public class DialogHelper {
    public static DialogProgress mDialogProgress = null;

    public static void showDialogExclamation(LifecycleOwner owner, FragmentManager fragmentManager, String title, String message) {
        ExclamationViewModel exclamationViewModel = new ViewModelProvider((ViewModelStoreOwner)owner).get(ExclamationViewModel.class);
        DialogExclamation dialogExclamation = new DialogExclamation(exclamationViewModel, title, message);
        dialogExclamation.show(fragmentManager, "DialogExclamation");

        exclamationViewModel.getAcknowledgement().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null && isYes) {
                dialogExclamation.dismissNow();
                exclamationViewModel.getAcknowledgement().removeObservers(owner);
                exclamationViewModel.onDialogDismissed();
            }
        });
    }

    public static void showDialogWarning(LifecycleOwner owner, FragmentManager fragmentManager, String title, String message) {
        ExclamationViewModel exclamationViewModel = new ViewModelProvider((ViewModelStoreOwner)owner).get(ExclamationViewModel.class);
        DialogWarning dialogWarning = new DialogWarning(exclamationViewModel, title, message);
        dialogWarning.show(fragmentManager, "DialogWarning");

        exclamationViewModel.getAcknowledgement().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null && isYes) {
                dialogWarning.dismissNow();
                exclamationViewModel.getAcknowledgement().removeObservers(owner);
                exclamationViewModel.onDialogDismissed();
            }
        });
    }

    public static void showConfirmationDialog(LifecycleOwner owner, FragmentManager fragmentManager, String title, String message,
                                              Runnable onYes, Runnable onNo) {
        ConfirmationViewModel confirmationViewModel = new ViewModelProvider((ViewModelStoreOwner)owner).get(ConfirmationViewModel.class);
        DialogConfirmation dialogConfirmation = new DialogConfirmation(confirmationViewModel, title, message);
        dialogConfirmation.show(fragmentManager, "DialogConfirmation");

        confirmationViewModel.getConfirmation().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null) {
                if (isYes) {
                    onYes.run();
                } else {
                    onNo.run();
                }

                dialogConfirmation.dismissNow();
                confirmationViewModel.getConfirmation().removeObservers(owner);
                confirmationViewModel.onDialogDismissed();
            }
        });
    }

    public static void showDialogProgress(AppCompatActivity activity) {
        showDialogProgress(activity.getSupportFragmentManager());
    }

    public static void showDialogProgress(Fragment fragment) {
        showDialogProgress(fragment.getChildFragmentManager());
    }

    public static void showDialogProgress(FragmentManager fragmentManager) {
        if (mDialogProgress != null) {
            hideDialogProgress();
        }

        mDialogProgress = new DialogProgress();
        mDialogProgress.showNow(fragmentManager, "DialogProgress");
    }

    public static void hideDialogProgress() {
        if (mDialogProgress != null && mDialogProgress.isAdded()) {
            mDialogProgress.dismissNow();
            mDialogProgress = null;
        }
    }
}