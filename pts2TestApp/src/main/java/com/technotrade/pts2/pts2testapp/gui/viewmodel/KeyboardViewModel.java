package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.datastructs.PumpOfflineStatus.Flag.Request;

import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;
import com.technotrade.pts2.pts2testapp.statemachine.StateMachine;
import com.technotrade.pts2.pts2testapp.statemachine.states.CurrencySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.FuelingState;
import com.technotrade.pts2.pts2testapp.statemachine.states.NozzleSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.QuantitySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.StoppingState;
import com.android.volley.Request;

public class KeyboardViewModel extends BaseViewModel {

    private final MutableLiveData<String> mMonitorTop;
    private final MutableLiveData<String> mMonitorBottom;
    private final StateMachine mStateMachine;
    private final PTSManager mPTSManager;
    private final OrderManager mOrderManager;

    public KeyboardViewModel() {
        final MutableLiveData<String> mFullName = new MutableLiveData<>();
        mMonitorTop = new MutableLiveData<String>("");
        mMonitorBottom = new MutableLiveData<String>("0");
        mPTSManager = ApplicationFacade.getInstance().getPTSManager();
        mOrderManager = ApplicationFacade.getInstance().getOrderManager();
        mStateMachine = ApplicationFacade.getInstance().getStateMachine();
    }

    public void onStart() {
        updateMonitorTop();
    }

    public void onStop() {
    }

    public MutableLiveData<String> getMonitorTop() {
        return mMonitorTop;
    }

    public MutableLiveData<String> getMonitorBottom() {
        return mMonitorBottom;
    }

    public void onButtonPumpClicked(View view) {

        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new PumpSelectedState(), stateData);

        mOrderManager.getConstructingOrder().addOnDataChangeListener(orderId -> {
            updateMonitorTop();
        });

        if (bRes) {
            updateMonitorTop();
            clearMonitorBottom();
        }
    }

    public void onButtonNozzleClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new NozzleSelectedState(), stateData);

        if (bRes) {
            updateMonitorTop();
            clearMonitorBottom();
        }
    }

    public void onButtonVolumeClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new QuantitySelectedState(), stateData);

        if (bRes) {
            updateMonitorTop();
            clearMonitorBottom();
        }
    }

    public void onButtonAmountClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new CurrencySelectedState(), stateData);

        if (bRes) {
            updateMonitorTop();
            clearMonitorBottom();
        }
    }

    public void onButtonEnterClicked(View view) {
        View pumpView = view.getRootView();

        JSONObject jsonData = prepareJsonData(pumpView);
        System.out.println("JSON Data: " + jsonData);

        // Prepare state data and send transition
        StateData stateData = prepareStateData();
        mStateMachine.transition(new FuelingState(), stateData);
        System.out.println(stateData);
    }




