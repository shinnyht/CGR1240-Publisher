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

    public static void main(String[] args) {

        try {
            Publisher publisher = new Publisher("CGR1240-ALPS-Sensor");
            SerialComm serialComm = new SerialComm("/dev/tty.usbserial-A903C71K");
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
                    dataMap = generateDataMap(dataString);

                    publisher.publishData(dataMap);
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

        for (int i = 1; i < dataSet.length; i++) {
            String[] d = dataSet[i].split("=", -1);
            String transducer = d[0];
            String value = strManager.removeUnits(d[1]);

            dataMap.put(transducer, value);
        }

        return dataMap;
    }
}
