/*
 * Copyright (c) 2014 HygieiaSoft
 * Distributed under the MIT License.
 * (See accompanying file LICENSE or copy at http://opensource.org/licenses/MIT)
 */
package org.hygieiasoft.cordova.uid;

import static android.Manifest.permission.READ_PHONE_STATE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.provider.Settings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UID extends CordovaPlugin {
	public static String uuid; // Device UUID
	public static String imei; // Device IMEI
	public static String imsi; // Device IMSI
	public static String iccid; // Sim IMSI
	public static String mac; // MAC address
	public static String serial; // Serial
	public static Context context;

	/**
	 * Constructor.
	 */
	public UID() {
	}

	/**
	 * Sets the context of the Command. This can then be used to do things like
	 * get file paths associated with the Activity.
	 *
	 * @param cordova The context of the main Activity.
	 * @param webView The CordovaWebView Cordova is running in.
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		this.context = cordova.getActivity().getApplicationContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.context.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				getData();
			} else {
				ActivityCompat.requestPermissions(this.cordova.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 123456);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults) {

		if (requestCode == 123456) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getData();
			} else {

			}
		}
	}

	public void getData() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			UID.uuid = Build.getSerial();
		}
		UID.imei = getImei(this.context);
		UID.imsi = getImsi(this.context);
		UID.iccid = getIccid(this.context);
		UID.mac = getMac(this.context);
	}


	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action            The action to execute.
	 * @param args              JSONArry of arguments for the plugin.
	 * @param callbackContext   The callback id used when calling back into JavaScript.
	 * @return                  True if the action was valid, false if not.
	 */
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("getUID")) {
			JSONObject r = new JSONObject();
			r.put("UUID", UID.uuid);
			r.put("IMEI", UID.imei);
			r.put("IMSI", UID.imsi);
			r.put("ICCID", UID.iccid);
			r.put("MAC", UID.mac);
			callbackContext.success(r);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Get the device's Universally Unique Identifier (UUID).
	 *
	 * @param context The context of the main Activity.
	 * @return
	 */
	public String getUuid(Context context) {
		String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return uuid;
	}

	/**
	 * Get the device's International Mobile Station Equipment Identity (IMEI).
	 *
	 * @param context The context of the main Activity.
	 * @return
	 */
	public String getImei(Context context) {
		final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephony.getDeviceId();
		return imei;
	}

	/**
	 * Get the device's International mobile Subscriber Identity (IMSI).
	 *
	 * @param context The context of the main Activity.
	 * @return
	 */
	public String getImsi(Context context) {
		final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephony.getSubscriberId();
		return imsi;

	}

	/**
	 * Get the sim's Integrated Circuit Card Identifier (ICCID).
	 *
	 * @param context The context of the main Activity.
	 * @return
	 */
	public String getIccid(Context context) {
		final TelephonyManager mTelephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String iccid = mTelephony.getSimSerialNumber();
		return iccid;
	}

	/**
	 * Get the Media Access Control address (MAC).
	 *
	 * @param context The context of the main Activity.
	 * @return
	 */
	public String getMac(Context context) {
		final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		final WifiInfo wInfo = wifiManager.getConnectionInfo();
		String mac = wInfo.getMacAddress();
		return mac;
	}

}
