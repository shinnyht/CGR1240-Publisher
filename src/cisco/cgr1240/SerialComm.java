package cisco.cgr1240;

import gnu.io.*;

/**
 * Created by shinny on 2016/04/06.
 */
public class SerialComm {
    private static SerialPort port;

    public SerialComm (String commPort) {
        try {
            // Open target serial ports
            CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(commPort);
            this.port = (SerialPort) portID.open("Main", 5000);

            // Set serial port parameters
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

    // Getter method for serial port
    public SerialPort getPort() {
        return port;
    }
}
