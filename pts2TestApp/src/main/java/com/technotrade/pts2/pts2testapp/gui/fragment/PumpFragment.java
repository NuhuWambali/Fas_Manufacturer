package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.transition.TransitionInflater;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentPumpBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.functional.PumpAction;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;

public class PumpFragment extends BaseFragment<PumpsViewModel> {
    private FragmentPumpBinding mBinding = null;
    private EditText etVolume;
    private EditText etPrice;
    private Spinner spinnerVolumePrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentPumpBinding.inflate(inflater, container, false);

        // Initialize views
        etVolume = mBinding.etVolume;
        etPrice = mBinding.etAmount;
        spinnerVolumePrice = mBinding.spinnerVolumePrice;

        // Set ViewModel and LifecycleOwner
        mBinding.setViewModel(getViewModel());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Set up transition name
        assert getArguments() != null;
        String transitionName = getArguments().getString("TRANSITION_NAME");
        mBinding.cvPump.setTransitionName(transitionName);

        // Set Spinner Listener
        spinnerVolumePrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Hide both EditTexts initially
                etVolume.setVisibility(View.GONE);
                etPrice.setVisibility(View.GONE);

                // Show the relevant EditText based on the spinner selection
                if (position == 0) { // Volume selected
                    etVolume.setVisibility(View.VISIBLE);
                    etPrice.setText(""); // Clear Amount field when switching to Volume
                } else if (position == 1) { // Price selected
                    etPrice.setVisibility(View.VISIBLE);
                    etVolume.setText(""); // Clear Volume field when switching to Amount
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action required
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        setSharedElementEnterTransition(TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_element_transition));

        setSharedElementReturnTransition(TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_element_transition));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        FragmentContainerView fragmentKeyboard = mBinding.fKeyboard;
        fragmentKeyboard.startAnimation(fadeIn);

        ApplicationFacade.getInstance().getKeyboardHelper().showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    protected Class<PumpsViewModel> getViewModelClass() {
        return PumpsViewModel.class;
    }

    @Override
    protected void execModelViewCommand(EventCommand<?> eventCommand) {
        if (eventCommand == null) {
            return;
        }

        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        switch (ViewModelCommand.valueOf(command)) {
            case choosePumpWithList:
                backToPumpsWithAnimation();
                break;
            case choosePump:
                choosePump((PumpItem) data);
                break;
            case chooseNozzleWithList:
                chooseNozzleWithList();
                break;
            case updateNozzlesForSelectedPumpItem:
                getViewModel().updateNozzlesForSelectedPumpItem((PumpItem) data);
                break;
            default:
        }
    }

    // Method to navigate back to pumps with animation
    private void backToPumpsWithAnimation() {
        ApplicationFacade.getInstance().getKeyboardHelper().hideAndRemoveKeyboard(getChildFragmentManager());
        mNavController.navigateUp();
    }

    // Method to handle pump selection
    public void choosePump(PumpItem pumpItem) {
        animatePumpChange(pumpItem, item -> getViewModel().setSelectedPump(pumpItem));
    }

    public void animatePumpChange(PumpItem pumpItem, PumpAction action) {
        final int animationDuration = 300;
        requireActivity().runOnUiThread(() -> {
            mBinding.cvPump.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mBinding.cvPump, "alpha", 1f, 0f);
            fadeOut.setDuration(animationDuration);

            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    action.execute(pumpItem);

                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mBinding.cvPump, "alpha", 0f, 1f);
                    fadeIn.setDuration(animationDuration);
                    fadeIn.start();
                }
            });

            fadeOut.start();
        });
    }

    public void chooseNozzleWithList() {
        ApplicationFacade.getInstance().getKeyboardHelper().hideAndRemoveKeyboard(getChildFragmentManager());

        Bundle args = new Bundle();
        NozzleItem nozzleItem = getViewModel().getTwoWayFields().getSelectedNozzle().getValue();

        if (nozzleItem != null) {
            args.putSerializable("NOZZLE_ITEM", nozzleItem);
        }

        mNavController.navigate(R.id.action_pumpFragment_to_nozzlesFragment, args);
    }
}
