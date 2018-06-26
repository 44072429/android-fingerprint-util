package util.android.ys.com.fingerprnt;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.HHDeviceControl;
import com.zkteco.android.biometric.module.fingerprint.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprint.FingerprintFactory;
import com.zkteco.android.biometric.module.fingerprint.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprint.exception.FingerprintSensorException;

import java.util.HashMap;
import java.util.Map;

import cn.pda.serialport.SerialPort;

import com.zkteco.zkfinger.FingerprintService;

/**
 * Created by 18758 on 2018/5/9.
 */
public class FingerprintZhongZhengV2 implements Fingerprint {

    private static final int VID = 6997;    //Silkid VID always 6997
    private static final int PID = 289;     //Silkid PID always 289
    private FingerprintSensor fingerprintSensor = null;
    private boolean isRegister = false;
    private int uid = 1;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0; // 表示目前按了几次
    private boolean bstart = false;
    private String TAG = "SLK20M";
    boolean mbStop = false;

    private Context context;

    // 指纹录入
    private int time;  // 需要录入几次指纹
    private int timesLeft = 3;  // 还需要录入几次指纹

    @Override
    public void globalInit(Context context) {
        this.context = context;

        powerOn();
        requestPemission();

        // Start fingerprint sensor
        startFingerprintSensor();
    }

    private void startFingerprintSensor() {

        // Define output log level
//        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        fingerprintSensor = FingerprintFactory.createFingerprintSensor(context, TransportType.USB, fingerprintParams);

    }

    // 销毁指纹模块
    @Override
    public void globalRelease() {
        if (fingerprintSensor != null) {
            FingerprintFactory.destroy(fingerprintSensor);
            powerDown();
            fingerprintSensor = null;
        }
    }


    /**
     * 用于指纹录入
     */
    @Override
    public boolean fingerEnrollStart(final FingerprinEnrollEventlistener fingerprinEnrollEventlistener, final int times) {

        boolean ret = fingerVerifyStart(new FingerprinEventlistener() {

            @Override
            public void onSuccess(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer) {

                final byte[] tmpBuffer = templateBuffer;

                if (fingerprinEnrollEventlistener != null) {

                    if (enrollidx == 2) {

                        byte[] regTemp = new byte[2048];
                        // 将三个指纹合并成一个最终指纹
                        if (0 < FingerprintService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp)) {
                            FingerprintService.save(regTemp, "test" + uid++);//Todo  改ID

                            // 将指纹回调上去
                            fingerprinEnrollEventlistener.onSuccess(captureMode, imageBuffer, imageAttributes, templateBuffer, timesLeft - enrollidx);
                            fingerprinEnrollEventlistener.onEnrollSuccess(regTemp);

                            enrollidx = 0;

                        }

                    } else {

                        // 比较是否按的是同一个指纹
                        if (enrollidx > 0 && FingerprintService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
                            fingerprinEnrollEventlistener.onFailure("请按同一个指纹三次");
                            enrollidx = 0;

                        } else {

                            System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                            enrollidx++;

                            fingerprinEnrollEventlistener.onSuccess(captureMode, imageBuffer, imageAttributes, templateBuffer, timesLeft - enrollidx);

                        }

                    }

                }
            }

            @Override
            public void onFailure(String err) {
                if (fingerprinEnrollEventlistener != null) {
                    fingerprinEnrollEventlistener.onFailure("获取指纹失败");
                }
            }

        });


        return ret;

//        requestPemission();
//        if (bstart) {
////            textView.setText("already started");
//            return true;
//        }
//
//        try {
//
//            int limit[] = new int[1];
//            //init algorithm share library
//            if (0 != FingerprintService.init(limit)) {
//                Log.d("msg", "init fpengine fail");
//                return false;
//            }
//
//            //open sensor
//            fingerprintSensor.open(0);
//
//            final FingerprintCaptureListener listener = new FingerprintCaptureListener() {
//                @Override
//                public void captureOK(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer) {
//
//                    final byte[] tmpBuffer = templateBuffer;
//
//                    if (fingerprinEnrollEventlistener != null) {
//
//                        if(timesLeft == 3){
//
//                            byte[] regTemp = new byte[2048];
//                            // 将三个指纹合并成一个最终指纹
//                            if (0 < FingerprintService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp)) {
//                                FingerprintService.save(regTemp, "test" + uid++);//Todo  改ID
//
//                                // 将指纹回调上去
//                                fingerprinEnrollEventlistener.onEnrollSuccess(regTemp);
//                                fingerprinEnrollEventlistener.onSuccess(captureMode, imageBuffer, imageAttributes, templateBuffer, timesLeft - enrollidx);
//
//                            }
//
//                        } else {
//
//                            // 比较是否按的是同一个指纹
//                            if (enrollidx > 0 && FingerprintService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
//                                fingerprinEnrollEventlistener.onFailure("请按同一个指纹三次");
//                                enrollidx = 0;
//
//                            } else {
//
//                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
//                                enrollidx++;
//
//                                fingerprinEnrollEventlistener.onSuccess(captureMode, imageBuffer, imageAttributes, templateBuffer, timesLeft - enrollidx);
//
//                            }
//
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void captureError(FingerprintSensorException e) {
//
//                    if (fingerprinEnrollEventlistener != null) {
//                        fingerprinEnrollEventlistener.onFailure("获取指纹失败");
//                    }
//
//                }
//            };
//
//            fingerprintSensor.setFingerprintCaptureListener(0, listener);
//            fingerprintSensor.startCapture(0);
//            fingerprintSensor.setFingerprintCaptureMode(0, FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE);
//
//            bstart = true;
//
//        } catch (FingerprintSensorException e) {
//
//        }
//
//        if (fingerprintSensor == null) {
//            return false;
//        }
//        return true;

    }

