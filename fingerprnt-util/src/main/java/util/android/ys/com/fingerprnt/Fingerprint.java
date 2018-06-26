package util.android.ys.com.fingerprnt;

import android.content.Context;

/**
 * Created by 18758 on 2018/5/9.
 */

public interface Fingerprint {

    interface FingerprinEventlistener {
        //        void onSuccess(byte[] fingerprintData);
        void onSuccess(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer);
        void onFailure(String err);
    }

    /**
     *  用于指纹录入
     */
    boolean startCaptrue(FingerprinEventlistener fingerprinEventlistener, Context context);

    /**
     *  用于指纹录入
     */
    void stopCaptrue();

    /**
     *  用于指纹验证
     */
    boolean fingerStart(FingerprinEventlistener fingerprinEventlistener, Context context);

    /**
     *  用于指纹验证
     */
     void fingerStop();


     void destroy();


}
