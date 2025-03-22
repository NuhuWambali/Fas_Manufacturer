//package com.technotrade.pts2.pts2testapp.entity;
//
//import com.technotrade.pts2.pts2testapp.listener.DataChangeListener;
//import com.technotrade.pts2.util.Flags;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Order {
//    private enum Flag {
//        Formed,
//        Pump,
//        Nozzle,
//        Quantity,
//        Amount
//    };
//    private int mId;
//    private boolean mFormed;
//    private PumpItem mPump;
//    private NozzleItem mNozzle;
//    BigDecimal mQuantity;
//    BigDecimal mAmount;
//    private final Flags<Order.Flag> mFlags = new Flags<>();
//    private List<DataChangeListener<Integer>> mListeners;
//
//    public Order() {
//        mListeners = new ArrayList<>();
//    }
//    public boolean getFormed() {
//        return mFormed;
//    }
//
//    public void setFormed(boolean formed) {
//        mFormed = formed;
//        mFlags.setFlag(Order.Flag.Formed);
//        dataChanged(mId);
//    }
//
//    public boolean isFormedSet() { return mFlags.isFlagSet(Order.Flag.Formed); }
//
//    public void resetFormed() { mFlags.resetFlag(Order.Flag.Formed); }
//
//    public PumpItem getPump() {
//        return mPump;
//    }
//
//    public void setPump(PumpItem pump) {
//        mPump = pump;
//        mFlags.setFlag(Order.Flag.Pump);
//        dataChanged(mId);
//    }
//
//    public boolean isPumpSet() { return mFlags.isFlagSet(Order.Flag.Pump); }
//
//    public void resetPump() { mFlags.resetFlag(Order.Flag.Pump); }
//
//    public NozzleItem getNozzle() {
//        return mNozzle;
//    }
//
//    public void setNozzle(NozzleItem nozzle) {
//        mNozzle = nozzle;
//        mFlags.setFlag(Flag.Nozzle);
//        dataChanged(mId);
//    }
//
//    public boolean isNozzleSet() { return mFlags.isFlagSet(Order.Flag.Nozzle); }
//
//    public void resetNozzle() { mFlags.resetFlag(Order.Flag.Nozzle); }
//
//    public BigDecimal getQuantity() {
//        return mQuantity;
//    }
//
//    public void setQuantity(BigDecimal quantity) {
//        mQuantity = quantity;
//        mFlags.setFlag(Order.Flag.Quantity);
//        dataChanged(mId);
//    }
//
//    public boolean isQuantitySet() { return mFlags.isFlagSet(Order.Flag.Quantity); }
//
//    public void resetQuantity() { mFlags.resetFlag(Order.Flag.Quantity); }
//
//    public BigDecimal getAmount() {
//        return mAmount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        mAmount = amount;
//        mFlags.setFlag(Flag.Amount);
//        dataChanged(mId);
//    }
//
//    public boolean isAmountSet() { return mFlags.isFlagSet(Order.Flag.Amount); }
//
//    public void resetAmount() { mFlags.resetFlag(Order.Flag.Amount); }
//
//    public void addOnDataChangeListener(DataChangeListener<Integer> listener) {
//        if (!mListeners.contains(listener)) {
//            mListeners.add(listener);
//        }
//    }
//
//    public void removeOnDataChangeListener(DataChangeListener<Integer> listener) {
//        mListeners.remove(listener);
//    }
//
//    public void dataChanged(int orderId) {
//        for (DataChangeListener<Integer> listener : mListeners) {
//            listener.onDataChanged(orderId);
//        }
//    }
//}
package com.technotrade.pts2.pts2testapp.entity;

import com.technotrade.pts2.pts2testapp.listener.DataChangeListener;
import com.technotrade.pts2.util.Flags;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private enum Flag {
        Formed,
        Pump,
        Nozzle,
        Quantity,
        Amount,
        FullName,
        Tin,
        PlateNumber
    }

    private int mId;
    private boolean mFormed;
    private PumpItem mPump;
    private NozzleItem mNozzle;
    private BigDecimal mQuantity;
    private BigDecimal mAmount;
    private String fullName;
    private String tin;
    private String plateNumber;

    private final Flags<Order.Flag> mFlags = new Flags<>();
    private List<DataChangeListener<Integer>> mListeners;

    public Order() {
        mListeners = new ArrayList<>();
    }

    public boolean getFormed() {
        return mFormed;
    }

    public void setFormed(boolean formed) {
        mFormed = formed;
        mFlags.setFlag(Order.Flag.Formed);
        dataChanged(mId);
    }

    public boolean isFormedSet() { return mFlags.isFlagSet(Order.Flag.Formed); }

    public void resetFormed() { mFlags.resetFlag(Order.Flag.Formed); }

    public PumpItem getPump() {
        return mPump;
    }

    public void setPump(PumpItem pump) {
        mPump = pump;
        mFlags.setFlag(Order.Flag.Pump);
        dataChanged(mId);
    }

    public boolean isPumpSet() { return mFlags.isFlagSet(Order.Flag.Pump); }

    public void resetPump() { mFlags.resetFlag(Order.Flag.Pump); }

    public NozzleItem getNozzle() {
        return mNozzle;
    }

    public void setNozzle(NozzleItem nozzle) {
        mNozzle = nozzle;
        mFlags.setFlag(Flag.Nozzle);
        dataChanged(mId);
    }

    public boolean isNozzleSet() { return mFlags.isFlagSet(Order.Flag.Nozzle); }

    public void resetNozzle() { mFlags.resetFlag(Order.Flag.Nozzle); }

    public BigDecimal getQuantity() {
        return mQuantity;
    }

    public void setQuantity(BigDecimal quantity) {
        mQuantity = quantity;
        mFlags.setFlag(Order.Flag.Quantity);
        dataChanged(mId);
    }

    public boolean isQuantitySet() { return mFlags.isFlagSet(Order.Flag.Quantity); }

    public void resetQuantity() { mFlags.resetFlag(Order.Flag.Quantity); }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
        mFlags.setFlag(Flag.Amount);
        dataChanged(mId);
    }

    public boolean isAmountSet() { return mFlags.isFlagSet(Order.Flag.Amount); }

    public void resetAmount() { mFlags.resetFlag(Order.Flag.Amount); }

    // New fields: full_name, tin, plate_number
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        mFlags.setFlag(Flag.FullName);
        dataChanged(mId);
    }

    public boolean isFullNameSet() { return mFlags.isFlagSet(Flag.FullName); }

    public void resetFullName() { mFlags.resetFlag(Flag.FullName); }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
        mFlags.setFlag(Flag.Tin);
        dataChanged(mId);
    }

    public boolean isTinSet() { return mFlags.isFlagSet(Flag.Tin); }

    public void resetTin() { mFlags.resetFlag(Flag.Tin); }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
        mFlags.setFlag(Flag.PlateNumber);
        dataChanged(mId);
    }

    public boolean isPlateNumberSet() { return mFlags.isFlagSet(Flag.PlateNumber); }

    public void resetPlateNumber() { mFlags.resetFlag(Flag.PlateNumber); }

    public void addOnDataChangeListener(DataChangeListener<Integer> listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeOnDataChangeListener(DataChangeListener<Integer> listener) {
        mListeners.remove(listener);
    }

    public void dataChanged(int orderId) {
        for (DataChangeListener<Integer> listener : mListeners) {
            listener.onDataChanged(orderId);
        }
    }
}
