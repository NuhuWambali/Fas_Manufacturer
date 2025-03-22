package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentExtraBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ExtraViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

public class ExtraFragment extends BaseFragment<ExtraViewModel> {
    private FragmentExtraBinding mBinding = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentExtraBinding.inflate(inflater, container, false);

        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mViewModel != null) {
            mViewModel.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mViewModel != null) {
            mViewModel.onStop();
        }
    }

    @Override
    protected Class<ExtraViewModel> getViewModelClass() {
        return ExtraViewModel.class;
    }

    @Override
    protected void execModelViewCommand(EventCommand<?> eventCommand) {
        if (eventCommand == null) {
            return;
        }

        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        switch(ViewModelCommand.valueOf(command)) {
            case showSynchronizePts2TimeToLocalConfirmationDialog:
                showSynchronizePts2TimeToLocalConfirmationDialog();
                break;
            case setSynchronizePts2TimeToLocalButtonEnabled:
                mBinding.bSynchronizePts2TimeToLocal.setEnabled((Boolean)data);
                break;
            default:
        }
    }

    private void showSynchronizePts2TimeToLocalConfirmationDialog() {
        DialogHelper.showConfirmationDialog(this, getChildFragmentManager(), requireView().getResources().getString(R.string.are_you_sure), requireView().getResources().getString(R.string.are_you_sure_you_want_to_synchronize_the_time),
            () -> {
                DialogHelper.showDialogProgress(this);

                getViewModel().synchronizePts2TimeToLocal(
                    () -> {
                        DialogHelper.hideDialogProgress();
                        DialogHelper.showDialogExclamation(this, getChildFragmentManager(), requireView().getResources().getString(R.string.success), requireView().getResources().getString(R.string.the_time_was_successfully_synchronized));
                    },
                    message -> {
                        DialogHelper.hideDialogProgress();
                        DialogHelper.showDialogWarning(this, getChildFragmentManager(), requireView().getResources().getString(R.string.error), message);
                    }
                );
            }, () -> {
            }
        );
    }
}