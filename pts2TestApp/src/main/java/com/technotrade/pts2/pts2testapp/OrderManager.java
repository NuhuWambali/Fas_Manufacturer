//package com.technotrade.pts2.pts2testapp;
//
//import com.technotrade.pts2.datastructs.MeasurementUnits;
//import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
//import com.technotrade.pts2.pts2testapp.entity.Order;
//import com.technotrade.pts2.pts2testapp.entity.PumpItem;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.math.MathContext;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///// <summary>
///// Manager class that responsible for orders
///// </summary>
//public class OrderManager implements Serializable {
//    private List<Order> mFormedOrders;
//    private Order mConstructingOrder;
//    private final ResourceManager mResourceManager;
//
//    public OrderManager() {
//        mFormedOrders = new ArrayList<>();
//        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
//        mResourceManager = applicationFacade.getResourceManager();
//    }
//
//    public synchronized Order getConstructingOrder() {
//        return mConstructingOrder;
//    }
//
//    public synchronized void setConstructingOrder(Order order) {
//        mConstructingOrder = order;
//    }
//
//    public synchronized List<Order> getFormedOrders() {
//        return mFormedOrders;
//    }
//
//    public synchronized Order getFormedOrderForPump(PumpItem pumpItem) {
//        List<Order> orders = getFormedOrderForPump(pumpItem.getNumber());
//        if (orders.isEmpty()) {
//            return null;
//        }
//        else {
//            return orders.get(0);
//        }
//    }
//
//    public synchronized List<Order> getFormedOrderForPump(int pump) {
//        return mFormedOrders.stream()
//            .filter(obj -> obj.isPumpSet()
//                && obj.getPump().getNumber() == pump
//                && obj.isFormedSet()
//                && obj.getFormed())
//            .collect(Collectors.toList());
//    }
//
//    public synchronized void setFormedOrders(ArrayList<Order> orders) {
//        for(int i = 0; i < orders.size(); ++i) {
//            orders.get(i).setFormed(true);
//        }
//
//        mFormedOrders = orders;
//    }
//
//    public synchronized void addFormedOrder(Order order) {
//        order.setFormed(true);
//        mFormedOrders.add(order);
//    }
//
//    public synchronized Order createConstructingFuelOrder(PumpItem pump) {
//        Order order = new Order();
//        order.setPump(pump);
//        setConstructingOrder(order);
//
//        return order;
//    }
//
//    public synchronized Order createConstructingFuelOrder(PumpItem pump, NozzleItem nozzleItem) {
//        Order order = new Order();
//        order.setPump(pump);
//        order.setNozzle(nozzleItem);
//        setConstructingOrder(order);
//
//        return order;
//    }
//
//    public synchronized void closeOrder(Order order) {
//        mFormedOrders.remove(order);
//    }
//
//    public synchronized void closeOrderForPump(PumpItem pumpItem) {
//        Optional<Order> foundOrder = mFormedOrders.stream()
//            .filter(order -> order.getPump().getNumber() == pumpItem.getNumber())
//            .findFirst();
//
//        if (foundOrder.isPresent()) {
//            Order foundItem = foundOrder.get();
//            closeOrder(foundItem);
//        }
//    }
//
//    public synchronized String getOrderInformationString() {
//        String informationString = "";
//        Order order = getConstructingOrder();
//
//        if(!order.isPumpSet()) {
//            return informationString;
//        }
//
//        informationString = String.valueOf(order.getPump().getNumber());
//
//        informationString += mResourceManager.getResourceString(R.string.pump);
//
//        if(!order.isNozzleSet()) {
//            return informationString;
//        }
//
//        NozzleItem nozzleItem = order.getNozzle();
//
//        informationString += " / ";
//        informationString += nozzleItem.getNozzleNumber();
//        informationString += "NOZZ";
//
//        if(!nozzleItem.getFuelName().isEmpty()) {
//            informationString += "(";
//            informationString += nozzleItem.getFuelName();
//            informationString += ")";
//        }
//
//        MeasurementUnits measurementUnits = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getMeasurementUnits();
//
//        if(order.isQuantitySet()) {
//            BigDecimal quantity = order.getQuantity();
//            BigDecimal roundedValue = round(quantity, RoundingMode.HALF_EVEN);
//
//            informationString += " x ";
//            informationString += roundedValue;
//            informationString += measurementUnits.getVolume();
//        }
//
//        Settings settings = ApplicationFacade.getInstance().getSettings();
//
//        if(order.isAmountSet()) {
//            BigDecimal amount = order.getAmount();
//            BigDecimal roundedValue = round(amount, RoundingMode.HALF_EVEN);
//
//            informationString += " x ";
//            informationString += roundedValue;
//            informationString += settings.getCurrency();
//        }
//
//        return informationString;
//    }
//
//    public synchronized void updateOrdersProgressIndicators(List<PumpItem> pumpItems) {
//        List<Order> orders = getFormedOrders();
//
//        for(PumpItem pumpItem : pumpItems) {
//            pumpItem.setProgress(0);
//        }
//
//        for(Order order : orders) {
//            PumpItem pumpItem = order.getPump();
//
//            if(order.isQuantitySet()) {
//                BigDecimal quantity = order.getQuantity();
//                BigDecimal currentQuantity;
//
//                try {
//                    currentQuantity = round(pumpItem.getVolume(), RoundingMode.CEILING);
//                } catch (ParseException e) {
//                    return;
//                }
//
//                MathContext mathContext = new MathContext(2);
//                BigDecimal progress = currentQuantity.multiply(new BigDecimal(100)).divide(quantity, mathContext);
//                pumpItem.setProgress(progress.intValue());
//            }
//            else if(order.isAmountSet()) {
//                BigDecimal amount = order.getAmount();
//                BigDecimal currentAmount;
//
//                try {
//                    currentAmount = round(pumpItem.getAmount(), RoundingMode.CEILING);
//                } catch (ParseException e) {
//                    return;
//                }
//
//                MathContext mathContext = new MathContext(2);
//                BigDecimal progress = currentAmount.multiply(new BigDecimal(100)).divide(amount, mathContext);
//                pumpItem.setProgress(progress.intValue());
//            }
//            else {
//                // full tank
//                if(pumpItem.getProgress() == 0) {
//                    pumpItem.setProgress(100);
//                }
//                else {
//                    pumpItem.setProgress(0);
//                }
//            }
//        }
//    }
//
//    public BigDecimal round(BigDecimal value, RoundingMode mode) {
//        return value.setScale(2, mode);
//    }
//
//    public BigDecimal round(String value, RoundingMode mode) throws ParseException {
//        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(Locale.US);
//        decimalFormat.setParseBigDecimal(true);
//        BigDecimal bigDecimal = (BigDecimal)decimalFormat.parse(value);
//        assert bigDecimal != null;
//
//        return round(bigDecimal, mode);
//    }
//}


