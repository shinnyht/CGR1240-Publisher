package cisco.cgr1240;

import gnu.io.*;

/**
 * Created by shinny on 2016/04/06.
 */
public class SerialComm {
    private static SerialPort port;

    public SerialComm (String commPort) {
        try {
            CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(commPort);
            this.port = (SerialPort) portID.open("Main", 5000);

            port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
    }

    public SerialPort getPort() {
        return port;
    }
}