    /**
     * 用于指纹录入
     */
    @Override
    public void fingerEnrollStop() {

        fingerVerifyStop();

    }

    /**
     * 用于指纹验证
     */
    @Override
    public boolean fingerVerifyStart(final FingerprinEventlistener fingerprinEventlistener) {

        requestPemission();
        if (bstart) {
//            textView.setText("already started");
            return true;
        }

        try {

            int limit[] = new int[1];
            //init algorithm share library
            if (0 != FingerprintService.init(limit)) {
                Log.d("msg", "init fpengine fail");
                return false;
            }

            //open sensor
            fingerprintSensor.open(0);

            final FingerprintCaptureListener listener = new FingerprintCaptureListener() {
                @Override
                public void captureOK(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer) {
                    if (fingerprinEventlistener != null) {
                        fingerprinEventlistener.onSuccess(captureMode, imageBuffer, imageAttributes, templateBuffer);
                    }
                }

                @Override
                public void captureError(FingerprintSensorException e) {

                    if (fingerprinEventlistener != null) {
                        fingerprinEventlistener.onFailure("获取指纹失败");
                    }
                }
            };

            fingerprintSensor.setFingerprintCaptureListener(0, listener);
            fingerprintSensor.startCapture(0);
            fingerprintSensor.setFingerprintCaptureMode(0, FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE);

            bstart = true;

        } catch (FingerprintSensorException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 用于指纹验证 停止
     */
    @Override
    public void fingerVerifyStop() {
        try {
            if (bstart) {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
                isRegister = false;
                enrollidx = 0;
//                textView.setText("stop capture succ");
            } else {
//                textView.setText("already stop");
            }
        } catch (FingerprintSensorException e) {
//            textView.setText("stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean findDevice() {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                return true;
            }
        }
        return false;
    }

    private int InitUsbDevice() {
        int ret = 0;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        Intent intent = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intent.addCategory("android.hardware.usb.action.USB_DEVICE_DETACHED");
        Map<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
        Log.i(TAG, "Init usb devices, device size = " + usbDeviceList.size());
        if (null != usbDeviceList && usbDeviceList.size() > 0) {
            for (UsbDevice device : usbDeviceList.values()) {
                Log.i(TAG, "requestPression vid=" + device.getVendorId() + ",pid=" + device.getProductId());
                if (VID == device.getVendorId() && PID == device.getProductId()) {
                    if (usbManager.hasPermission(device)) {
//                        textView.setText("hasPermission");
                        Log.i(TAG, "hasPermission");
                    } else {
//                        textView.setText("requestPermission");
                        Log.i(TAG, "requestPermission");
                        PendingIntent mIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                        usbManager.requestPermission(device, mIntent);
                    }
                    ret = 1;
                    break;
                }
            }
        }

        return ret;
    }


    private void powerOn() {
        //HHDeviceControl.HHDeviceGpioHigh(92);
        //HHDeviceControl.HHDeviceGpioHigh(115);
        HHDeviceControl.HHDevicePowerOn("5V");
        HHDeviceControl.HHDeviceGpioLow(141);
    }

    private void powerDown() {
        //HHDeviceControl.HHDeviceGpioLow(92);
        //HHDeviceControl.HHDeviceGpioLow(115);
        HHDeviceControl.HHDeviceGpioHigh(141);
        HHDeviceControl.HHDevicePowerOff("5V");
    }

    private void requestPemission() {
        //等待模块上电
        long lTickStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - lTickStart < 5 * 1000) {
            if (findDevice()) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int times = 0;
        long start_time = System.currentTimeMillis();
        while (System.currentTimeMillis() - start_time < 1000) {
            int ret = InitUsbDevice();

            if (1 == ret) {
//                textView.setText("hasPermission,tryTimes=" + ++times);
                Log.i(TAG, "hasPermission,tryTimes=" + times);
                break;
            }
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }
}

