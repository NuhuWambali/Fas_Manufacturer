package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentKeyboardBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.KeyboardViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

public class KeyboardFragment extends BaseFragment<KeyboardViewModel> {
    private FragmentKeyboardBinding mBinding = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentKeyboardBinding.inflate(inflater, container, false);

        mBinding.setViewModel(getViewModel());
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
    protected Class<KeyboardViewModel> getViewModelClass() {
        return KeyboardViewModel.class;
    }

    @Override
    protected void execModelViewCommand(EventCommand<?> eventCommand) {
        // keyboard fragment can send messages to parent fragments that uses onscreen keyboard
        Fragment parentFragment = getParentFragment();

        if (parentFragment instanceof BaseFragment) {
            BaseFragment<?> baseFragment = (BaseFragment<?>) parentFragment;
            baseFragment.execModelViewCommand(eventCommand);
        }

        if (eventCommand == null) {
            return;
        }

        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        switch(ViewModelCommand.valueOf(command)) {
            case showPumpStopConfirmationDialog:
                showPumpStopConfirmationDialog((PumpItem)data);
                break;
            default:
        }
    }

    private void showPumpStopConfirmationDialog(PumpItem pumpItem) {
        final String title =requireView().getResources().getString(R.string.are_you_sure);
        String message = requireView().getResources().getString(R.string.are_you_sure_you_want_to_stop_the_pump_n)
            + " " + pumpItem.getNumber();
        DialogHelper.showConfirmationDialog(this, getChildFragmentManager(), title, message,
            () -> {
                DialogHelper.showDialogProgress(this);

                getViewModel().stopSelectedPump(pumpItem);

                DialogHelper.hideDialogProgress();
            }, () -> {
            }
        );
    }
}