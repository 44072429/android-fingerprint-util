package com.device;

public class Device {
    static {
        try {
            System.loadLibrary("Device");
        } catch (UnsatisfiedLinkError e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *  获取指纹图像
     * @param timeout   超时时间    10000
     * @param finger    指纹图像数据 长度 2000+152*200
     * @param message   返回错误信息 长度 200
     * @return
     */
    public native static int getImage(int timeout, byte[] finger, byte[] message);

    /**
     *
     * @param timeout   超时时间
     * @param finger    指纹特征数据 长度 256
     * @param message   返回错误信息 长度 200
     * @return
     */
    public native static int getFinger(int timeout, byte[] finger, byte[] message);

    public native static int setParam(byte[] message);

    /**
     * 图像转特征
     * @param image     图像数据 长度 2000+152*200
     * @param feature   特征数据 长度 513
     * @param message
     * @return
     */
    public native static int ImageToFeature(byte[] image, byte[] feature, byte[] message);

    public native static int FeatureToTemp(byte[] tz1, byte[] tz2,
                                           byte[] tz3, byte[] mb, byte[] message);

    public native static int verifyFinger(String mbFinger, String tzFinger, int level);

    public native static int verifyBinFinger(byte[] mbFinger, byte[] tzFinger, int level);

    public native static int getRfid(int timeout, byte[] epc, byte[] epcid, byte[] message);

    public native static int cancel();

    public native static int readIdCard(int timeout, byte[] textData,
                                        byte[] photoData, byte[] message);

    public native static int openRfid(byte[] message);

    public native static int closeRfid(byte[] message);

    public native static int openFinger(byte[] message);

    public native static int closeFinger(byte[] message);
}
