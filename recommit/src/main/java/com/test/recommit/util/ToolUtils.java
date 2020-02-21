package com.test.recommit.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ToolUtils {

    /**
     * 获取电脑的mac地址
     * @return
     * @author zqk
     * @since 2020/2/20
     */
    public static String getMACAddressOld() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            byte[] bytes = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            for (int i = 0; i < bytes.length; i++) {
                if (i != 0) {
                    stringBuilder.append("-");
                }
                String s = Integer.toHexString(bytes[i] & 0xFF);
                stringBuilder.append(s.length() == 1 ? 0 + s : s);
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().toUpperCase().replace("-", "");
    }

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && isNotValidAddress(ip)) {
            ip = null;
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip != null && isNotValidAddress(ip)) {
                ip = null;
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip != null && isNotValidAddress(ip)) {
                ip = null;
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip != null && isNotValidAddress(ip)) {
                ip = null;
            }
        }
        return ip;
    }

    private static boolean isNotValidAddress(String ip) {
        if (ip == null) {
            return true;
        } else {
            for (int i = 0; i < ip.length(); ++i) {
                char ch = ip.charAt(i);
                if ((ch < '0' || ch > '9') && (ch < 'A' || ch > 'F') && (ch < 'a' || ch > 'f') && ch != '.' && ch != ':') {
                    return true;
                }
            }
            return false;
        }
    }

    public static String getMACAddressOld1() {
        String address = "";
        String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("ipconfig", "/all");
                Process p = pb.start();
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("Physical Address") != -1) {
                        int index = line.indexOf(":");
                        address = line.substring(index + 1);
                        break;
                    }
                }
                br.close();
                return address.trim();
            } catch (IOException e) {
            }
        }
        return address;
    }

    /**
     * 此方法描述的是：获得服务器的MAC地址
     * @author:  zhangyang33@sinopharm.com
     * @version: 2014年9月5日 下午1:27:25
     */
    public static String getMACAddress() {
        String macId = "";
        InetAddress ip = null;
        NetworkInterface ni = null;
        try {
            boolean bFindIP = false;
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                    .getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                if (bFindIP) {
                    break;
                }
                ni = (NetworkInterface) netInterfaces
                        .nextElement();
                // ----------特定情况，可以考虑用ni.getName判断
                // 遍历所有ip
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = (InetAddress) ips.nextElement();
                    if (!ip.isLoopbackAddress() // 非127.0.0.1
                            && ip.getHostAddress().matches(
                            "(\\d{1,3}\\.){3}\\d{1,3}")) {
                        bFindIP = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != ip) {
            try {
                macId = getMacFromBytes(ni.getHardwareAddress());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return macId;
    }

    private static String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

}
