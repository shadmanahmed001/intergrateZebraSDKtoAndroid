package info.shadmanahmed.zebraprinter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
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


public class MainActivity extends Activity { // WHY

    private UIHelper helper = new UIHelper(this);
    private EditText macAddress, ipDNSAddress, portNumber;
    private Context mContext;

    Connection printerConnection = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mContext = this;

        ipDNSAddress = (EditText) findViewById(R.id.ipAddressInput);
        String ip = SettingsHelper.getIp(this);
        ipDNSAddress.setText(ip);

        portNumber = (EditText) findViewById(R.id.portInput);
        String port = SettingsHelper.getPort(this);
        portNumber.setText(port);


//        printToZebraPrinter("James Huerta", "350", "6160", "3e4567-e89b-12d3-a456-426655440000", "US Postal Service", "12345678901234");
    }



    public void printToZebraPrinter(String name, String unitNumber, String building, String uuid, String courier, String trackingnumber){
        // first check if bluetooth or wifi
        // then make new printer connection
        // then open connection
        // write
        // close the connection

        if (SettingsHelper.getBluetoothAddress(MainActivity.this) == "" || SettingsHelper.getIp(MainActivity.this) == "" || SettingsHelper.getPort(MainActivity.this) == ""){
            Intent intent = new Intent(MainActivity.this, PrinterSettings.class);
            startActivity(intent);
        }
        else { // printer config found send the print job
            portNumber.setVisibility(View.INVISIBLE);
            ipDNSAddress.setVisibility(View.INVISIBLE);
            if (!SettingsHelper.isBluetoothUsed(MainActivity.this)) {
                try {
                    printerConnection = new TcpConnection(SettingsHelper.getIp(MainActivity.this), Integer.valueOf(SettingsHelper.getPort(MainActivity.this)));
                } catch (NumberFormatException e) {
                    helper.showErrorDialogOnGuiThread("Port number is invalid");
                    return;
                }
            } else {
                printerConnection = new BluetoothConnection(SettingsHelper.getBluetoothAddress(MainActivity.this));
            }
            try {

                printerConnection.open();

                ZebraPrinter printer = null;
                if (printerConnection.isConnected()) {
                    printer = ZebraPrinterFactory.getInstance(printerConnection);


                    if (printer != null) {
                        PrinterLanguage pl = printer.getPrinterControlLanguage();
                        if (pl == PrinterLanguage.CPCL) {
                            helper.showErrorDialogOnGuiThread("TCPCL printers not supported!");
                        } else {
//                            sendTestLabel();
                            // THIS IS WHERE TO SEND THE INFO TO PRINT!!!
                            try {
                                byte[] configLabel = createZebraLabel(name, unitNumber, building, uuid, courier, trackingnumber).getBytes();
                                printerConnection.write(configLabel);
                                sleep(1500);
                                if (printerConnection instanceof BluetoothConnection) {
                                    sleep(500);
                                }
                            } catch (ConnectionException e) {
                            }
                        }
                        printerConnection.close();
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

    }
    // needed functions
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static String createZebraLabel(String name, String unitNumber, String building, String uuid, String courier, String trackingnumber) {
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

    // this is just for test print
    public void printToZebra(View view){
        // this is to save the port and ip to shared pref
        // TODO: 9/14/17 should do check if its in shared pref, if not then alert to take user input
        SettingsHelper.savePort(MainActivity.this, portNumber.getText().toString());
        SettingsHelper.saveIp(MainActivity.this,ipDNSAddress.getText().toString());
        try { // ONLY CONNECTING TO THE WIFI NOT THE BLUETOOTH
            printerConnection = new TcpConnection(SettingsHelper.getIp(MainActivity.this), Integer.valueOf(SettingsHelper.getPort(MainActivity.this)));
        } catch (NumberFormatException e){
            helper.showErrorDialogOnGuiThread("Port number is invalid");
            return;
        }
        try {
            printerConnection.open();
            ZebraPrinter printer = null;
            if (printerConnection.isConnected()) {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
                if (printer != null) {
                    PrinterLanguage pl = printer.getPrinterControlLanguage();
                    if (pl == PrinterLanguage.CPCL) {
                        helper.showErrorDialogOnGuiThread("TCPCL printers not supported!");
                    } else {
                        // [self.connectivityViewController setStatus:@"Building receipt in ZPL..." withColor:[UIColor
                        // cyanColor]];
//                        if (withManyJobs) {
////                            sendTestLabelWithManyJobs(printerConnection);
//                            createZebraLabel("James Huerta", "350", "6160", "3e4567-e89b-12d3-a456-426655440000", "US Postal Service", "12345678901234").getBytes();
//                        } else {
//                            sendTestLabel();
//                        }
                        sendTestLabel();
                    }
                    printerConnection.close();
//                    saveSettings();
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


//    private void connectAndSendLabel(final boolean withManyJobs) {
//        if (isBluetoothSelected() == false) {
//            try {
//                printerConnection = new TcpConnection(getTcpAddress(), Integer.valueOf(getTcpPortNumber()));
//            } catch (NumberFormatException e) {
//                helper.showErrorDialogOnGuiThread("Port number is invalid");
//                return;
//            }
//        } else {
//            printerConnection = new BluetoothConnection(getMacAddressFieldText());
//        }
//        try {
//            helper.showLoadingDialog("Connecting...");
//            printerConnection.open();
//
//            ZebraPrinter printer = null;
//
//            if (printerConnection.isConnected()) {
//                printer = ZebraPrinterFactory.getInstance(printerConnection);
//
//                if (printer != null) {
//                    PrinterLanguage pl = printer.getPrinterControlLanguage();
//                    if (pl == PrinterLanguage.CPCL) {
//                        helper.showErrorDialogOnGuiThread("This demo will not work for CPCL printers!");
//                    } else {
//                        // [self.connectivityViewController setStatus:@"Building receipt in ZPL..." withColor:[UIColor
//                        // cyanColor]];
//                        if (withManyJobs) {
////                            sendTestLabelWithManyJobs(printerConnection);
//                            createZebraLabel("James Huerta", "350", "6160", "3e4567-e89b-12d3-a456-426655440000", "US Postal Service", "12345678901234").getBytes();
//                        } else {
//                            sendTestLabel();
//                        }
//                    }
//                    printerConnection.close();
//                    saveSettings();
//                }
//            }
//        } catch (ConnectionException e) {
//            helper.showErrorDialogOnGuiThread(e.getMessage());
//        } catch (ZebraPrinterLanguageUnknownException e) {
//            helper.showErrorDialogOnGuiThread("Could not detect printer language");
//        } finally {
//            helper.dismissLoadingDialog();
//        }
//    }

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
    //    private void saveSettings() {
//        SettingsHelper.saveBluetoothAddress(MainActivity.this, getMacAddressFieldText());
//        SettingsHelper.saveIp(MainActivity.this, getTcpAddress());
//        SettingsHelper.savePort(MainActivity.this, getTcpPortNumber());
//    }

    }
