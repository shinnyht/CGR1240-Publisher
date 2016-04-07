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
    private static Map<String, String> ALPS_TRANSDUCERS = new HashMap<String, String>();

    public static void main (String[] args) throws IOException, XMPPException, SmackException {
        NodeCreator nodeCreator = new NodeCreator();
        // Create sensor node on sox
        nodeCreator.setTransducerMap();
        nodeCreator.createNode("CGR1240-ALPS-Sensor");
    }

    private NodeCreator() throws IOException, XMPPException, SmackException {
        // Connect to SoX server
        conn = new SoxConnection("sox.ht.sfc.keio.ac.jp", "guest", "miroguest", true);
    }

    private void setTransducerMap () {
        ALPS_TRANSDUCERS.put("latitude", "lat");
        ALPS_TRANSDUCERS.put("longitude", "lon");
        ALPS_TRANSDUCERS.put("temperature", "Celsius");
        ALPS_TRANSDUCERS.put("light", "lux");
        ALPS_TRANSDUCERS.put("humidity", "%");
        ALPS_TRANSDUCERS.put("pressure", "hPa");
        ALPS_TRANSDUCERS.put("voltage", "V");
        ALPS_TRANSDUCERS.put("door", "Open/Close");
        ALPS_TRANSDUCERS.put("RSSI", "dBm");
    }

    private void createNode (String nodeName) {
        Device device = new Device();
        device.setId(nodeName); 
        device.setDeviceType(DeviceType.INDOOR_WEATHER);
        device.setName(nodeName); 

        List<Transducer> transducerList = new ArrayList<Transducer>();

        for (String t: ALPS_TRANSDUCERS.keySet()) {
            Transducer transducer = new Transducer();
            transducer.setName(t);
            transducer.setId(t);
            transducer.setUnits(ALPS_TRANSDUCERS.get(t));
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
