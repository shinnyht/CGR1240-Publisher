package cisco.cgr1240;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.keio.sfc.ht.sox.protocol.Device;
import jp.ac.keio.sfc.ht.sox.protocol.DeviceType;
import jp.ac.keio.sfc.ht.sox.protocol.Transducer;
import jp.ac.keio.sfc.ht.sox.soxlib.SoxConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.PublishModel;

/**
 * Created by shinny on 2016/04/05.
 */
public class NodeCreator {
    private static SoxConnection conn;

    public static void main (String[] args) throws IOException, XMPPException, SmackException {
        Map<String, String> alpsTransducers = new HashMap<String, String>();
        Map<String, String> processedTransducers = new HashMap<String, String>();
        NodeCreator nodeCreator = new NodeCreator();

        // Create sensor node on sox
        nodeCreator.setALPSTransducerMap(alpsTransducers);
        nodeCreator.createNode("CGR1240-ALPS-Sensor", alpsTransducers);

        nodeCreator.setProcessedTransducerMap(processedTransducers);
        nodeCreator.createNode("CGR1240-Processed-Sensor", processedTransducers);
    }

    private NodeCreator() throws IOException, XMPPException, SmackException {
        // Connect to SoX server
        conn = new SoxConnection("sox.ht.sfc.keio.ac.jp", "guest", "miroguest", true);
    }

    private void setALPSTransducerMap (Map<String, String> alpsTransducers) {
        alpsTransducers.put("latitude", "lat");
        alpsTransducers.put("longitude", "lon");
        alpsTransducers.put("temperature", "Celsius");
        alpsTransducers.put("light", "lux");
        alpsTransducers.put("humidity", "%");
        alpsTransducers.put("pressure", "hPa");
        alpsTransducers.put("voltage", "V");
        alpsTransducers.put("door", "Open/Close");
        alpsTransducers.put("RSSI", "dBm");
    }

    private void setProcessedTransducerMap (Map<String, String> processedTransducers) {
        processedTransducers.put("latitude", "lat");
        processedTransducers.put("longitude", "lon");
        processedTransducers.put("Discomfortness", "");
    }

    private void createNode (String nodeName, Map<String, String> transducers) {
        Device device = new Device();
        device.setId(nodeName); 
        device.setDeviceType(DeviceType.INDOOR_WEATHER);
        device.setName(nodeName); 

        List<Transducer> transducerList = new ArrayList<Transducer>();

        for (String t: transducers.keySet()) {
            Transducer transducer = new Transducer();
            transducer.setName(t);
            transducer.setId(t);
            transducer.setUnits(transducers.get(t));
            transducerList.add(transducer);
        }

        device.setTransducers(transducerList);

        // Create sensor node
        try {
            conn.createNode(nodeName, device, AccessModel.open, PublishModel.open);
            System.out.println("Node Created!");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
