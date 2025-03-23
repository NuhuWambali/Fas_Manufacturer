package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.datastructs.PumpOfflineStatus.Flag.Request;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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
        JSONObject dataObject = new JSONObject();
        JSONObject customerObject = new JSONObject();

        try {
            // Retrieve inputs
            EditText etNozzle = rootView.findViewById(R.id.etNozzle);
            EditText etAmount = rootView.findViewById(R.id.etAmount);
            EditText etVolume = rootView.findViewById(R.id.etVolume);
            EditText etTIN = rootView.findViewById(R.id.etTIN);
            EditText etFullName = rootView.findViewById(R.id.etFullName);
            EditText etPlateNumber = rootView.findViewById(R.id.etPlateNumber);

            String nozzle = etNozzle.getText().toString().trim();
            String amount = etAmount.getText().toString().trim();
            String volume = etVolume.getText().toString().trim();
            String tin = etTIN.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String plateNumber = etPlateNumber.getText().toString().trim();

            // TIN validation: Must be exactly 9 digits and contain only numbers from 0 to 9
            if (!tin.matches("^[0-9]{9}$")) {
                showColoredAlert(rootView, "Invalid TIN", "TIN must be exactly 9 digits and contain only numbers from 0 to 9.", Color.RED);
                return jsonObject;
            }

            // Get the selected pump
            PumpItem selectedPump = mPTSManager.getDataStorage().getSelectedPump();
            if (selectedPump == null) {
                showColoredAlert(rootView, "Error", "No pump selected!", Color.RED);
                return jsonObject;
            }

            // Convert inputs to proper types
            int parsedNozzle = nozzle.isEmpty() ? 0 : Integer.parseInt(nozzle);
            double parsedAmount = amount.isEmpty() ? 0.0 : Double.parseDouble(amount);
            double parsedVolume = volume.isEmpty() ? 0.0 : Double.parseDouble(volume);

            // Nozzle must be greater than 0
            if (parsedNozzle <= 0) {
                showColoredAlert(rootView, "Invalid Input", "Nozzle must be greater than 0.", Color.RED);
                return jsonObject;
            }

            // Logic to handle inputs
            String type;
            double dose;

            if (amount.isEmpty() && volume.isEmpty()) {
                etAmount.setText("");
                etVolume.setText("");
                type = "FullTank";
                dose = 0.0;

                // Show confirmation dialog before proceeding
                new AlertDialog.Builder(rootView.getContext())
                        .setTitle("Confirm Full Tank")
                        .setMessage("You are about to fill the full tank. Do you want to proceed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Proceed with the transaction
                            proceedWithTransaction(rootView, jsonObject, dataObject, customerObject, selectedPump, parsedNozzle, type, dose, tin, fullName, plateNumber);
                        })
                        .setNegativeButton("No", null)  // Do nothing on No
                        .show();

            } else {
                // If there is a volume or amount, ensure only one field is filled
                if (!volume.isEmpty() && !amount.isEmpty()) {
                    type = "";
                    dose = 0.0;
                    showColoredAlert(rootView, "Invalid Input", "Both volume and amount cannot be filled at the same time.", Color.RED);
                    return jsonObject;
                } else if (!volume.isEmpty()) {
                    etAmount.setText("");  // Clear amount if volume is selected
                    type = "Volume";
                    dose = parsedVolume;
                } else if (!amount.isEmpty()) {
                    etVolume.setText("");  // Clear volume if amount is selected
                    type = "Amount";
                    dose = parsedAmount;
                } else {
                    type = "";
                    dose = 0.0;
                }
            }

            proceedWithTransaction(rootView, jsonObject, dataObject, customerObject, selectedPump, parsedNozzle, type, dose, tin, fullName, plateNumber);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showColoredAlert(rootView, "Error", "JSON creation failed: " + e.getMessage(), Color.RED);
        }

        return jsonObject;
    }

    private void proceedWithTransaction(View rootView, JSONObject jsonObject, JSONObject dataObject, JSONObject customerObject, PumpItem selectedPump, int parsedNozzle, String type, double dose, String tin, String fullName, String plateNumber) {
        try {
            // Fill the data object
            dataObject.put("pump", selectedPump.getNumber());
            dataObject.put("nozzle", parsedNozzle);
            dataObject.put("type", type);
            dataObject.put("dose", dose);

            // Populate the customer object
            customerObject.put("tin", tin);
            customerObject.put("name", fullName);
            customerObject.put("plateNumber", plateNumber);

            // Add data to the main JSON object
            jsonObject.put("data", dataObject);
            jsonObject.put("customer", customerObject);

            System.out.println("Generated JSON: " + jsonObject.toString());

            // Send the generated JSON to the API
            sendPumpAuthorizationRequest(jsonObject, rootView);

        } catch (JSONException e) {
            e.printStackTrace();
            showColoredAlert(rootView, "Error", "Transaction failed: " + e.getMessage(), Color.RED);
        }
    }






    // Helper method to show colored alerts
    private void showColoredAlert(View rootView, String title, String message, int color) {
        SpannableString coloredMessage = new SpannableString(message);
        coloredMessage.setSpan(new ForegroundColorSpan(color), 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        new AlertDialog.Builder(rootView.getContext())
                .setTitle(title)
                .setMessage(coloredMessage)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    // Helper method to show alerts
    private void showAlert(View rootView, String title, String message) {
        new AlertDialog.Builder(rootView.getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void sendPumpAuthorizationRequest(JSONObject jsonData, View rootView) {
        // Create the confirmation dialog
        new AlertDialog.Builder(rootView.getContext())
                .setTitle("Confirm Transaction")
                .setMessage("Do you want to proceed with this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    String url = "http://192.168.100.8:5301/api/pump/authorize";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            1,
                            url,
                            jsonData,
                            response -> {
                                try {
                                    String responseMessage = response.getString("message");

                                    // Log the server response
                                    System.out.println("Server Response: " + responseMessage);

                                    if ("Success".equalsIgnoreCase(responseMessage)) {
                                        JSONObject successDetails = response.getJSONObject("ptsPacketResponse");

                                        System.out.println("Transaction Success: " + successDetails.toString());

                                        showAlert(rootView, "Transaction Success", "Filling in progress...", Color.BLACK);
                                        clearInputs(rootView);
                                    } else if ("Failure".equalsIgnoreCase(responseMessage)) {
                                        JSONObject errorDetails = response.getJSONObject("ptsPacketResponse");
                                        String errorMessage = errorDetails.optString("message", "Unknown Error Occurred");

                                        // Log the error details
                                        System.out.println("Transaction Failure: " + errorMessage);

                                        showAlert(rootView, "Transaction Failed", "Error: " + errorMessage, Color.RED);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // Log the exception message
                                    System.out.println("Error in Response: " + e.getMessage());

                                    showAlert(rootView, "Transaction Failed", "Invalid server response", Color.RED);
                                }
                            },
                            error -> {
                                String errorMessage = "An unexpected error occurred.";
                                if (error.networkResponse != null) {
                                    // Server response with error
                                    int statusCode = error.networkResponse.statusCode;
                                    String errorData = new String(error.networkResponse.data);

                                    // Log the error response
                                    System.out.println("Error Code: " + statusCode);
                                    System.out.println("Error Data: " + errorData);

                                    try {
                                        JSONObject errorJson = new JSONObject(errorData);
                                        if (errorJson.has("ptsPacketResponse")) {
                                            JSONObject errorDetails = errorJson.getJSONObject("ptsPacketResponse");
                                            errorMessage = errorDetails.optString("message", "Unknown server error");

                                            // Log the error details
                                            System.out.println("Error Message from server: " + errorMessage);
                                        } else {
                                            errorMessage = errorJson.optString("message", "Unknown server error");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        errorMessage = "Failed to parse error response.";
                                    }
                                } else if (error instanceof TimeoutError) {
                                    errorMessage = "Request timed out. Please try again.";
                                } else if (error instanceof NoConnectionError) {
                                    errorMessage = "No internet connection. Check your network.";
                                } else if (error instanceof AuthFailureError) {
                                    errorMessage = "Authorization failed.";
                                } else if (error instanceof ServerError) {
                                    errorMessage = "Server error. Try again later.";
                                } else if (error instanceof NetworkError) {
                                    errorMessage = "Network error occurred.";
                                } else if (error instanceof ParseError) {
                                    errorMessage = "Response parsing failed.";
                                }

                                // Log the error message
                                System.out.println("Transaction Failed: " + errorMessage);

                                showAlert(rootView, "Transaction Failed", "Error: " + errorMessage, Color.RED);
                            }
                    );

                    RequestQueue requestQueue = Volley.newRequestQueue(rootView.getContext());
                    requestQueue.add(jsonObjectRequest);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())  // Dismiss if "No"
                .show();
    }


    private void clearInputs(View rootView) {
        // Clear the values of the EditText fields
        ((EditText) rootView.findViewById(R.id.etNozzle)).setText("");
        ((EditText) rootView.findViewById(R.id.etAmount)).setText("");
        ((EditText) rootView.findViewById(R.id.etVolume)).setText("");
        ((EditText) rootView.findViewById(R.id.etTIN)).setText("");
        ((EditText) rootView.findViewById(R.id.etFullName)).setText("");
        ((EditText) rootView.findViewById(R.id.etPlateNumber)).setText("");
    }
    // Helper method to show alerts with color-coded messages
    private void showAlert(View rootView, String title, String message, int color) {
        SpannableString spannableMessage = new SpannableString(message);
        spannableMessage.setSpan(new ForegroundColorSpan(color), 0, spannableMessage.length(), 0);

        new AlertDialog.Builder(rootView.getContext())
                .setTitle(title)
                .setMessage(spannableMessage)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
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


