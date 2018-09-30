package util.android.ys.com.androidmediautil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.device.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.android.ys.com.androidmediautil.util.BaseRecyclerAdapter;
import util.android.ys.com.fingerprnt.ScanRfid;
import util.android.ys.com.fingerprnt.ScanRfisUtil;

public class ScanRfidBP900Activity extends AppCompatActivity {

    @BindView(R.id.rv_item)
    RecyclerView rvItem;

    private RfidAdapter rfidAdapter;
    private List<String> mList = new ArrayList<>();

    private List<Map<String, String>> rfids;
    boolean flag = false;

    private Thread thread = null;
//    ErrHandler errhandle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_rfid_bp900);
        ButterKnife.bind(this);

//        rfids = new ArrayList<Map<String,String>>();
//        rfids.clear();

        rfidAdapter = new RfidAdapter(ScanRfidBP900Activity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ScanRfidBP900Activity.this);
        rvItem.setLayoutManager(linearLayoutManager);
        rvItem.setAdapter(rfidAdapter);

    }

    @OnClick(R.id.bt_access)
    public void onViewClicked() {
//        if (thread == null) {
//            thread = new Thread(new VerifyBox()) {
//            };
//            errhandle = new ErrHandler();
//            thread.setUncaughtExceptionHandler(errhandle);
//            thread.start();
//        }

        ScanRfisUtil.ScanRfisStart(new ScanRfid.RfidEventlistener() {
            @Override
            public void onSuccess(final String epcid) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mList.size() == 6) {
                            mList.clear();
                            mList.add(epcid);
                            rfidAdapter.setDatas(mList);
                        } else {
                            mList.add(epcid);
                            rfidAdapter.setDatas(mList);
                        }
                    }
                });

            }

            @Override
            public void onFailure(final String err) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                Toast.makeText(ScanRfidBP900Activity.this, err, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public class RfidAdapter extends BaseRecyclerAdapter<String, RfidAdapter.HoldView> {

        Activity activity;
        LayoutInflater inflater;

        public RfidAdapter(Context context) {
            super(context);
            this.activity = (Activity) context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public HoldView onCreateHolder(ViewGroup parent, int viewType) {
            return new HoldView(inflater.inflate(R.layout.recy_item_rfid, parent, false));
        }

        @Override
        public void onBind(HoldView viewHolder, int realPosition, final String data) {
            viewHolder.tvText.setText(data);
        }

        public class HoldView extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_text)
            TextView tvText;

            public HoldView(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


//    public boolean isContainsRfid(String rfid) {
//        boolean contains = false;
//        for (Map<String, String> r : rfids) {
//            if (TextUtils.equals(r.get("tid"), rfid)) {
//                contains = true;
//                break;
//            }
//        }
//        return contains;
//    }
//
//    public class VerifyBox implements Runnable {
//        public void run() {
//            Message msg = new Message();
//            try {
//                flag = true;
//                while (flag) {
//
//                    byte[] tids = new byte[20000];
//                    byte[] epcids = new byte[20000];
//                    byte[] errmsg = new byte[100];
//
//                    int result = Device.getRfid(1000, tids, epcids,
//                            errmsg);
//
//                    if (result != 0) {
//                        Log.d("Rfid", "scan VERIFY_ERROR");
////                        msg = new Message();
////                        msg.what = VERIFY_ERROR;
////                        handler.sendMessage(msg);
////                        continue;
//                    }
//
//                    String epc = new String(epcids).trim();
//                    String t = new String(tids).trim();
//                    Map<String, String> rf = new HashMap<String, String>();
//                    rf.put("epcid", epc);
//                    rf.put("tid", t);
//
//                    Log.d("Rfid", "scan VERIFY_SUCCESS");
//
//                    initBoxRfid(rf);
////                    msg = new Message();
////                    msg.what = VERIFY_SUCCESS;
////                    msg.obj = rf;
////                    handler.sendMessage(msg);
//
//                }
//
//            } catch (Exception ex) {
//                Log.d("Rfid", "scan 验证箱包出现错误");
////                msg = new Message();
////                msg.what = VERIFY_ERROR;
////                msg.obj = "验证箱包出现错误";
////                handler.sendMessage(msg);
//            }
//        }
//    }
//
//
//    public void initBoxRfid(Map<String, String> rfid) {
//        String epcids = rfid.get("epcid");
////		String tids=rfid.get("tid");
//        if (!TextUtils.isEmpty(epcids)) {
//            String[] etpcidarr = epcids.split(",");
////			String[] tidarr=tids.split(",");
//            for (int i = 0; i < etpcidarr.length; i++) {
//                if (!isContainsRfid(etpcidarr[i].trim())) {
////                    ++rfidcount;
////                    tv_rfidcount.setText("扫描箱包 " + rfidcount + " 个");
//                    Map<String, String> rfidObj = new HashMap<String, String>();
////					rfidObj.put("tid", tidarr[i].trim());
//
////                    if (mList.size() == 6) {
////                        mList.clear();
////                        mList.add(etpcidarr[i].trim());
////                        rfidAdapter.setDatas(mList);
////                    } else {
////                        mList.add(etpcidarr[i].trim());
////                        rfidAdapter.setDatas(mList);
////                    }
//
//                    Log.d("Rfid", etpcidarr[i].trim());
//
//                    rfidObj.put("epcid", etpcidarr[i].trim());
//                    rfids.add(rfidObj);
////                    rfidAdapter.setRfids(rfids);
////                    mListView.setAdapter(rfidAdapter);
////                    rfidAdapter.notifyDataSetChanged();
//                }
//            }
//        }
////			if(!isContainsRfid(rfid.get("tid"))){
////				rfids.add(rfid);
////				rfidAdapter.setRfids(rfids);
////				mListView.setAdapter(rfidAdapter);
////				rfidAdapter.notifyDataSetChanged();
////			}
//    }
//
//    class ErrHandler implements Thread.UncaughtExceptionHandler {
//        public void uncaughtException(Thread a, Throwable e) {
//            Log.d("Rfid", "线程错误");
//        }
//    }

}
