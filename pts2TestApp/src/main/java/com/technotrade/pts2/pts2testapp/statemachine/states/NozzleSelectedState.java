package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.chooseNozzleWithList;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.helper.MonitorHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NozzleSelectedState extends BaseState {
	public static final String NAME = "ChooseNozzleState";

	public NozzleSelectedState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);

		if (!bRes) {

			return bRes;
		}

		String sMonitorText = stateData.getText();
		BaseViewModel viewModel = stateData.getViewModel();

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order order = orderManager.getConstructingOrder();
		if (!order.isPumpSet()) {
			String sError = getResourceString(R.string.select_the_pump_first);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		PumpItem pumpItem = order.getPump();
		PumpsConfiguration pumpsConfiguration = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getPumpsConfiguration();
		List<Pump> foundPumps = pumpsConfiguration.getPumps().stream()
			.filter(obj -> obj.getId() == pumpItem.getNumber())
			.collect(Collectors.toList());

		if (foundPumps.size() != 1) {
			String sError = getResourceString(R.string.wrong_pump_during_choosing_a_nozzle);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		Pump pump = foundPumps.get(0);

		MonitorHelper monitorHelper = new MonitorHelper();
		AtomicInteger nozzle = new AtomicInteger();

		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
			monitorHelper.parseNozzleForPump(sMonitorText, pump, (result, sError, parsedValue) -> {
				if (!result) {
					viewModel.sendViewModelCommandEvent(showError.toString(), sError);
				}
				else {
					nozzle.set(parsedValue);
				}
			}
		));

		try {
			bRes = future.get();
		} catch (InterruptedException | ExecutionException e) {
			bRes = false;
		}

		if (!bRes) {
			return bRes;
		}

		if (nozzle.get() == 0) {
			viewModel.sendViewModelCommandEvent(chooseNozzleWithList.toString(), pumpItem);

			return false;
		}
		else {
			List<NozzleItem> nozzleItems =  ApplicationFacade.getInstance().getPTSManager().getDataStorage().getNozzleItems();
			assert nozzleItems != null;
			List<NozzleItem> found = nozzleItems.stream()
				.filter(obj -> obj.getNozzleNumber() == nozzle.get())
				.collect(Collectors.toList());

			if (found.isEmpty()) {
				String sError = getResourceString(R.string.the_chosen_nozzle_is_not_configured);
				LogHelper.logError(sError);
				viewModel.sendViewModelCommandEvent(showError.toString(), sError);

				return false;
			}

			NozzleItem nozzleItem = found.get(0);

			order.setNozzle(nozzleItem);
		}

		return true;
	}

	@Override
	public boolean onEnd(BaseState stateTo, StateData stateData) {
		boolean bRes = super.onEnd(stateTo, stateData);
		if (!bRes) {
			return bRes;
		}

		if(stateTo.getName().equals(PumpSelectedState.NAME)) {
			OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
			Order order = orderManager.getConstructingOrder();
//			System.out.println("this is my order "+order);
			order.resetNozzle();
			order.resetQuantity();
			order.resetAmount();
		}

		return true;
	}

	@Override
	public List<String> getPossibleNextStates() {
		List<String> possibleNextStates = new ArrayList<>();
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(NozzleSelectedState.NAME);
		possibleNextStates.add(QuantitySelectedState.NAME);
		possibleNextStates.add(CurrencySelectedState.NAME);
		possibleNextStates.add(FuelingState.NAME);
		possibleNextStates.add(StoppingState.NAME);

		return possibleNextStates;
	}
}
