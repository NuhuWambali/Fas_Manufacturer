package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;
import com.technotrade.pts2.pts2testapp.helper.ValidationHelper;
import com.technotrade.pts2.util.Logger;

/// <summary>
/// The fragment that allowing to enter and store settings.
/// </summary>
public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentManager mSupportFragmentManager = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSupportFragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        androidx.preference.EditTextPreference httpPortNumberText = getPreferenceManager()
            .findPreference("http_port_number");
        assert httpPortNumberText != null;
        httpPortNumberText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("123456789."));
        });

        androidx.preference.EditTextPreference httpsPortNumberText = getPreferenceManager()
            .findPreference("https_port_number");
        assert httpsPortNumberText != null;
        httpsPortNumberText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("123456789."));
        });
    }

    /// <summary>
    /// The fragment overridden method that will be called at start to load settings
    /// </summary>
    @Override
    public void onStart() {
        super.onStart();

        loadSettings();
    }

    /// <summary>
    /// The fragment overridden method that will be called at stop to save settings
    /// </summary>
    @Override
    public void onStop() {
        super.onStop();

        saveSettings();
    }

    public void loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        String login = preferences.getString("login", "admin");
        EditTextPreference editLoginPreference = findPreference("login");
        assert editLoginPreference != null;
        editLoginPreference.setText(login);
        editLoginPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onLoginChanged((String) newValue);
            return true;
        });

        String password = preferences.getString("password", "admin");
        EditTextPreference editPasswordPreference = findPreference("password");
        assert editPasswordPreference != null;
        editPasswordPreference.setText(password);
        editPasswordPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onPasswordChanged((String) newValue);
            return true;
        });

        String hostDomain = preferences.getString("host_domain", "192.168.1.117");
        EditTextPreference editHostDomainPreference = findPreference("host_domain");
        assert editHostDomainPreference != null;
        editHostDomainPreference.setText(hostDomain);
        editHostDomainPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onHostDomainChanged((String) newValue);
            return true;
        });

        String defaultHttpPortNumber = "80";
        String httpPortNumber = preferences.getString("http_port_number", defaultHttpPortNumber);

        if (!ValidationHelper.isNumeric(httpPortNumber)) {
            httpPortNumber = defaultHttpPortNumber;
        }

        EditTextPreference editHttpPortNumberPreference = findPreference("http_port_number");
        assert editHttpPortNumberPreference != null;
        editHttpPortNumberPreference.setText(httpPortNumber);
        editHttpPortNumberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isNumeric((String) newValue)) {
                onHttpPortNumberChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, mSupportFragmentManager, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTP_port_must_be_a_valid_number_between_0_and_65535));
                return false;
            }
        });

        String defaultHttpsPortNumber = "443";
        String httpsPortNumber = preferences.getString("https_port_number", defaultHttpsPortNumber);

        if (!ValidationHelper.isNumeric(httpsPortNumber)) {
            httpsPortNumber = defaultHttpsPortNumber;
        }

        EditTextPreference editHttpsPortNumberPreference = findPreference("https_port_number");
        assert editHttpsPortNumberPreference != null;
        editHttpsPortNumberPreference.setText(httpsPortNumber);
        editHttpsPortNumberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isNumeric((String) newValue)) {
                onHttpsPortNumberChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, mSupportFragmentManager, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTPS_port_must_be_a_valid_number_between_0_and_65535));
                return false;
            }
        });

        ListPreference protocolPreference = findPreference("protocol_preference");
        assert protocolPreference != null;
        String protocol = preferences.getString("protocol_preference", "https");
        protocolPreference.setSummary(protocol.equals("https") ? "HTTPS" : "HTTP");

        protocolPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedProtocol = (String) newValue;
            preference.setSummary(selectedProtocol.equals("https") ? "HTTPS" : "HTTP");
            onProtocolChanged(selectedProtocol);
            return true;
        });

        boolean digest = preferences.getBoolean("digest_auth_switch", true);
        SwitchPreference switchDigestPreference = findPreference("digest_auth_switch");
        assert switchDigestPreference != null;
        switchDigestPreference.setChecked(digest);
        switchDigestPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onDigestAuthChanged((Boolean) newValue);
            return true;
        });

        String currency = preferences.getString("currency", "$");
        EditTextPreference editCurrencyPreference = findPreference("currency");
        assert editCurrencyPreference != null;
        editCurrencyPreference.setText(currency);
        editCurrencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onCurrencyChanged((String) newValue);
            return true;
        });
    }

    public void saveSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = preferences.edit();

        EditTextPreference editLoginPreference = findPreference("login");
        assert editLoginPreference != null;
        String login = editLoginPreference.getText();
        editor.putString("login", login);

        EditTextPreference editPasswordPreference = findPreference("password");
        assert editPasswordPreference != null;
        String password = editPasswordPreference.getText();
        editor.putString("password", password);

        EditTextPreference editHostDomainPreference = findPreference("host_domain");
        assert editHostDomainPreference != null;
        String hostDomain = editHostDomainPreference.getText();
        editor.putString("host_domain", hostDomain);

        EditTextPreference editHttpPortNumberPreference = findPreference("http_port_number");
        assert editHttpPortNumberPreference != null;
        String httpPortNumber = editHttpPortNumberPreference.getText();

        if (ValidationHelper.isNumeric(httpPortNumber)) {
            editor.putString("http_port_number", httpPortNumber);
        } else {
            DialogHelper.showDialogWarning(this, mSupportFragmentManager, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTP_port_must_be_a_valid_number_between_0_and_65535));
        }

        EditTextPreference editHttpsPortNumberPreference = findPreference("https_port_number");
        assert editHttpsPortNumberPreference != null;
        String httpsPortNumber = editHttpsPortNumberPreference.getText();

        if (ValidationHelper.isNumeric(httpsPortNumber)) {
            editor.putString("https_port_number", httpsPortNumber);
        } else {
            DialogHelper.showDialogWarning(this, mSupportFragmentManager, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTPS_port_must_be_a_valid_number_between_0_and_65535));
        }

        ListPreference protocolPreference = findPreference("protocol_preference");
        assert protocolPreference != null;
        String protocol = protocolPreference.getValue();
        editor.putString("protocol_preference", protocol);

        SwitchPreference switchDigestPreference = findPreference("digest_auth_switch");
        assert switchDigestPreference != null;
        boolean digest = switchDigestPreference.isChecked();
        editor.putBoolean("digest_auth_switch", digest);

        EditTextPreference editCurrencyPreference = findPreference("currency");
        assert editCurrencyPreference != null;
        String currency = editCurrencyPreference.getText();
        editor.putString("currency", currency);

        editor.apply();

        ApplicationFacade.getInstance().getPTSManager().loadPTSSettings();
    }

    private void onLoginChanged(String newLogin) {
        Logger.debug("SettingsFragment", "Login changed to: " + newLogin);
        saveSettings();
    }

    private void onPasswordChanged(String newPassword) {
        Logger.debug("SettingsFragment", "Password changed to: " + newPassword);
        saveSettings();
    }

    private void onHostDomainChanged(String newHostDomain) {
        Logger.debug("SettingsFragment", "Host domain changed to: " + newHostDomain);
        saveSettings();
    }

    private void onHttpPortNumberChanged(String newHttpPortNumber) {
        Logger.debug("SettingsFragment", "HTTP port number changed to: " + newHttpPortNumber);
        saveSettings();
    }

    private void onHttpsPortNumberChanged(String newHttpsPortNumber) {
        Logger.debug("SettingsFragment", "HTTPS port number changed to: " + newHttpsPortNumber);
        saveSettings();
    }

    private void onProtocolChanged(String newProtocol) {
        Logger.debug("SettingsFragment", "Protocol changed to: " + newProtocol);
        saveSettings();
    }

    private void onDigestAuthChanged(Boolean newDigestAuth) {
        Logger.debug("SettingsFragment", "Digest auth changed to: " + newDigestAuth);
        saveSettings();
    }

    private void onCurrencyChanged(String newCurrency) {
        Logger.debug("SettingsFragment", "Currency changed to: " + newCurrency);
        saveSettings();
    }
}