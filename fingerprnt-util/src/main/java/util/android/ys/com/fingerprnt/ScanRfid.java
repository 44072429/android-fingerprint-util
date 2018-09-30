package util.android.ys.com.fingerprnt;

import android.content.Context;

public interface ScanRfid {

    interface RfidEventlistener {
        /**
         * @param epcid 卡id
         */
        void onSuccess(String epcid);
        void onFailure(String err);
    }

    void openRfid(RfidEventlistener rfidEventlistener);
    void closeRfid();

}
