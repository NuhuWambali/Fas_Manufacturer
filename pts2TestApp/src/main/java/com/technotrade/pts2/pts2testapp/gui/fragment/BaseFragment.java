package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.Event;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.activity.MainActivity;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

public abstract class BaseFragment<T extends BaseViewModel> extends Fragment {
    protected T mViewModel;

    protected NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(getViewModelClass());
        mNavController = NavHostFragment.findNavController (this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel.getViewModelCommandQueue().observe(getViewLifecycleOwner(), eventQueue -> {
            if (eventQueue != null) {
                while (!eventQueue.isEmpty()) {
                    Event<EventCommand<?>> event = eventQueue.poll(); // Retrieve and remove the head of the queue
                    assert event != null;
                    EventCommand<?> eventCommand = event.getContent();

                    if (eventCommand != null) {
                        String command = eventCommand.getCommand();
                        Object data = eventCommand.getData();

                        if (command != null) {
                            // common commands
                            switch(ViewModelCommand.valueOf(command)) {
                                case showError:
                                    DialogHelper.showDialogWarning(this, getChildFragmentManager(), requireView().getResources().getString(R.string.error), (String)data);
                                    break;
                                case navigateUp:
                                    navigateUp();
                                    break;
                                case setProgressVisible:
                                    if((boolean)data) {
                                        DialogHelper.showDialogProgress(this);
                                    }
                                    else {
                                        DialogHelper.hideDialogProgress();
                                    }

                                    break;
                                default:
                            }

                            // specific for different fragments commands
                            execModelViewCommand(eventCommand);
                        }
                    }
                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract Class<T> getViewModelClass();

    protected T getViewModel() {
        return mViewModel;
    }

    protected abstract void execModelViewCommand(EventCommand<?> eventCommand);

    protected MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    };

    protected boolean onBackPressed() {
        return true;
    };

    protected void navigateUp() {
        if (mNavController != null) {
            mNavController.navigateUp();
        }
    }

    protected void navigateTo(int actionId) {
        if (mNavController != null) {
            mNavController.navigate(actionId);
        }
    }

    protected void navigateTo(int actionId, Bundle args) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args);
        }
    }

    protected void navigateTo(int actionId, Bundle args, NavOptions navOptions) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args, navOptions);
        }
    }

    protected void navigateTo(int actionId, Bundle args, NavOptions navOptions, FragmentNavigator.Extras extras) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args, navOptions, extras);
        }
    }
}