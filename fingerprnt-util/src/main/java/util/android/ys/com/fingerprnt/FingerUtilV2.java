package util.android.ys.com.fingerprnt;

import android.content.Context;

/**
 * Created by 18758 on 2018/5/9.
 */

public class FingerUtilV2 {

    public static Fingerprint fingerprint;

    /**
     * 这个函数必须在ui线程调用，必须在app开始时调用
     */
    public static void globalInit(Context context) {
        fingerprint = new FingerprintZhongZhengV2();
        fingerprint.globalInit(context);
    }

    /**
     * 这个函数必须在ui线程调用，必须在app结束时调用
     */
    public static void globalRelease() {
        if (fingerprint != null) {
            fingerprint.globalRelease();
            fingerprint = null;
        }
    }

    // 指纹录入
    public static boolean fingerEnrollStart(final Fingerprint.FingerprinEnrollEventlistener fingerprinEnrollEventlistener , int time) {

//        String devType = "default";
//
//        if (devType == "default") {
//            fingerprint = new FingerprintZhongZhengV2();
//        } else {
//
//        }

        if (fingerprint != null) {
            return fingerprint.fingerEnrollStart( fingerprinEnrollEventlistener  , time);
        }

        return false;
    }

    public static void fingerEnrollStop() {

        if (fingerprint != null) {
            fingerprint.fingerEnrollStop();
        }
    }

    /**
     * 用于指纹验证
     */
    public static boolean fingerVerifyStart(Fingerprint.FingerprinEventlistener fingerprinEventlistener){
        if (fingerprint != null) {
            return fingerprint.fingerVerifyStart(fingerprinEventlistener);
        }

        return false;
    }

    /**
     * 用于指纹验证
     */
    public static void fingerVerifyStop(){
        if (fingerprint != null) {
            fingerprint.fingerVerifyStop();
        }
    }
}

