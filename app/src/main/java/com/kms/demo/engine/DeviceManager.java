package com.kms.demo.engine;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.kms.appcore.utils.crypt.MD5Utils;
import com.kms.demo.config.AppSettings;
import com.kms.demo.utils.SystemUtil;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 设备管理类
 *
 * @author matrixelement
 */
public class DeviceManager {


    private String OS;
    private String deviceID;

    private static final class InnerClass {
        private static final DeviceManager DEVICE_MANAGER = new DeviceManager();
    }

    private DeviceManager() {

    }

    public static DeviceManager getInstance() {
        return InnerClass.DEVICE_MANAGER;
    }

    public void init(Context context) {
        // 部分写死的配置项
        OS = "ANDROID";

        if (TextUtils.isEmpty(deviceID)) {
            synchronized (DeviceManager.class) {
                if (deviceID == null) {
                    final String id = AppSettings.getInstance().getDeviceId();
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        deviceID = UUID.fromString(id).toString();
                    } else {
                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                deviceID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                deviceID = System.currentTimeMillis() + new String(MD5Utils.encode(SystemUtil.getMacAddr().getBytes()), "utf-8");
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        AppSettings.getInstance().setDeviceId(deviceID.toString());
                    }
                }
            }
        }
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
        AppSettings.getInstance().setDeviceId(deviceID);
    }
}
