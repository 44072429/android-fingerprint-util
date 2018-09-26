package com.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:超高频操作，批量读取超高频卡片</p>
 * @version 1.2.6
 */
public class BatchRfid {
	private static BatchRfid instance;
	/**
	 * <p>Description:批量读取的rfid结果集，以map的形式存储键分别是tid和epcid</p>
	 */

	private BatchRfid() {
	}

	private static boolean isstop = false;
	/**
	 * 
	 * <p>Description: 创建批量取rfid对象</p>
	 * @return BatchRfid 返回批量取rfid的对象
	 */
	public static synchronized BatchRfid getInstance() {
		if (instance == null) {
			instance = new BatchRfid();
		}
		return instance;
	}

	/**
	 * 
	 * <p>Description: 开始读取超高频卡片</p>
	 * @param time 用于定义返回时间
	 * @return BatchRfid 返回扫描结果以，List<map<string,string>>形式返回 
	 */
	public List<Map<String,String>> start(int time) throws Exception {
		isstop = false;
		List<Map<String,String>> rfids = new ArrayList<Map<String,String>>();
		long startTime = System.currentTimeMillis();
		long sptime = 0;
		byte[] boxrfid = new byte[200];
		byte[] tid = new byte[200];
		byte[] errmsg = new byte[100];
		while (sptime <= time) {
			long endTime = System.currentTimeMillis();
			sptime = endTime - startTime;
			int result = Device.getRfid(1000,tid, boxrfid, errmsg);
			if (result == 0) {
				Map<String,String> rfid=new HashMap<String,String>();
				rfid.put("epcid", new String(boxrfid).trim());
				rfid.put("tid", new String(tid).trim());
				if(rfids!=null){
					if(!isContainsRfid(rfids,rfid.get("tid"))){
						rfids.add(rfid);
				}
			}
			}
			if (isstop) {
				break;
			}
		}
		return rfids;
	}

	private boolean isContainsRfid(List<Map<String,String>> rfids, String tid){
		boolean contains=false;
		for(Map<String,String> r:rfids){
			if(r.get("tid").equals(tid)){
				contains=true;
				break;
			}
		}
		return contains;
	}
	/**
	 * 
	 * <p>Description: 停止批量读取超高频卡片</p>
	 */
	public void stop() {
		isstop = true;
	}
}
