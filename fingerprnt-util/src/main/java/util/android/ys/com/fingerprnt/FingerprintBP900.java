package util.android.ys.com.fingerprnt;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import com.device.Device;

public class FingerprintBP900 implements Fingerprint {

    Thread regFingerThread = null;
    FingerprinEnrollEventlistener fingerprinEnrollEventlistener;
    FingerprinEventlistener fingerprinEventlistener;

    @Override
    public void globalInit(Context context) {

    }

    @Override
    public void globalRelease() {

    }

    /**
     * BP900指纹录入
     *
     * @param fingerprinEnrollEventlistener
     * @param times                         需要录入多少次指纹，建议3
     * @return
     */
    @Override
    public boolean fingerEnrollStart(FingerprinEnrollEventlistener fingerprinEnrollEventlistener, int times) {
        this.fingerprinEnrollEventlistener = fingerprinEnrollEventlistener;

        if(regFingerThread == null || regFingerThread.isAlive() == false){
            regFingerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    regFinger();
                }
            });
            regFingerThread.start();
        }
//        else {
//            regFingerThread.interrupt();
//            regFingerThread = null;
//            Device.cancel();
//        }
        return true;
    }

    @Override
    public void fingerEnrollStop() {
        if(regFingerThread.isAlive()) {
            regFingerThread.interrupt();
            regFingerThread = null;
        }
    }

    /**
     * BP900指纹验证
     * @param fingerprinEventlistener
     * @return
     */
    @Override
    public boolean fingerVerifyStart(FingerprinEventlistener fingerprinEventlistener) {
        this.fingerprinEventlistener = fingerprinEventlistener;
        if(regFingerThread == null){
            regFingerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    regFinger();
                }
            });
            regFingerThread.start();
        }
        return true;
    }

    @Override
    public void fingerVerifyStop() {

    }

    private String newGBKString(byte[] bytes) {
        try {
            return new String(bytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private void regFinger() {
        byte[] image = new byte[2000 + 152 * 200];
        byte[] message = new byte[200];
        byte[][] tz = new byte[3][513];
        byte[] mb = new byte[513];
        Device.openFinger(message);
        Device.openRfid(message);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        try {
            for (int i = 0; i < 3; i++) {
                image = new byte[2000 + 152 * 200];
                Log.d("aaa", "    采集指纹,请按手指(第" + (i + 1) + "次)...");
                int r = Device.getImage(10000, image, message);
                if (r != 0) {
                    String str = newGBKString(message);
                    Log.d("aaa", "Str == >" + str);
                    Log.d("aaa", "r   == >" + str);
                    fingerprinEnrollEventlistener.onFailure("超时 重新录入");

                    if (fingerprinEnrollEventlistener.onCaptureTime()) {
                        // 超时回调 如果true 继续采集 重新启动指纹
                        regFinger();
                    }
                    return;
                }
                r = Device.ImageToFeature(image, tz[i], message);
                if (r != 0) {
                    String str = newGBKString(message);
                    Log.d("aaaa", "str == >" + str);
                    Log.d("aaaa", "r   == >" + str);
                    return;
                }
                fingerprinEnrollEventlistener.onSuccess(0, image, null, null, i + 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 整合新指纹
        int r = Device.FeatureToTemp(tz[0], tz[1], tz[2], mb, message);
        if (r != 0) {
            String str = newGBKString(message);
            Log.d("aaaa", "str == >" + str);
            Log.d("aaaa", "r   == >" + str);
            return;
        }
        Log.d("aaaa", "录入成功");
        fingerprinEnrollEventlistener.onEnrollSuccess(mb);
        regFinger();
    }

    private void verify() {

        byte[] image = new byte[2000 + 152 * 200];
        byte[] message = new byte[200];
        byte[][] tz = new byte[3][513];
        byte[] mb = new byte[513];
        Device.openFinger(message);
        Device.openRfid(message);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        try {
            image = new byte[2000 + 152 * 200];
            int r = Device.getImage(10000, image, message);
            if (r != 0) {
                String str = newGBKString(message);
                Log.d("aaa", "Str == >" + str);
                Log.d("aaa", "r   == >" + str);
                fingerprinEventlistener.onFailure("超时 重新录入");

                if (fingerprinEnrollEventlistener.onCaptureTime()) {
                    // 超时回调 如果true 继续采集 重新启动指纹
                    regFinger();
                }
                return;
            }
            r = Device.ImageToFeature(image, tz[0], message);
            if (r != 0) {
                String str = newGBKString(message);
                Log.d("aaaa", "str == >" + str);
                Log.d("aaaa", "r   == >" + str);
                return;
            }
            fingerprinEventlistener.onSuccess(0, image, null, tz[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
