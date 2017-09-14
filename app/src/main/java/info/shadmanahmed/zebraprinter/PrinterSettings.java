package info.shadmanahmed.zebraprinter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by shadmanahmed on 9/14/17.
 */

public class PrinterSettings extends Activity {
    private RadioButton btRadioButton;
    private EditText macAddress;
    private EditText ipAddress;
    private EditText portNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_screen);

        ipAddress = (EditText) findViewById(R.id.ipAddressInput);
        String ip = SettingsHelper.getIp(PrinterSettings.this);
        ipAddress.setText(ip);

        portNumber = (EditText) findViewById(R.id.portInput);
        String port = SettingsHelper.getPort(PrinterSettings.this);
        portNumber.setText(port);

//        toggleEditField(portNumber, shouldAllowPortNumberEditing());

        macAddress = (EditText) findViewById(R.id.macInput);
        String mac = SettingsHelper.getBluetoothAddress(PrinterSettings.this);
        macAddress.setText(mac);

        btRadioButton = (RadioButton) findViewById(R.id.bluetoothRadio);

//        if(btRadioButton.isChecked()

        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bluetoothRadio) {
                    toggleEditField(macAddress, true);
                    toggleEditField(portNumber, false);
                    toggleEditField(ipAddress, false);
//                    secondTestButton.setVisibility(desiredVisibilityForSecondTestButton());
                } else {
                    toggleEditField(portNumber, shouldAllowPortNumberEditing());
                    toggleEditField(ipAddress, true);
                    toggleEditField(macAddress, false);
//                    secondTestButton.setVisibility(View.INVISIBLE);
                }
            }
        });



    }

    private void toggleEditField(EditText editText, boolean set) {
        /*
         * Note: Disabled EditText fields may still get focus by some other means, and allow text input.
         *       See http://code.google.com/p/android/issues/detail?id=2771
         */
        editText.setEnabled(set);
        editText.setFocusable(set);
        editText.setFocusableInTouchMode(set);
    }

    protected boolean shouldAllowPortNumberEditing() {
        return true;
    }

    public void saveButtonPressed(View view){
        if(btRadioButton.isChecked()){
            // bluetooth
            SettingsHelper.saveBluetoothAddress(PrinterSettings.this, macAddress.getText().toString());
            SettingsHelper.setBluetoothUsed(PrinterSettings.this, true);
            finish();
        }
        else {
            // wifi
            SettingsHelper.saveIp(PrinterSettings.this, ipAddress.getText().toString());
            SettingsHelper.savePort(PrinterSettings.this, portNumber.getText().toString());
            SettingsHelper.setBluetoothUsed(PrinterSettings.this, false);
            finish();
        }
    }
}
