package info.shadmanahmed.zebraprinter;

/**
 * Created by shadmanahmed on 9/13/17.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {

    public static final String bluetoothAddressKey = "SMIOTA_ZEBRA_PRINTER_BLUETOOTH_ADDRESS";
    public static final String tcpAddressKey = "SMIOTA_ZEBRA_PRINTER_TCP_ADDRESS";
    public static final String tcpPortKey = "SMIOTA_ZEBRA_PRINTER_TCP_PORT";
    public static final String bluetoothSelected = "SMIOTA_ZEBRA_PRINTER_BLUETOOTH_SELECTED";

    public static final String PREFS_NAME = "OurSavedAddress";

    public static String getIp(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(tcpAddressKey, "");
    }

    public static String getPort(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(tcpPortKey, "");
    }

    public static String getBluetoothAddress(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(bluetoothAddressKey, "");
    }

    public static void saveIp(Context context, String ip) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(tcpAddressKey, ip);
        editor.commit();
    }

    public static void savePort(Context context, String port) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(tcpPortKey, port);
        editor.commit();
    }
    public static void setBluetoothUsed(Context context, boolean set) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(bluetoothSelected, set);
        editor.commit();
    }
    public static boolean isBluetoothUsed(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(bluetoothSelected, false);
    }

    public static void saveBluetoothAddress(Context context, String address) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(bluetoothAddressKey, address);
        editor.commit();
    }
}