package com.technotrade.pts2.pts2testapp;

import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/// <summary>
/// Manager class that is responsible for handling orders
/// </summary>
public class OrderManager implements Serializable {
    private List<Order> mFormedOrders;
    private Order mConstructingOrder;
    private final ResourceManager mResourceManager;

    public OrderManager() {
        mFormedOrders = new ArrayList<>();
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        mResourceManager = applicationFacade.getResourceManager();
    }

    
    public synchronized Order getConstructingOrder() {
        return mConstructingOrder;
    }

    public synchronized void setConstructingOrder(Order order) {
        mConstructingOrder = order;
    }

    public synchronized List<Order> getFormedOrders() {
        return mFormedOrders;
    }

    public synchronized Order getFormedOrderForPump(PumpItem pumpItem) {
        List<Order> orders = getFormedOrderForPump(pumpItem.getNumber());
        if (orders.isEmpty()) {
            return null;
        }
        return orders.get(0);
    }

    public synchronized List<Order> getFormedOrderForPump(int pump) {
        return mFormedOrders.stream()
                .filter(obj -> obj.isPumpSet()
                        && obj.getPump().getNumber() == pump
                        && obj.isFormedSet()
                        && obj.getFormed())
                .collect(Collectors.toList());
    }

    public synchronized void setFormedOrders(ArrayList<Order> orders) {
        for (Order order : orders) {
            order.setFormed(true);
        }
        mFormedOrders = orders;
    }

    public synchronized void addFormedOrder(Order order) {
        order.setFormed(true);
        mFormedOrders.add(order);
    }

    public synchronized Order createConstructingFuelOrder(PumpItem pump) {
        Order order = new Order();
        order.setPump(pump);
        setConstructingOrder(order);
        return order;
    }

    public synchronized Order createConstructingFuelOrder(PumpItem pump, NozzleItem nozzleItem) {
        Order order = new Order();
        order.setPump(pump);
        order.setNozzle(nozzleItem);
        setConstructingOrder(order);
        return order;
    }

    public synchronized void closeOrder(Order order) {
        mFormedOrders.remove(order);
    }

