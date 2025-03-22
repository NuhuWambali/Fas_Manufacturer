//package com.technotrade.pts2.pts2testapp.statemachine;
//
//import com.technotrade.pts2.pts2testapp.entity.PumpItem;
//import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
//
//public class StateData {
//	private String mText;
//	private BaseViewModel mViewModel;
//	private PumpItem mPumpItem;
//	private boolean mConfirmed;
//
//	public StateData() {
//	}
//
//	public String getText() {
//		return mText;
//	}
//
//	public void setText(String text) {
//		mText = text;
//	}
//
//	public BaseViewModel getViewModel() {
//		return mViewModel;
//	}
//
//	public void setViewModel(BaseViewModel viewModel) {
//		mViewModel = viewModel;
//	}
//
//	public PumpItem getPumpItem() {
//		return mPumpItem;
//	}
//
//	public void setPumpItem(PumpItem pumpItem) {
//		mPumpItem = pumpItem;
//	}
//	public boolean getConfirmed() {
//		return mConfirmed;
//	}
//
//	public void setConfirmed(boolean confirmed) {
//		mConfirmed = confirmed;
//	}
//}

package com.technotrade.pts2.pts2testapp.statemachine;

import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;

public class StateData {
	private String mText;
	private BaseViewModel mViewModel;
	private PumpItem mPumpItem;
	private boolean mConfirmed;
	private String fullName;
	private String tin;
	private String plateNumber;

	public StateData() {
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public BaseViewModel getViewModel() {
		return mViewModel;
	}

	public void setViewModel(BaseViewModel viewModel) {
		mViewModel = viewModel;
	}

	public PumpItem getPumpItem() {
		return mPumpItem;
	}

	public void setPumpItem(PumpItem pumpItem) {
		mPumpItem = pumpItem;
	}

	public boolean getConfirmed() {
		return mConfirmed;
	}

	public void setConfirmed(boolean confirmed) {
		mConfirmed = confirmed;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
}
