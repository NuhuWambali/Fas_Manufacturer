package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.technotrade.pts2.pts2testapp.databinding.FragmentNozzlesBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;

public class NozzlesFragment extends BaseFragment<PumpsViewModel> {

	private FragmentNozzlesBinding mBinding;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mBinding = FragmentNozzlesBinding.inflate(inflater, container, false);
		mBinding.setViewModel(getViewModel());
		mBinding.setLifecycleOwner(getViewLifecycleOwner());
		mBinding.rvNozzles.setLayoutManager(new LinearLayoutManager(getContext()));

        mNavController = NavHostFragment.findNavController(this);

		Bundle args = getArguments();
		if (args != null) {
			NozzleItem nozzleItem;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
				nozzleItem = (NozzleItem) args.getSerializable("NOZZLE_ITEM", NozzleItem.class);
			} else {
				nozzleItem = (NozzleItem) args.getSerializable("NOZZLE_ITEM");
			}

			if (nozzleItem != null) {
				getViewModel().getNozzlesRecyclerViewAdapter().setSelectedNozzle(nozzleItem);
			}
		}

		return mBinding.getRoot();
	}

	@Override
	protected Class<PumpsViewModel> getViewModelClass() {
		return PumpsViewModel.class;
	}

	@Override
	protected void execModelViewCommand(EventCommand<?> eventCommand) {

	}
}