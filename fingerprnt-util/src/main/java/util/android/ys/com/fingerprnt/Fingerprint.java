package util.android.ys.com.fingerprnt;

import android.content.Context;

/**
 * Created by 18758 on 2018/5/9.
 */

public interface Fingerprint {

    interface FingerprinEventlistener {
        /**
         * 每次输入完指纹回调
         * @param captureMode
         * @param imageBuffer
         * @param imageAttributes
         * @param templateBuffer
         */
        void onSuccess(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer);
        void onFailure(String err);
    }

    interface FingerprinEnrollEventlistener {

        /**
         * 每次输入完指纹回调
         * @param captureMode
         * @param imageBuffer
         * @param imageAttributes
         * @param templateBuffer
         * @param timesLeft 还剩多少次才能录入成功
         */
        void onSuccess(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer,int timesLeft);
        void onFailure(String err);

        /**
         * 三次指纹全部输入完毕且没有错误时回调
         * @param templateBuffer
         */
        void onEnrollSuccess( byte[] templateBuffer);

        /**
         * 超时回调，返回true表示继续采集 ，false表示停止采集
         * @return
         */
        boolean onCaptureTime();

    }

    void globalInit(Context context);

    /**
     * 这个函数必须在ui线程调用，必须在app结束时调用
     */
    void globalRelease();

    /**
     * 用于指纹录入
     * @param times 需要录入多少次指纹，建议3
     */
    boolean fingerEnrollStart(FingerprinEnrollEventlistener fingerprinEnrollEventlistener , int times);

    /**
     * 用于指纹录入
     */
    void fingerEnrollStop();

    /**
     * 用于指纹验证
     */
    boolean fingerVerifyStart(FingerprinEventlistener fingerprinEventlistener);

    /**
     * 用于指纹验证
     */
    void fingerVerifyStop();

}