//    public void onButtonEnterClicked(View view) {
//        String userInput = mMonitorBottom.getValue();
//
//        // Fetch user info again when OK is pressed
//        String fullName = mPTSManager.getDataStorage().getSelectedPump().getFullName();
//        String tin = mPTSManager.getDataStorage().getSelectedPump().getTin();
//        String plateNumber = mPTSManager.getDataStorage().getSelectedPump().getPlateNumber();
//
//        System.out.println("DEBUG - Fetched FullName: " + fullName);
//        System.out.println("DEBUG - Fetched TIN: " + tin);
//        System.out.println("DEBUG - Fetched PlateNumber: " + plateNumber);
//
//        StateData stateData = prepareStateData();
//        mStateMachine.transition(new FuelingState(), stateData);
//        System.out.println(stateData);
//    }

    public void onButtonClearClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new StoppingState(), stateData);

        updateMonitorTop();
        clearMonitorBottom();
    }

    public void stopSelectedPump(PumpItem pumpItem) {
        StateData stateData = prepareStateData();
        stateData.setPumpItem(pumpItem);
        stateData.setConfirmed(true);

        boolean bRes = mStateMachine.transition(new StoppingState(), stateData);

        if (bRes) {
            updateMonitorTop();
            clearMonitorBottom();
        }
    }

    public void setFullName(String fullName) {
        mOrderManager.setFullName(fullName);
    }

    public void setTin(String tin) {
        mOrderManager.setTin(tin);
    }

    public void setPlateNumber(String plateNumber) {
        mOrderManager.setPlateNumber(plateNumber);
    }

    public void onButtonOneClicked(View view) {
        addCharacter('1');
    }

    public void onButtonTwoClicked(View view) {
        addCharacter('2');
    }

    public void onButtonThreeClicked(View view) {
        addCharacter('3');
    }

    public void onButtonFourClicked(View view) {
        addCharacter('4');
    }

    public void onButtonFiveClicked(View view) {
        addCharacter('5');
    }

    public void onButtonSixClicked(View view) {
        addCharacter('6');
    }

    public void onButtonSevenClicked(View view) {
        addCharacter('7');
    }

    public void onButtonEightClicked(View view) {
        addCharacter('8');
    }

    public void onButtonNineClicked(View view) {
        addCharacter('9');
    }

    public void onButtonZeroClicked(View view) {
        addCharacter('0');
    }

    public void onButtonTwoZerosClicked(View view) {
        addCharacters("00");
    }

    public void onButtonDotClicked(View view) {
        addCharacter('.');
    }

    public void onButtonBackspaceClicked(View view) {
        clearLastCharacter();
    }

    private void addCharacter(char charakter) {
        if (!"0123456789.".contains(String.valueOf(charakter))) {
            return;
        }

        String curText = mMonitorBottom.getValue();
        assert curText != null;

        if (charakter == '.' && curText.contains(".")) {
            return;
        }

        if (curText.equals(String.valueOf(0))) {
            curText = "";
        }

        curText += charakter;
        System.out.println("KEY BOARD BEFORE :"+curText);
        mMonitorBottom.postValue(curText);

        System.out.println("KEY BOARD AFTER :"+mMonitorBottom.getValue());
    }

    private void addCharacters(String charakters) {
        for (int i = 0; i < charakters.length(); ++i) {
            addCharacter(charakters.charAt(i));
        }
    }

    private void clearLastCharacter() {
        String curText = mMonitorBottom.getValue();
        assert curText != null;

        curText = curText.substring(0, curText.length() - 1);

        if (curText.isEmpty()) {
            curText = String.valueOf(0);
        }

        mMonitorBottom.postValue(curText);
    }

    public void clearMonitorBottom() {
        mMonitorBottom.postValue(String.valueOf(0));
    }

    private void updateMonitorTop() {
        mMonitorTop.postValue(mOrderManager.getOrderInformationString());
    }

    private StateData prepareStateData() {
        StateData stateData = new StateData();
        stateData.setText(mMonitorBottom.getValue());
        stateData.setViewModel(this);
        stateData.setPumpItem(mPTSManager.getDataStorage().getSelectedPump());

        stateData.setFullName(mOrderManager.getFullName());
        stateData.setTin(mOrderManager.getTin());
        stateData.setPlateNumber(mOrderManager.getPlateNumber());

        // Generate JSON and print it
//        JSONObject jsonData = prepareJsonData();
//        System.out.println("Data Sent to State Machine: " + jsonData.toString());

        return stateData;
    }

