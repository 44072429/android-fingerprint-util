package util.android.ys.com.fingerprnt;

import android.content.Context;

/**
 * Created by 18758 on 2018/5/9.
 */

public class FingerUtilV2 {

    public static Fingerprint fingerprint;

    // 指纹录入
    public static boolean startCaptrue( final Fingerprint.FingerprinEventlistener fingerprinEventlistener ,  Context context) {
        String devType = "default";

        if(devType=="default")
        {
            fingerprint = new FingerprintZhongZhengV2();
        } else {

        }

        if(fingerprint != null) {
            return fingerprint.startCaptrue( fingerprinEventlistener , context );
        }

        return false;
    }

    public static void stopCaptrue() {

        if( fingerprint != null){
            fingerprint.stopCaptrue();
        }
    }


    // 指纹验证
    public static boolean fingerStart( final Fingerprint.FingerprinEventlistener fingerprinEventlistener ,  Context context) {
        String devType = "default";

        if(devType=="default")
        {
            fingerprint = new FingerprintZhongZhengV2();
        } else {

        }

        if(fingerprint != null) {
            return fingerprint.fingerStart( fingerprinEventlistener , context );
        }

        return false;
    }

    public static void fingerStop() {

        if( fingerprint != null){
            fingerprint.fingerStop();
        }

    }

    // 指纹销毁
    public  static void destroy (){
        if( fingerprint != null){
            fingerprint.destroy();
        }
    }

}

