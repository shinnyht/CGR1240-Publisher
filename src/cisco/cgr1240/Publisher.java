package cisco.cgr1240;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.keio.sfc.ht.sox.protocol.TransducerValue;
import jp.ac.keio.sfc.ht.sox.soxlib.SoxConnection;
import jp.ac.keio.sfc.ht.sox.soxlib.SoxDevice;

/**
 * Created by shinny on 2016/04/05.
 */
public class Publisher {
    private static SoxDevice soxDevice;

    public Publisher (String nodeName) throws Exception {
        // Create sox connection to sox.ht.sfc.keio.ac.jp server
        SoxConnection con = new SoxConnection("sox.ht.sfc.keio.ac.jp", true); //anonymous login
        soxDevice = new SoxDevice(con, nodeName); // sensor device
    }

    public void publishData(Map<String, String> dataMap) {
        List<TransducerValue> valueList = new ArrayList<TransducerValue>();

        // Add each transducer value to the List
        for (String str: dataMap.keySet()) {
            TransducerValue value = new TransducerValue();
            value.setId(str);
            value.setRawValue(dataMap.get(str));
            value.setTypedValue(dataMap.get(str));
            value.setCurrentTimestamp();

            valueList.add(value);
        }

        System.out.println("Publishing...");
        // Publish data to sox.ht.sfc.keio.ac.jp
        soxDevice.publishValues(valueList);
        System.out.println("Published");
    }
}
