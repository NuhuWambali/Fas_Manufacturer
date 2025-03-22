package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.navigateToPump;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.navigateUp;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpDisplayData;
import com.technotrade.pts2.datastructs.PumpEndOfTransactionStatus;
import com.technotrade.pts2.datastructs.PumpFillingStatus;
import com.technotrade.pts2.datastructs.PumpIdleStatus;
import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.datastructs.PumpOfflineStatus;
import com.technotrade.pts2.datastructs.PumpTotals;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.BR;
import com.technotrade.pts2.pts2testapp.DataStorage;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.adapter.NozzlesRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.adapter.PumpsRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.entity.DataHolder;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.listener.OnNozzleItemClickListener;
import com.technotrade.pts2.pts2testapp.listener.OnPumpItemClickListener;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;
import com.technotrade.pts2.pts2testapp.statemachine.StateMachine;
import com.technotrade.pts2.pts2testapp.statemachine.states.IdleState;
import com.technotrade.pts2.pts2testapp.statemachine.states.NozzleSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpSelectedState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PumpsViewModel extends BaseViewModel {
    public static class ObservableFieldsForTwoWayBinding extends BaseObservable {
        private final MutableLiveData<PumpItem> mSelectedPump;
        private final MutableLiveData<NozzleItem> mSelectedNozzle;
//        public MutableLiveData<String> fullName = new MutableLiveData<>();
//        public MutableLiveData<String> tin = new MutableLiveData<>();
//        public MutableLiveData<String> plateNumber = new MutableLiveData<>();

        public ObservableFieldsForTwoWayBinding() {
            mSelectedPump = new MutableLiveData<>();
            mSelectedNozzle = new MutableLiveData<>();
        }

        @Bindable
        public MutableLiveData<PumpItem> getSelectedPump() {
            return mSelectedPump;
        }

        public void setSelectedPump(PumpItem pumpItem) {
            mSelectedPump.postValue(pumpItem);
            notifyPropertyChanged(BR.selectedPump);
        }

        @Bindable
        public MutableLiveData<NozzleItem> getSelectedNozzle() {
            return mSelectedNozzle;
        }

        public void setSelectedNozzle(NozzleItem nozzleItem) {
            mSelectedNozzle.postValue(nozzleItem);
            notifyPropertyChanged(BR.selectedNozzle);
        }
    }

    private final ObservableFieldsForTwoWayBinding mTwoWayFields;
    private final PTSManager mPTSManager;
    private final DataStorage mDataStorage;
    private final MutableLiveData<List<PumpItem>> mPumps;
    private final MutableLiveData<List<NozzleItem>> mNozzles;
    private final PumpsRecyclerViewAdapter mPumpsRecyclerViewAdapter;
    private final NozzlesRecyclerViewAdapter mNozzlesRecyclerViewAdapter;

    public PumpsViewModel() {
        mTwoWayFields = new ObservableFieldsForTwoWayBinding();
        mPTSManager = ApplicationFacade.getInstance().getPTSManager();
        mDataStorage = mPTSManager.getDataStorage();
        mPumps = new MutableLiveData<>();
        mNozzles = new MutableLiveData<>();
        mPumpsRecyclerViewAdapter = new PumpsRecyclerViewAdapter();
        mNozzlesRecyclerViewAdapter = new NozzlesRecyclerViewAdapter();

        initOnClickListeners();
        initFieldsObserver();
        initConnectedListener();
        initPumpsConfigurationListener();
        initPumpIdleStatusDataListener();
        initPumpFillingStatusDataListener();
        initPumpEndOfTransactionStatusListener();
        initPumpOfflineStatusListener();
        initPumpTotalsListener();
        initPumpDisplayDataListener();
    }

    private void initOnClickListeners() {
        mPumpsRecyclerViewAdapter.addOnPumpItemClickListener(new OnPumpItemClickListener() {
            @Override
            public void onPumpItemClick(PumpItem item) {
                onRequestToNavigateToPump(item);
            }
        });

        mNozzlesRecyclerViewAdapter.addOnNozzleItemClickListener(new OnNozzleItemClickListener() {
            @Override
            public void onNozzleItemClick(NozzleItem item) {
                updateSelectedNozzleForPumpItem(item);
            }
        });
    }

    private void initFieldsObserver() {

        final OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();

        mTwoWayFields.getSelectedPump().observeForever(newSelectedPump -> {
            if (newSelectedPump != null) {
                mDataStorage.setSelectedPump(newSelectedPump);
            }
        });

        mTwoWayFields.getSelectedNozzle().observeForever(newSelectedNozzle -> {
            if (newSelectedNozzle != null) {
                mDataStorage.setSelectedNozzle(newSelectedNozzle);
            }
        });

        mPumps.observeForever(newPumps -> {
            if (newPumps != null) {
                mDataStorage.setPumpItems(newPumps);

                final PumpItem selectedPump = getSelectedPump().getValue();

                if(selectedPump != null) {
                    Optional<PumpItem> foundPump = newPumps.stream()
                        .filter(item -> item.getNumber() == selectedPump.getNumber())
                        .findFirst();

                    if (foundPump.isPresent()) {
                        PumpItem foundItem = foundPump.get();
                        setSelectedPump(foundItem);
                    }
                }

                orderManager.updateOrdersProgressIndicators(newPumps);
            }
        });

        mNozzles.observeForever(newNozzles -> {
            if (mDataStorage.getNozzleItems() != newNozzles) {
                mDataStorage.setNozzleItems(newNozzles);
            }

            chooseSelectedNozzleAutomatically(newNozzles);
        });
    }

    private void initConnectedListener() {
        DataHolder<Boolean> connectedDataHolder = mDataStorage.getConnectedDataHolder();
        connectedDataHolder.addOnDataChangeListener(connected -> {
            if (connected) {
            }
        });
    }

    private void initPumpsConfigurationListener() {
        DataHolder<PumpsConfiguration> pumpsConfigurationDataHolder = mDataStorage.getPumpsConfigurationDataHolder();
        pumpsConfigurationDataHolder.addOnDataChangeListener(data -> {
            List<Pump> pumps = data.getPumps();
            List<PumpItem> pumpItems = new ArrayList<>();

            for (Pump pump : pumps) {
                PumpItem pumpItem = new PumpItem();
                pumpItem.setNumber(pump.getId());
                pumpItems.add(pumpItem);
            }

            setPumps(pumpItems);
        });
    }

    private void initPumpIdleStatusDataListener() {
        DataHolder<PumpIdleStatus> pumpIdleStatusDataHolder = mDataStorage.getPumpIdleStatusDataHolder();
        pumpIdleStatusDataHolder.addOnDataChangeListener(pumpIdleStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpIdleStatus.getPump()) {
                        item.setStatus(pumpIdleStatus.getStatus());
                        item.setStateDescription(pumpIdleStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpIdleStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpIdleStatus.getStatus()));
                        item.setNozzle(pumpIdleStatus.getNozzle());
                        item.setFuelName(pumpIdleStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpIdleStatus.getLastPrice()));
                        item.setVolume(String.valueOf(pumpIdleStatus.getLastVolume()));
                        item.setAmount(String.valueOf(pumpIdleStatus.getLastAmount()));

                        StateData stateData = new StateData();
                        stateData.setText(String.valueOf(item.getNozzle()));
                        stateData.setViewModel(this);
                        stateData.setPumpItem(item);

                        final StateMachine stateMachine = ApplicationFacade.getInstance().getStateMachine();
                        stateMachine.transition(new IdleState(), stateData);

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    private void initPumpFillingStatusDataListener() {
        DataHolder<PumpFillingStatus> pumpFillingStatusDataHolder = mDataStorage.getPumpFillingStatusDataHolder();
        pumpFillingStatusDataHolder.addOnDataChangeListener(pumpFillingStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpFillingStatus.getPump()) {
                        item.setStatus(pumpFillingStatus.getStatus());
                        item.setStateDescription(pumpFillingStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpFillingStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpFillingStatus.getStatus()));
                        item.setNozzle(pumpFillingStatus.getNozzle());
                        item.setFuelName(pumpFillingStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpFillingStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpFillingStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpFillingStatus.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    private void initPumpEndOfTransactionStatusListener() {
        DataHolder<PumpEndOfTransactionStatus> pumpEndOfTransactionStatusDataHolder = mDataStorage.getPumpEndOfTransactionStatusDataHolder();
        pumpEndOfTransactionStatusDataHolder.addOnDataChangeListener(pumpEndOfTransactionStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpEndOfTransactionStatus.getPump()) {
                        item.setStatus(pumpEndOfTransactionStatus.getStatus());
                        item.setStateDescription(pumpEndOfTransactionStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpEndOfTransactionStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpEndOfTransactionStatus.getStatus()));
                        item.setNozzle(pumpEndOfTransactionStatus.getNozzle());
                        item.setFuelName(pumpEndOfTransactionStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpEndOfTransactionStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpEndOfTransactionStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpEndOfTransactionStatus.getAmount()));

                        // close transaction here in case if “AutoCloseTransaction” set to false

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    private void initPumpOfflineStatusListener() {
        DataHolder<PumpOfflineStatus> pumpOfflineStatusDataHolder = mDataStorage.getPumpOfflineStatusDataHolder();
        pumpOfflineStatusDataHolder.addOnDataChangeListener(pumpOfflineStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpOfflineStatus.getPump()) {
                        item.setStatus(pumpOfflineStatus.getStatus());
                        item.setStateDescription(pumpOfflineStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpOfflineStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpOfflineStatus.getStatus()));
                        item.setNozzle(pumpOfflineStatus.getNozzle());
                        item.setFuelName(pumpOfflineStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpOfflineStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpOfflineStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpOfflineStatus.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    private void initPumpTotalsListener() {
        DataHolder<PumpTotals> pumpTotalsDataHolder = mDataStorage.getPumpTotalsDataHolder();
        pumpTotalsDataHolder.addOnDataChangeListener(pumpTotals -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpTotals.getPump()) {
                        item.setStatus(pumpTotals.getStatus());
                        item.setStateDescription(pumpTotals.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpTotals.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpTotals.getStatus()));
                        item.setNozzle(pumpTotals.getNozzle());
                        item.setFuelName(pumpTotals.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpTotals.getPrice()));
                        item.setVolume(String.valueOf(pumpTotals.getVolume()));
                        item.setAmount(String.valueOf(pumpTotals.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    private void initPumpDisplayDataListener() {
        DataHolder<PumpDisplayData> pumpDisplayDataDataHolder = mDataStorage.getPumpDisplayDataDataHolder();
        pumpDisplayDataDataHolder.addOnDataChangeListener(displayData -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == displayData.getPump()) {
                        item.setStatus(displayData.getStatus());
                        item.setStateDescription(displayData.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(displayData.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(displayData.getStatus()));
                        item.setNozzle(displayData.getLastNozzle());
                        item.setFuelName(displayData.getLastFuelGradeName());
                        item.setVolume(String.valueOf(displayData.getVolume()));
                        item.setAmount(String.valueOf(displayData.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
            }
        });
    }

    public ObservableFieldsForTwoWayBinding getTwoWayFields() {
        return mTwoWayFields;
    }

    public void onRequestToNavigateToPump(PumpItem pumpItem) {
        CompletableFuture<Boolean> futureSwitchStateMachineToPumpSelectedState = CompletableFuture.supplyAsync(() -> switchStateMachineToPumpSelectedState(pumpItem));
        futureSwitchStateMachineToPumpSelectedState.thenAccept(result -> {
            if (result) {
                updateNozzlesForSelectedPumpItem(pumpItem);
                sendViewModelCommandEvent(navigateToPump.toString(), pumpItem);
            }
        });
    }

    public void updateNozzlesForSelectedPumpItem(PumpItem pumpItem) {
        if(pumpItem == null) {
            return;
        }

        List<NozzleItem> nozzleItems = new ArrayList<>();
        List<PumpNozzles> pumpNozzlesConfiguration = mDataStorage.getPumpNozzlesConfiguration();

        if(pumpNozzlesConfiguration == null) {
            return;
        }

        Optional<PumpNozzles> pumpNozzlesOptional = pumpNozzlesConfiguration.stream()
            .filter(obj -> obj.getPumpId() == pumpItem.getNumber())
            .findFirst();

        if (pumpNozzlesOptional.isPresent()) {
            PumpNozzles pumpNozzles = pumpNozzlesOptional.get();

            List<FuelGrade> fuelGradesConfiguration = mDataStorage.getFuelGradesConfiguration();
            if(fuelGradesConfiguration == null) {
                return;
            }

            List<Integer> fuelGradeIds = pumpNozzles.getFuelGradeIds();

            for (int nozzleIndex = 0; nozzleIndex < fuelGradeIds.size(); ++nozzleIndex) {
                final int finalNozzleIndex = nozzleIndex;
                Optional<FuelGrade> fuelGradeOptional = fuelGradesConfiguration.stream()
                    .filter(obj -> obj.getId() == fuelGradeIds.get(finalNozzleIndex))
                    .findFirst();

                if (fuelGradeOptional.isPresent()) {
                    FuelGrade fuelGrade = fuelGradeOptional.get();

                    NozzleItem nozzleItem = new NozzleItem();

                    nozzleItem.setNozzleNumber(finalNozzleIndex + 1);
                    nozzleItem.setFuelName(fuelGrade.getName());
                    nozzleItem.setPrice(fuelGrade.getPrice());

                    nozzleItems.add(nozzleItem);
                }
            }

            setNozzles(nozzleItems);
        }
    }

    private void chooseSelectedNozzleAutomatically(List<NozzleItem> nozzles) {
        if (nozzles != null && !nozzles.isEmpty()) {
            PumpItem selectedPumpItem = getSelectedPump().getValue();

            if(selectedPumpItem == null) {
                return;
            }

            int upNozzleNumber = selectedPumpItem.getNozzle();

            Optional<NozzleItem> selectedNozzleItemOptional = nozzles.stream()
                .filter(obj -> obj.getNozzleNumber() == upNozzleNumber)
                .findFirst();

            if (selectedNozzleItemOptional.isPresent()) {
                StateData stateData = new StateData();
                stateData.setText(String.valueOf(upNozzleNumber));
                stateData.setViewModel(this);
                stateData.setPumpItem(selectedPumpItem);

                boolean bRes = ApplicationFacade.getInstance().getStateMachine().transition(new NozzleSelectedState(), stateData);

                if(bRes) {
                    setSelectedNozzle(selectedNozzleItemOptional.get());
                }
            } else {
                setSelectedNozzle(nozzles.get(0));
            }
        }
    }

    private void updateSelectedNozzleForPumpItem(NozzleItem nozzleItem) {
        PumpItem pumpItem = getSelectedPump().getValue();
    }

    public LiveData<List<PumpItem>> getPumps() {
        return mPumps;
    }

    public void setPumps(List<PumpItem> pumps) {
        mPumps.postValue(pumps);
    }

    public LiveData<PumpItem> getSelectedPump() {
        return mTwoWayFields.getSelectedPump();
    }

    public void setSelectedPump(PumpItem selectedPump) {
        mTwoWayFields.setSelectedPump(selectedPump);
    }

    public LiveData<List<NozzleItem>> getNozzles() {
        return mNozzles;
    }

    public void setNozzles(List<NozzleItem> nozzles) {
        mNozzles.postValue(nozzles);
    }

    public LiveData<NozzleItem> getSelectedNozzle() {
        return mTwoWayFields.getSelectedNozzle();
    }

    public void setSelectedNozzle(NozzleItem selectedNozzle) {
        mTwoWayFields.setSelectedNozzle(selectedNozzle);
    }

    public PumpsRecyclerViewAdapter getPumpsRecyclerViewAdapter() {
        return mPumpsRecyclerViewAdapter;
    }

    public NozzlesRecyclerViewAdapter getNozzlesRecyclerViewAdapter() {
        return mNozzlesRecyclerViewAdapter;
    }

    public boolean switchStateMachineToPumpSelectedState(PumpItem pumpItem) {
        if(pumpItem == null) {
            return false;
        }

        StateData stateData = new StateData();
        stateData.setText(String.valueOf(pumpItem.getNumber()));
        stateData.setViewModel(this);
        stateData.setPumpItem(pumpItem);

        return ApplicationFacade.getInstance().getStateMachine().transition(new PumpSelectedState(), stateData);
    }

    public void onApplyNozzleClicked(View view) {
        StateData stateData = new StateData();
        stateData.setText(String.valueOf(Objects.requireNonNull(getSelectedNozzle().getValue()).getNozzleNumber()));
        stateData.setViewModel(this);
        stateData.setPumpItem(Objects.requireNonNull(getSelectedPump().getValue()));

        ApplicationFacade.getInstance().getStateMachine().transition(new NozzleSelectedState(), stateData);

        sendViewModelCommandEvent(navigateUp.toString(), null);
    }
}