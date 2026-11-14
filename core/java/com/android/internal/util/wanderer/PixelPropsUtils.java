package com.android.internal.util.wanderer;

import android.app.Application;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public class PixelPropsUtils {
    private static final String TAG = "WandererStealth";
    private static final String PIF_JSON_PATH = "/data/system/pif.json";

    // Certified Stock Fingerprint for Xiaomi Redmi 13 5G / POCO M6 Plus 5G (breeze)
    private static final Map<String, Object> sStockProps = new HashMap<>();
    static {
        sStockProps.put("BRAND", "Redmi");
        sStockProps.put("MANUFACTURER", "Xiaomi");
        sStockProps.put("DEVICE", "breeze");
        sStockProps.put("PRODUCT", "breeze_in");
        sStockProps.put("MODEL", "2406ERN9CI");
        sStockProps.put("FINGERPRINT", "Redmi/breeze_in/breeze:14/UKQ1.231003.002/V816.0.4.0.UNUINXM:user/release-keys");
        sStockProps.put("TYPE", "user");
        sStockProps.put("TAGS", "release-keys");
    }

    private static final Set<String> sBankingPackages = new HashSet<>(Arrays.asList(
        "com.google.android.apps.nbu.paisa.user", // Google Pay (Tez)
        "com.phonepe.app",                       // PhonePe
        "net.one97.paytm",                        // Paytm
        "com.sbi.lotusintouch",                  // YONO SBI
        "com.hdfcbank.android",                  // HDFC Bank MobileBanking
        "com.icicibank.mobile",                  // iMobile Pay by ICICI
        "com.axis.mobile",                       // Axis Mobile
        "com.chase.sig.android",                 // Chase Mobile
        "com.revolut.revolut"                    // Revolut
    ));

    public static void initProps(String packageName) {
        if (packageName == null) return;

        // 1. Google Play Services / Play Integrity DroidGuard Process Spoofing
        if (packageName.equals("com.google.android.gms")) {
            final String process = Application.getProcessName();
            if (process != null && (process.toLowerCase().contains("unstable") 
                    || process.toLowerCase().contains("instrumentation"))) {
                loadDynamicPifIfExists();
                for (Map.Entry<String, Object> entry : sStockProps.entrySet()) {
                    setProp(entry.getKey(), entry.getValue());
                }
                return;
            }
        }

        // 2. Banking App Stealth Shield
        if (isBankingApp(packageName) || (!packageName.startsWith("com.android.") && !packageName.startsWith("org.lineageos."))) {
            setProp("TYPE", "user");
            setProp("TAGS", "release-keys");
            setProp("DISPLAY", Build.ID);
        }
    }

    public static boolean isBankingApp(String packageName) {
        return packageName != null && sBankingPackages.contains(packageName);
    }

    private static void loadDynamicPifIfExists() {
        try {
            File pif = new File(PIF_JSON_PATH);
            if (pif.exists()) {
                String content = new String(Files.readAllBytes(pif.toPath()));
                JSONObject json = new JSONObject(content);
                for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                    String key = it.next();
                    sStockProps.put(key, json.get(key));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading dynamic pif.json", e);
        }
    }

    private static void setProp(String key, Object value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e(TAG, "Failed to spoof property " + key, e);
        }
    }
}
