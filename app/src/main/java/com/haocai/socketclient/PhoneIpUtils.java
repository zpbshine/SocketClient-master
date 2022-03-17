package com.haocai.socketclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PhoneIpUtils {

    /**
     * 获取ip地址：先通过条件判断满足条件的ip地址数量，如果存在多个则通过反射获取（思路来源Settings）
     *
     * @param context
     * @return
     */
    public static String getIp(Context context) {
        try {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // 有些手机可以通过USB上网，这里过滤虚拟IP地址
                        if (!TextUtils.isEmpty(intf.getDisplayName()) && !intf.getDisplayName().startsWith("usb"))
                            hashMap.put(intf.getDisplayName(), inetAddress.getHostAddress().toString());
                    }
                }
            }
            // 根据IP地址数做处理
            if (hashMap.size() == 0) {
                return "";
            } else if (hashMap.size() == 1) {
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    return entry.getValue();
                }
            } else {
                // 存在多个IP地址，通过反射模拟Settings获取IP地址方式
                String settingIp = getSettingIP(context);
                if (!TextUtils.isEmpty(settingIp)) {
                    return settingIp;
                } else {
                    // 最糟糕的情况，一般不会进入这里
                    // 排序，一般情况是返回rmnet0
                    Object[] keyArray = hashMap.keySet().toArray();
                    Arrays.sort(keyArray);
                    return hashMap.get(keyArray[0]);
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 反射获取ip地址，方式和Setting的一样。不建议做为获取IP地址的首选方法
     *
     * @param context
     * @return
     */
    private static String getSettingIP(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class class1 = Class.forName("android.net.ConnectivityManager");
            Class class2 = Class.forName("android.net.LinkProperties");
            Method method1 = class1.getMethod("getActiveLinkProperties");
            Object result = method1.invoke(cm);
            Method method2 = class2.getMethod("getAddresses");
            Collection<InetAddress> list = (Collection<InetAddress>) method2.invoke(result);
            Iterator<InetAddress> iterator = list.iterator();
            while (iterator.hasNext()) {
                InetAddress inetAddress = iterator.next();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
    public static String getMacIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return ((ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "."  + (ip >> 24 & 0xff));
    }

}