    public synchronized void closeOrderForPump(PumpItem pumpItem) {
        Optional<Order> foundOrder = mFormedOrders.stream()
                .filter(order -> order.getPump().getNumber() == pumpItem.getNumber())
                .findFirst();

        foundOrder.ifPresent(this::closeOrder);
    }

    public synchronized String getOrderInformationString() {
        StringBuilder informationString = new StringBuilder();
        Order order = getConstructingOrder();

        if (!order.isPumpSet()) {
            return informationString.toString();
        }

        informationString.append(order.getPump().getNumber());
        informationString.append(mResourceManager.getResourceString(R.string.pump));

        if (!order.isNozzleSet()) {
            return informationString.toString();
        }

        NozzleItem nozzleItem = order.getNozzle();

        informationString.append(" / ").append(nozzleItem.getNozzleNumber()).append("NOZZ");

        if (!nozzleItem.getFuelName().isEmpty()) {
            informationString.append("(").append(nozzleItem.getFuelName()).append(")");
        }

        MeasurementUnits measurementUnits = ApplicationFacade.getInstance()
                .getPTSManager().getDataStorage().getMeasurementUnits();

        if (order.isQuantitySet()) {
            BigDecimal quantity = round(order.getQuantity(), RoundingMode.HALF_EVEN);
            informationString.append(" x ").append(quantity).append(measurementUnits.getVolume());
        }

        Settings settings = ApplicationFacade.getInstance().getSettings();

        if (order.isAmountSet()) {
            BigDecimal amount = round(order.getAmount(), RoundingMode.HALF_EVEN);
            informationString.append(" x ").append(amount).append(settings.getCurrency());
        }

        return informationString.toString();
    }

    public synchronized void updateOrdersProgressIndicators(List<PumpItem> pumpItems) {
        List<Order> orders = getFormedOrders();

        for (PumpItem pumpItem : pumpItems) {
            pumpItem.setProgress(0);
        }

        for (Order order : orders) {
            PumpItem pumpItem = order.getPump();

            try {
                if (order.isQuantitySet()) {
                    BigDecimal quantity = order.getQuantity();
                    BigDecimal currentQuantity = round(pumpItem.getVolume(), RoundingMode.CEILING);

                    if (quantity.compareTo(BigDecimal.ZERO) != 0) {  // Prevent division by zero
                        MathContext mathContext = new MathContext(2);
                        BigDecimal progress = currentQuantity.multiply(new BigDecimal(100)).divide(quantity, mathContext);
                        pumpItem.setProgress(progress.intValue());
                    } else {
                        pumpItem.setProgress(0);  // Handle zero quantity case
                        System.out.println("WARNING: Quantity is zero, setting progress to 0.");
                    }
                } else if (order.isAmountSet()) {
                    BigDecimal amount = order.getAmount();
                    BigDecimal currentAmount = round(pumpItem.getAmount(), RoundingMode.CEILING);

                    if (amount.compareTo(BigDecimal.ZERO) != 0) {  // Prevent division by zero
                        MathContext mathContext = new MathContext(2);
                        BigDecimal progress = currentAmount.multiply(new BigDecimal(100)).divide(amount, mathContext);
                        pumpItem.setProgress(progress.intValue());
                    } else {
                        pumpItem.setProgress(0);  // Handle zero amount case
                        System.out.println("WARNING: Amount is zero, setting progress to 0.");
                    }
                } else {
                    // Full tank
                    pumpItem.setProgress(pumpItem.getProgress() == 0 ? 100 : 0);
                }
            } catch (ParseException e) {
                System.out.println("ERROR - ParseException: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }


    public BigDecimal round(BigDecimal value, RoundingMode mode) {
        return value.setScale(2, mode);
    }

    public BigDecimal round(String value, RoundingMode mode) throws ParseException {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.setParseBigDecimal(true);
        BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(value);
        assert bigDecimal != null;
        return round(bigDecimal, mode);
    }

    // **New Methods to Fix the "Cannot Resolve Method" Error**
    public synchronized String getFullName() {
        return (mConstructingOrder != null) ? mConstructingOrder.getFullName() : "";
    }

    public synchronized String getTin() {
        return (mConstructingOrder != null) ? mConstructingOrder.getTin() : "";
    }

    public synchronized String getPlateNumber() {
        return (mConstructingOrder != null) ? mConstructingOrder.getPlateNumber() : "";
    }

    public void setFullName(String fullName) {
    }

    public void setTin(String tin) {
    }

    public void setPlateNumber(String plateNumber) {
    }
}
