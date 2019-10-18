package com.kms.demo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.kms.demo.entity.ShareAppInfo;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author matrixelement
 */
public class SystemUtil {

    private SystemUtil() {

    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {

        }
        return "02:00:00:00:00:00";
    }


    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @return
     */
    public static List<ShareAppInfo> getShareAppInfoList(Context context) {
        List<ShareAppInfo> shareAppInfoList = new ArrayList<>();
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //从pinfo中将包名字逐一取出，压入packageInfost中
        if (packageInfos != null) {
            for (ShareAppInfo appInfo : ShareAppInfo.values()) {
                for (int i = 0; i < packageInfos.size(); i++) {
                    String packName = packageInfos.get(i).packageName;
                    if (appInfo.packageName.equals(packName)) {
                        shareAppInfoList.add(appInfo);
                    }
                }
            }

        }
        return shareAppInfoList;
    }

    /**
     * ping
     *
     * @param url ping的目标URL
     * @return
     **/
    public static boolean ping(String url) {

        try {
            //代表ping 3 次 超时时间为10秒
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + url);//ping3次

            int status = p.waitFor();

            return status == 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
