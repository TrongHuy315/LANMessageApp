package src.security;

import java.net.NetworkInterface;
import java.net.InetAddress;

public class DeviceUtils {
    public static String getMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
            byte[] macBytes = ni.getHardwareAddress();

            if (macBytes == null) return "UNKNOWN";

            StringBuilder sb = new StringBuilder();
            for (byte b : macBytes) {
                sb.append(String.format("%02X", b));
                sb.append("-");
            }

            return sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
