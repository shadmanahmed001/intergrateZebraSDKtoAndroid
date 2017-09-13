package info.shadmanahmed.zebraprinter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ConnectionScreen {

    private UIHelper helper = new UIHelper(this);
    private EditText macAddress, ipDNSAddress, portNumber;
    private Context mContext;

    Connection printerConnection = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        // set all teh fields
        portNumber = (EditText) findViewById(R.id.portInput);
        ipDNSAddress = (EditText) this.findViewById(R.id.ipAddressInput);

        if (SettingsHelper.getPort(MainActivity.this) != null && SettingsHelper.getIp(MainActivity.this) != null) {
            portNumber.setText(SettingsHelper.getPort(MainActivity.this));
            ipDNSAddress.setText(SettingsHelper.getIp(MainActivity.this));
        }
        else {
            Button printButton = (Button) findViewById(R.id.printButtonId);
            printButton.setText("Add the IP and Port please");
        }



    }
    public void printToZebra(View view){
        if (SettingsHelper.getPort(MainActivity.this) != null && SettingsHelper.getIp(MainActivity.this) != null) {
            System.out.println(SettingsHelper.getPort(MainActivity.this));
            System.out.println(SettingsHelper.getIp(MainActivity.this));
            /// do the print commands since its connected
        }
        else {
            // get the fields from teh textview
            System.out.println("saving the port and ip");
            SettingsHelper.savePort(MainActivity.this,portNumber.getText().toString());
            SettingsHelper.saveIp(MainActivity.this,ipDNSAddress.getText().toString());
        }
    }


    @Override
    public void performTest() {

    }

    private void connectAndSendLabel(final boolean withManyJobs) {
        if (isBluetoothSelected() == false) {
            try {
                printerConnection = new TcpConnection(getTcpAddress(), Integer.valueOf(getTcpPortNumber()));
            } catch (NumberFormatException e) {
                helper.showErrorDialogOnGuiThread("Port number is invalid");
                return;
            }
        } else {
            printerConnection = new BluetoothConnection(getMacAddressFieldText());
        }
        try {
            helper.showLoadingDialog("Connecting...");
            printerConnection.open();

            ZebraPrinter printer = null;

            if (printerConnection.isConnected()) {
                printer = ZebraPrinterFactory.getInstance(printerConnection);

                if (printer != null) {
                    PrinterLanguage pl = printer.getPrinterControlLanguage();
                    if (pl == PrinterLanguage.CPCL) {
                        helper.showErrorDialogOnGuiThread("This demo will not work for CPCL printers!");
                    } else {
                        // [self.connectivityViewController setStatus:@"Building receipt in ZPL..." withColor:[UIColor
                        // cyanColor]];
                        if (withManyJobs) {
//                            sendTestLabelWithManyJobs(printerConnection);
                            createZebraLabel("James Huerta", "350", "6160", "3e4567-e89b-12d3-a456-426655440000", "US Postal Service", "12345678901234").getBytes();
                        } else {
                            sendTestLabel();
                        }
                    }
                    printerConnection.close();
                    saveSettings();
                }
            }
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        } catch (ZebraPrinterLanguageUnknownException e) {
            helper.showErrorDialogOnGuiThread("Could not detect printer language");
        } finally {
            helper.dismissLoadingDialog();
        }
    }

    private void sendTestLabel() {
        try {
            byte[] configLabel = createZebraLabel("James Huerta", "350", "6160", "3e4567-e89b-12d3-a456-426655440000", "US Postal Service", "12345678901234").getBytes();
            printerConnection.write(configLabel);
            sleep(1500);
            if (printerConnection instanceof BluetoothConnection) {
                sleep(500);
            }
        } catch (ConnectionException e) {
        }
    }
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void saveSettings() {
        SettingsHelper.saveBluetoothAddress(MainActivity.this, getMacAddressFieldText());
        SettingsHelper.saveIp(MainActivity.this, getTcpAddress());
        SettingsHelper.savePort(MainActivity.this, getTcpPortNumber());
    }

    private String createZebraLabel(String name, String unitNumber, String building, String uuid, String courier, String trackingnumber) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = sdf.format(date);
        String labelString = "^XA\n" +
                "^CFB,25\n" +
                "^FWR\n" +
                "^FO775,230^FD"+dateString+"^FS\n" +
                "^FO720,50^FD"+courier+"^FS\n" +
                "^FO680,50^FD"+trackingnumber+"^FS\n" +
                "^FO600,50^FD"+name+"^FS\n" +
                "^FO590,50^GB5,500,3^FS\n" +
                "^CF40,50\n" + //
                "^FO490,50^FD"+unitNumber+"^FS\n" +
                "^FO470,50^GB1,500,3^FS\n" +
                "^FO400,50^FD"+building+"^FS\n" +
                "^BQN,2,7,H,7\n" +
                "^FO50,400^FDMA,"+uuid+"^FS\n" +
                "^XZ";
        return labelString;
    }

    }
