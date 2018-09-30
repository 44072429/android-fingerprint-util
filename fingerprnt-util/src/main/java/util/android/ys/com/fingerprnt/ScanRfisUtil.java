package util.android.ys.com.fingerprnt;

public class ScanRfisUtil {

    public static ScanRfid scanRfid;

    // 指纹录入
    public static void  ScanRfisStart(final ScanRfid.RfidEventlistener rfidEventlistener) {

        String devType = "BP900";

        if (devType == "BP900") {
            scanRfid = new ScanRfidBP900();
        }

        if (scanRfid != null) {
            scanRfid.openRfid( rfidEventlistener );
        }

    }

    public static void  ScanRfisStop() {
        if (scanRfid != null) {
            scanRfid.closeRfid();
        }
    }

}
