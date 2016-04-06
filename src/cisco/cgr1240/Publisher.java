package cisco.cgr1240;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.keio.sfc.ht.sox.protocol.TransducerValue;
import jp.ac.keio.sfc.ht.sox.soxlib.SoxConnection;
import jp.ac.keio.sfc.ht.sox.soxlib.SoxDevice;

public class Publisher {
	private static SoxDevice soxDevice;

	public Publisher (String nodeName) throws Exception {
        SoxConnection con = new SoxConnection("sox.ht.sfc.keio.ac.jp", true); //anonymous login
        this.soxDevice = new SoxDevice(con, nodeName);
	}

	public void publishData(Map<String, String> dataMap) {
        List<TransducerValue> valueList = new ArrayList<TransducerValue>();

		for (String str: dataMap.keySet()) {
			TransducerValue value = new TransducerValue();
			value.setId(str);
			value.setRawValue(dataMap.get(str));
			value.setTypedValue(dataMap.get(str));
			value.setCurrentTimestamp();

			valueList.add(value);
		}

        this.soxDevice.publishValues(valueList);

        System.out.println("Published !");
	}
}
