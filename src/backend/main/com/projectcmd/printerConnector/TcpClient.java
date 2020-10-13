package backend.main.com.projectcmd.printerConnector;

import backend.main.com.projectcmd.csvprocessor.CommandFactor;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class TcpClient {
    private static final int TIME_OUT = 10000;
    private static String ip;
    private static int port;

    public TcpClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;

    }

    public synchronized void sendBarcodes(List<String> barcodes, int start) throws IOException, InterruptedException {
        Socket client = new Socket(ip, port);
        client.setSoTimeout(TIME_OUT);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
        int beCount = start;

        byte[] stopPrev = CommandFactor.getByteArr("SR", null, null);
        out.write(stopPrev);
        out.flush();

        byte[] cleanBuffer = CommandFactor.getByteArr("KX", null, null);
        out.write(cleanBuffer);
        out.flush();

        // send be to program update barcode
        for (String barcode : barcodes) {
            byte[] curCommand = CommandFactor.getByteArr("BE", beCount + ",1", barcode);
            beCount++;
            out.write(curCommand);
            out.flush();

            try {
                String response = buf.readLine();
                System.out.println(response);
            } catch(SocketTimeoutException exception) {
                System.out.println("barcode update issue");
            }
        }

        // send n + 1 fw to buffer
        int fwCount = start;
        for (int i = 0; i <= barcodes.size(); i++) {
            byte[] curCommand = CommandFactor.getByteArr("FW", fwCount + "", null);
            fwCount++;
            out.write(curCommand);
            out.flush();

            try {
                String response = buf.readLine();
                System.out.println(response);
            } catch(SocketTimeoutException exception) {
                System.out.println("fw to buffer issue");
            }
        }

        byte[] resumePrint = CommandFactor.getByteArr("SQ", null, null);
        out.write(resumePrint);
        out.flush();

        // check status
        int status = start;
        while(status != start + barcodes.size() + 1) {
            byte[] ckStatus = CommandFactor.getByteArr("FR", null, null);
            out.write(ckStatus);
            out.flush();
            try {
                String response = buf.readLine();

                //TODO:what we will get when total count less than 80
                if(response == null) {
                    break;
                }

                System.out.println(response);
                response.split(",");
                status = Integer.parseInt(response.split(",")[1]);
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
                break;
            }

            // stop 2 secs between each check
            Thread.sleep(2000);
        }

        //TODO: STOP THE PROGRAM. DO WE NEED THIS?
        byte[] ckStatus = CommandFactor.getByteArr("SR", null, null);
        out.write(ckStatus);
        out.flush();

        if(client != null) {
            client.close();
        }
    }
}
