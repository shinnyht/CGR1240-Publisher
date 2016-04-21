package cisco.cgr1240;


import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shinny on 2016/04/05.
 */
public class Main {
    private static StringManager strManager = new StringManager();
    private static Map<String, String> transducerNames = organizeTransducer();

    public static void main(String[] args) {

        try {
            Publisher publisher = new Publisher("CGR1240-ALPS-Sensor");
            // SerialComm serialComm = new SerialComm("/dev/tty.usbserial-A903C71K");
            SerialComm serialComm = new SerialComm("/dev/ttyUSB0");
            SerialPort port = serialComm.getPort();

            /*
             * Send "set1" to ALPS sink node
             * This prevents garbled characters
             */
            OutputStream os = port.getOutputStream();
            os.write("set1\r".getBytes());
            os.close();

            // Prepare InputStream
            InputStream is = port.getInputStream();

            ArrayList<Character> dataArray = new ArrayList<Character>();
            String dataString;
            Map<String, String> dataMap;

            int readBytes;
            while (true) {
                readBytes = is.read();

                /*
                 * Publish sensor data when it comes to the end of value
                 * (readBytes == 13) is CR(Carriage Return) in ascii code
                 */
                if (readBytes == 13) {
                    dataString = strManager.getStringRepresentation(dataArray);
                    System.out.println(dataString);
                    if (!dataString.contains("set1") && !dataString.contains("SET OK")) {
                        dataMap = generateDataMap(dataString);

                        publisher.publishData(dataMap);
                    }
                    dataArray.clear();
                }
                else {
                    dataArray.add((char)readBytes);
                }
            }
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Format String sensor data to Map
    private static Map<String, String> generateDataMap(String dataString) {
        dataString = strManager.removeString(dataString, " ");
        dataString = strManager.removeString(dataString, "\\+");
        String[] dataSet = dataString.split(",", -1);

        Map<String, String> dataMap = new HashMap<String, String>();

        for (int i = 2; i < dataSet.length; i++) {
            String[] d = dataSet[i].split("=", -1);
            String transducer = d[0];
            String value = strManager.removeUnits(d[1]);

            dataMap.put(transducerNames.get(transducer), value);
        }
        dataMap = addLocation(dataMap);

        return dataMap;
    }

    // FIx short term representation of transducers
    private static Map<String, String> organizeTransducer () {
        Map<String, String> properTransducerName = new HashMap<String, String>();

        properTransducerName.put("TEMP", "temperature");
        properTransducerName.put("LIGHT", "light");
        properTransducerName.put("HUMI", "humidity");
        properTransducerName.put("PRES", "pressure");
        properTransducerName.put("VBAT", "voltage");
        properTransducerName.put("DOOR", "door");
        properTransducerName.put("RSSI", "RSSI");

        return properTransducerName;
    }

    // Set Tokyo Midtown's location
    private static Map<String, String> addLocation (Map<String, String> dataMap) {
        dataMap.put("latitude", "35.665575");
        dataMap.put("longitude", "139.730451");

        return dataMap;
    }
}

