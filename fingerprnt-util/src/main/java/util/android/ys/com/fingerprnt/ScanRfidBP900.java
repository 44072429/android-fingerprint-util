package util.android.ys.com.fingerprnt;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.device.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScanRfidBP900 implements  ScanRfid {

    private List<Map<String, String>> rfids;
    boolean flag = false;

    private Thread thread = null;
    ErrHandler errhandle = null;

    RfidEventlistener rfidEventlistener;

    @Override
    public void openRfid(RfidEventlistener rfidEventlistener) {
        this.rfidEventlistener = rfidEventlistener;
        rfids = new ArrayList<Map<String,String>>();
        rfids.clear();
        if (thread == null) {
            thread = new Thread(new VerifyBox()) {
            };
            errhandle = new ErrHandler();
            thread.setUncaughtExceptionHandler(errhandle);
            thread.start();
        }

    }

    @Override
    public void closeRfid() {
        try {
            if (thread != null) {
                flag = false;
                thread.interrupt();
                thread = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class VerifyBox implements Runnable {
        public void run() {
            Message msg = new Message();
            try {
                flag = true;
                while (flag) {

                    thread.sleep(1000);

                    byte[] tids = new byte[20000];
                    byte[] epcids = new byte[20000];
                    byte[] errmsg = new byte[100];

                    int result = Device.getRfid(1000, tids, epcids,
                            errmsg);

                    if (result != 0) {
                        Log.d("Rfid", "scan VERIFY_ERROR");
                        rfidEventlistener.onFailure("scan VERIFY_ERROR");
                    }

                    String epc = new String(epcids).trim();
                    String t = new String(tids).trim();
                    Map<String, String> rf = new HashMap<String, String>();
                    rf.put("epcid", epc);
                    rf.put("tid", t);

                    Log.d("Rfid", "scan VERIFY_SUCCESS");
                    initBoxRfid(rf);

                }

            } catch (Exception ex) {
                Log.d("Rfid", "scan 验证箱包出现错误");
                rfidEventlistener.onFailure("scan 验证箱包出现错误");
            }
        }
    }


    public void initBoxRfid(Map<String, String> rfid) {
        String epcids = rfid.get("epcid");
//		String tids=rfid.get("tid");
        if (!TextUtils.isEmpty(epcids)) {
            String[] etpcidarr = epcids.split(",");
//			String[] tidarr=tids.split(",");
            for (int i = 0; i < etpcidarr.length; i++) {
                if (!isContainsRfid(etpcidarr[i].trim())) {
                    Map<String, String> rfidObj = new HashMap<String, String>();
//					rfidObj.put("tid", tidarr[i].trim());

                    Log.d("Rfid", etpcidarr[i].trim());
                    rfidEventlistener.onSuccess(etpcidarr[i].trim());
                    rfidObj.put("epcid", etpcidarr[i].trim());
                    rfids.add(rfidObj);
                }
            }
        }
//			if(!isContainsRfid(rfid.get("tid"))){
//				rfids.add(rfid);
//				rfidAdapter.setRfids(rfids);
//				mListView.setAdapter(rfidAdapter);
//				rfidAdapter.notifyDataSetChanged();
//			}
    }

    public boolean isContainsRfid(String rfid) {
        boolean contains = false;
        for (Map<String, String> r : rfids) {
            if (TextUtils.equals(r.get("tid"), rfid)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    class ErrHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread a, Throwable e) {
            Log.d("Rfid", "线程错误");
        }
    }


}
