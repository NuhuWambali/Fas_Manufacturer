package com.technotrade.pts2.pts2testapp.gui.viewmodel;

public enum ViewModelCommand {
	showConnectingProgress("showConnectingProgress"),
	showConnectedButton("showConnectedButton"),
	showDisconnectedButton("showDisconnectedButton"),
	setSettingsButtonEnabled("setSettingsButtonEnabled"),
	navigateToPump("navigateToPump"),
	choosePumpWithList("choosePumpWithList"),
	choosePump("choosePump"),
	chooseNozzleWithList("chooseNozzleWithList"),
	showError("showError"),
	navigateUp("navigateUp"),
	setProgressVisible("setProgressVisible"),
	showSynchronizePts2TimeToLocalConfirmationDialog("showSynchronizePts2TimeToLocalConfirmationDialog"),
	setSynchronizePts2TimeToLocalButtonEnabled("setSynchronizePts2TimeToLocalButtonEnabled"),
	showPumpStopConfirmationDialog("showPumpStopConfirmationDialog"),
	updateNozzlesForSelectedPumpItem("updateNozzlesForSelectedPumpItem");

	private final String mText;

	ViewModelCommand(final String text) {
		mText = text;
	}

	@Override
	public String toString() {
		return mText;
	}
}