//    private JSONObject prepareJsonData(View rootView) {
//        JSONObject jsonObject = new JSONObject();
//        JSONObject dataObject = new JSONObject();  // "data" object
//        JSONObject customerObject = new JSONObject();  // "customer" object
//
//        try {
//            // Retrieve the nozzle and price from the UI
//            String nozzle = ((EditText) rootView.findViewById(R.id.etNozzle)).getText().toString().trim();
//            String amount = ((EditText) rootView.findViewById(R.id.etAmount)).getText().toString().trim();
//
//            // Fetch user input for customer details (TIN, Name, PlateNumber)
//            String tin = ((EditText) rootView.findViewById(R.id.etTIN)).getText().toString().trim();
//            String fullName = ((EditText) rootView.findViewById(R.id.etFullName)).getText().toString().trim();
//            String plateNumber = ((EditText) rootView.findViewById(R.id.etPlateNumber)).getText().toString().trim();
//
//            // Get the selected pump
//            PumpItem selectedPump = mPTSManager.getDataStorage().getSelectedPump();
//            if (selectedPump == null) {
//                System.out.println("ERROR - No pump selected!");
//                return jsonObject;
//            }
//
//            // Convert values to proper types
//            int parsedNozzle = nozzle.isEmpty() ? 0 : Integer.parseInt(nozzle);
//            double parsedAmount = amount.isEmpty() ? 0.0 : Double.parseDouble(amount);
//
//            dataObject.put("pump", selectedPump.getNumber());
//            dataObject.put("nozzle", parsedNozzle);
//            dataObject.put("type", "string");
//            dataObject.put("dose", 1);
//            dataObject.put("amount", parsedAmount);
//            customerObject.put("tin", tin);
//            customerObject.put("name", fullName);
//            customerObject.put("plateNumber", plateNumber);
//
//            jsonObject.put("data", dataObject);
//            jsonObject.put("customer", customerObject);
//
//            System.out.println("Generated JSON: " + jsonObject.toString());
//
//
//            sendPumpAuthorizationRequest(jsonObject, rootView);
//
//        } catch (JSONException | NumberFormatException e) {
//            e.printStackTrace();
//            System.out.println("ERROR - JSON creation failed: " + e.getMessage());
//        }
//        return jsonObject;
//    }
private JSONObject prepareJsonData(View rootView) {
    JSONObject jsonObject = new JSONObject();
    JSONObject dataObject = new JSONObject();  // "data" object
    JSONObject customerObject = new JSONObject();  // "customer" object

    try {
        // Retrieve the nozzle, price (amount), and volume from the UI
        String nozzle = ((EditText) rootView.findViewById(R.id.etNozzle)).getText().toString().trim();
        String amount = ((EditText) rootView.findViewById(R.id.etAmount)).getText().toString().trim();
        String volume = ((EditText) rootView.findViewById(R.id.etVolume)).getText().toString().trim();

        // Fetch user input for customer details (TIN, Name, PlateNumber)
        String tin = ((EditText) rootView.findViewById(R.id.etTIN)).getText().toString().trim();
        String fullName = ((EditText) rootView.findViewById(R.id.etFullName)).getText().toString().trim();
        String plateNumber = ((EditText) rootView.findViewById(R.id.etPlateNumber)).getText().toString().trim();

        // Get the selected pump
        PumpItem selectedPump = mPTSManager.getDataStorage().getSelectedPump();
        if (selectedPump == null) {
            System.out.println("ERROR - No pump selected!");
            return jsonObject;
        }

        // Convert values to proper types
        int parsedNozzle = nozzle.isEmpty() ? 0 : Integer.parseInt(nozzle);
        double parsedAmount = amount.isEmpty() ? 0.0 : Double.parseDouble(amount);
        double parsedVolume = volume.isEmpty() ? 0.0 : Double.parseDouble(volume);

        // Declare variables for type and dose
        String type = "";
        double dose = 0.0;

        // Check if both volume and amount are provided (invalid state)
        if (!volume.isEmpty() && !amount.isEmpty()) {
            System.out.println("ERROR - Both volume and amount cannot be filled at the same time.");
            return jsonObject; // Returning empty json in case of invalid input
        }

        // Clear the other field when one is filled (to ensure only one is filled)
        if (!volume.isEmpty()) {
            // If volume is filled, clear the amount field
            ((EditText) rootView.findViewById(R.id.etAmount)).setText("");
            type = "Volume";
            dose = parsedVolume;  // Dose will be set to volume if volume is selected
        } else if (!amount.isEmpty()) {
            // If amount is filled, clear the volume field
            ((EditText) rootView.findViewById(R.id.etVolume)).setText("");
            type = "Amount";
            dose = parsedAmount;  // Dose will be set to amount if amount is selected
        }

        // Populate the "data" object
        dataObject.put("pump", selectedPump.getNumber());
        dataObject.put("nozzle", parsedNozzle);
        dataObject.put("type", type);  // Set type to either "Volume" or "Amount"
        dataObject.put("dose", dose);  // Set the correct dose based on user input
//        dataObject.put("price", 2000);

        // Populate the "customer" object
        customerObject.put("tin", tin);
        customerObject.put("name", fullName);
        customerObject.put("plateNumber", plateNumber);

        // Add data to the main JSON object
        jsonObject.put("data", dataObject);
        jsonObject.put("customer", customerObject);

        System.out.println("Generated JSON: " + jsonObject.toString());

        // Send the generated JSON to the API
        sendPumpAuthorizationRequest(jsonObject, rootView);

    } catch (JSONException | NumberFormatException e) {
        e.printStackTrace();
        System.out.println("ERROR - JSON creation failed: " + e.getMessage());
    }
    return jsonObject;
}



    private void sendPumpAuthorizationRequest(JSONObject jsonData, View rootView) {
        String url = "http://192.168.100.178:5301/api/pump/authorize";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                1,  // POST method
                url,
                jsonData,
                response -> {
       
                    System.out.println("Response: " + response.toString());
                },
                error -> {
                    // Print the error response
                    System.out.println("Error: " + error.toString());
                    if (error.networkResponse != null) {
                        System.out.println("Error Code: " + error.networkResponse.statusCode);
                        System.out.println("Error Data: " + new String(error.networkResponse.data));
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(rootView.getContext());
        requestQueue.add(jsonObjectRequest);
    }






    public void onNozzleSelected(int nozzleNumber) {
        PumpItem selectedPump = mPTSManager.getDataStorage().getSelectedPump();

        if (selectedPump != null) {
            // Update the nozzle number
            selectedPump.setNozzle(nozzleNumber);

            // 🔥 Ensure the updated pump is saved back
            mPTSManager.getDataStorage().setSelectedPump(selectedPump);

            // Debugging log to confirm update
            System.out.println("DEBUG - Nozzle Updated to: " + selectedPump.getNozzle());

            // 🔥 Force UI/state refresh if needed (update LiveData, ViewModel, etc.)
        } else {
            System.out.println("ERROR - No pump selected when setting nozzle!");
        }
    }


}


