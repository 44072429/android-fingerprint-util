package util.android.ys.com.fingerprnt;

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

    //指纹相关
    private static FingerprintSensor fingerprintSensor = null;

    private static final int VID = 6997;    //Silkid VID always 6997
    private static final int PID = 289;     //Silkid PID always 289

    Context context;

    // 初始化指纹模块
    void globalInit() {
        powerOn();
        requestPemission();
        // Start fingerprint sensor
        startFingerprintSensor();
    }

    // 销毁指纹模块
    void globalRelease() {


        try {

            fingerprintSensor.stopCapture(0);
            fingerprintSensor.close(0);

        } catch (FingerprintSensorException e) {

        }

//        if (fingerprintSensor != null) {
//            FingerprintFactory.destroy(fingerprintSensor);
//            fingerprintSensor = null;
//        }
//        powerDown();

    }


    /**
     * 用于指纹录入
     */
    @Override
    public boolean startCaptrue(final FingerprinEventlistener fingerprinEventlistener, Context context) {

        this.context = context;
        globalInit();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {

            int limit[] = new int[1];
            //init algorithm share library
            if (0 != FingerprintService.init(limit)) {
                Log.d("msg" , "init fpengine fail");
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

        } catch (FingerprintSensorException e) {

        }

        if (fingerprintSensor == null) {
            return false;
        }
        return true;

    }

    /**
     * 用于指纹录入
     */
    @Override
    public void stopCaptrue() {

        if (fingerprintSensor != null) {
            globalRelease();
        }

    }

    /**
     * 用于指纹验证
     */
    @Override
    public boolean fingerStart(final FingerprinEventlistener fingerprinEventlistener, Context context) {

        this.context = context;
        globalInit();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requestPemission();
        try {

            int limit[] = new int[1];
            //init algorithm share library
            if (0 != FingerprintService.init(limit)) {
                Log.d("msg" , "init fpengine fail");
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

        } catch (FingerprintSensorException e) {

        }

        if (fingerprintSensor == null) {
            return false;
        }
        return true;

    }


    /**
     * 用于指纹验证 停止
     */
    @Override
    public void fingerStop() {

        if (fingerprintSensor != null) {
            globalRelease();
        }

    }


    /**
     *  销毁 释放充电口
     */
    @Override
    public void destroy() {
        FingerprintFactory.destroy(fingerprintSensor);
        powerDown();
    }

    private void startFingerprintSensor() {

        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);

        fingerprintSensor = FingerprintFactory.createFingerprintSensor(context, TransportType.USB, fingerprintParams);

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
//        Log.i(TAG, "Init usb devices, device size = " + usbDeviceList.size() );
        if (null != usbDeviceList && usbDeviceList.size() > 0) {
            for (UsbDevice device : usbDeviceList.values()) {
//                Log.i(TAG, "requestPression vid=" + device.getVendorId() + ",pid=" + device.getProductId());
                if (VID == device.getVendorId() && PID == device.getProductId()) {
                    if (usbManager.hasPermission(device)) {
//                        textView.setText("hasPermission");
//                        Log.i(TAG, "hasPermission");
                    } else {
//                        textView.setText("requestPermission");
//                        Log.i(TAG, "requestPermission");
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

    private void requestPemission() {
        // 等待模块上电
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
//                Log.i(TAG, "hasPermission,tryTimes=" + times);
                break;
            }
            try {
                Thread.sleep(300);
            } catch (Exception e) {
//                Log.i(TAG, e.getMessage());
            }
        }
    }

}

