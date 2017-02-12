package none.rg.jssctest;

import java.util.*;
import jssc.*;

public class App {
    
    private Scanner input;
    private SerialPort port;
    private StringBuffer incomingLine = new StringBuffer();
    
    public static void main(String... args) {
        App app = new App();
        try {
            app.init();
        } catch (Exception e) {
            System.out.println("Error during initialization: " + e.getMessage());
            return;
        }
        app.run();
        app.closePort();
    }
    
    private void init() {
        input = new Scanner(System.in);
        String portName = choosePort();
        port = openPort(portName);
        setClosePortHook();
    }
    
    private void closePort() {
        try {
            if (port != null && port.isOpened()) {
                System.out.println("Closing port...");
                port.closePort();
            }
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String choosePort() {
        String[] ports = SerialPortList.getPortNames();
        if (ports.length < 1) {
            throw new RuntimeException("No serial ports found at all");
        }
        for (int i = 0; i < ports.length; i++) {
            System.out.printf("%d) %s%n", i + 1, ports[i]);
        }
        int choice = input.nextInt();
        if (choice < 1 || choice > ports.length) {
            throw new RuntimeException("Choose better next time");
        }
        return ports[choice - 1];
    }
    
    private SerialPort openPort(String name) {
        SerialPort port = new SerialPort(name);
        try {
            if (!port.openPort()) {
                throw new RuntimeException("Port opening returns 'false'");
            }
            if (!port.setParams(9600, 8, 1, 0)) {
                throw new RuntimeException("Setting port params returns 'false'");
            }
        } catch (SerialPortException e) {
            throw new RuntimeException("Oh, what a boolsheet: " + e);
        }
        return port;
    }
    
    private void setClosePortHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closePort();
        }));
    }
    
    private void run() {
        System.out.println("We are ready, input lines... Enter 'bye' to exit.");
        while (true) {
            String textToSend = input.nextLine();
            System.out.println("You entered: '" + textToSend + "'");
            if ("bye".equalsIgnoreCase(textToSend.trim())) {
                break;
            }
            sendString(textToSend + "\r");
            waitMillis(200);
            receiveBytes(readPort());
        }
        System.out.println("Good bye!");
    }
    
    private byte[] readPort() {
        byte[] result = null;
        try {
            result = port.readBytes();
        } catch (SerialPortException e) {
            // fall through
        }
        return result != null ? result : new byte[0];
    }
    
    private void receiveBytes(byte[] b) {
        String s = new String(b);
        incomingLine.append(s);
        int lineEndPos = incomingLine.indexOf("\r");
        if (lineEndPos >= 0) {
            System.out.println(incomingLine.substring(0, lineEndPos));
            incomingLine.delete(0, lineEndPos + 1);
        }
    }
    
    private void sendString(String s) {
        try {
            port.writeString(s);
        } catch (SerialPortException e) {
            throw new RuntimeException("Exception on sending: " + e);
        }
    }
    
    private void waitMillis(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // never mind, another boolsheet
        }
    }
    
}

