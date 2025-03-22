package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.helper.MonitorHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class QuantitySelectedState extends BaseState {
	public static final String NAME = "QuantitySelectedState";

	public QuantitySelectedState() {
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
		if (!order.isPumpSet() || !order.isNozzleSet()) {
			String sError = getResourceString(R.string.select_the_pump_and_nozzle_first);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		MonitorHelper monitorHelper = new MonitorHelper();
		AtomicInteger quantity = new AtomicInteger();

		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
			monitorHelper.parseQuantity(sMonitorText, (result, sError, parsedValue) -> {
					if (!result) {
						viewModel.sendViewModelCommandEvent(showError.toString(), sError);
					}
					else {
						quantity.set(parsedValue);
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

		//Quantity and Amount are mutually exclusive
		order.resetAmount();

		order.setQuantity(BigDecimal.valueOf(quantity.get()));

		return true;
	}

	@Override
	public boolean onEnd(BaseState stateTo, StateData stateData) {
		boolean bRes = super.onEnd(stateTo, stateData);
		if (!bRes) {
			return bRes;
		}

		if(stateTo.getName().equals(PumpSelectedState.NAME)
			|| stateTo.getName().equals(NozzleSelectedState.NAME)) {
			OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
			Order order = orderManager.getConstructingOrder();
			order.resetQuantity();
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
