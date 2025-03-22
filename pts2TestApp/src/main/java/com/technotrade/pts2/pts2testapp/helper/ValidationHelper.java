package com.technotrade.pts2.pts2testapp.helper;

public class ValidationHelper {
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isValidPortNumber(String portNumberStr) {
        if (portNumberStr == null || portNumberStr.isEmpty()) {
            return false;
        }

        try {
            int portNumber = Integer.parseInt(portNumberStr);
            return portNumber >= 0 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}