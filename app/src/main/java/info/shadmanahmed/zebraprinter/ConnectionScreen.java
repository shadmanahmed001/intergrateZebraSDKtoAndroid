package info.shadmanahmed.zebraprinter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by shadmanahmed on 9/13/17.
 */

public abstract class ConnectionScreen extends Activity {

    protected Button testButton;
    protected Button secondTestButton;
    private RadioButton btRadioButton;
    private EditText macAddress;
    private EditText ipAddress;
    private EditText portNumber;

    public static final String bluetoothAddressKey = "SMIOTA_ZEBRA_PRINTER_BLUETOOTH_ADDRESS";
    public static final String tcpAddressKey = "SMIOTA_ZEBRA_PRINTER_TCP_ADDRESS";
    public static final String tcpPortKey = "SMIOTA_ZEBRA_PRINTER_TCP_PORT";
    public static final String PREFS_NAME = "OurSavedAddress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_screen);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        ipAddress = (EditText) this.findViewById(R.id.ipAddressInput);
        String ip = settings.getString(tcpAddressKey, "");
        ipAddress.setText(ip);

        portNumber = (EditText) this.findViewById(R.id.portInput);
        String port = settings.getString(tcpPortKey, "");
        portNumber.setText(port);
        toggleEditField(portNumber, shouldAllowPortNumberEditing());

        macAddress = (EditText) this.findViewById(R.id.macInput);
        String mac = settings.getString(bluetoothAddressKey, "");
        macAddress.setText(mac);

        btRadioButton = (RadioButton) this.findViewById(R.id.bluetoothRadio);

        testButton = (Button) this.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                performTest();
            }
        });

        secondTestButton = (Button) this.findViewById(R.id.secondTestButton);
        secondTestButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                performSecondTest();
            }
        });
        secondTestButton.setVisibility(View.INVISIBLE);

//        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.bluetoothRadio) {
//                    toggleEditField(macAddress, true);
//                    toggleEditField(portNumber, false);
//                    toggleEditField(ipAddress, false);
//                    secondTestButton.setVisibility(desiredVisibilityForSecondTestButton());
//                } else {
//                    toggleEditField(portNumber, shouldAllowPortNumberEditing());
//                    toggleEditField(ipAddress, true);
//                    toggleEditField(macAddress, false);
//                    secondTestButton.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
    }
    protected int desiredVisibilityForSecondTestButton() {
        return View.INVISIBLE;
    }

    protected boolean shouldAllowPortNumberEditing() {
        return true;
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

    protected boolean isBluetoothSelected() {
        return btRadioButton.isChecked();
    }

    protected String getMacAddressFieldText() {
        return macAddress.getText().toString();
    }

    protected String getTcpAddress() {
        return ipAddress.getText().toString();
    }

    protected String getTcpPortNumber() {
        return portNumber.getText().toString();
    }

    protected void disablePortEditBox() {
        toggleEditField(portNumber, false);
    }

    public abstract void performTest();

    public void performSecondTest() {

    }
}
