package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionInflater;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentPumpsBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;

public class PumpsFragment extends BaseFragment<PumpsViewModel> {
    private FragmentPumpsBinding mBinding = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSharedElementEnterTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));

        setSharedElementReturnTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentPumpsBinding.inflate(inflater, container, false);

        mBinding.setViewModel(getViewModel());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mBinding.rvPumps.setLayoutManager(layoutManager);

        return mBinding.getRoot();
    }

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

        switch(ViewModelCommand.valueOf(command)) {
            case navigateToPump:
                navigateToPump((PumpItem)data);
                break;
            default:
        }
    }

    public void navigateToPump(PumpItem pumpItem) {
        if(pumpItem == null) {
            return;
        }

        int position = pumpItem.getNumber() - 1;
        String transitionName = "pumpTransition" + position;
        View view = mBinding.rvPumps.getChildAt(position);

        if (view == null) {
            return;
        }

        view.setTransitionName(transitionName);

        Bundle args = new Bundle();
        args.putString("TRANSITION_NAME", transitionName);

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
            .addSharedElement(view, transitionName)
            .build();

        navigateTo(R.id.action_pumpsFragment_to_pumpFragment, args, null, extras);
    }
}