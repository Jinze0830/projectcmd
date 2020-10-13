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
        int count = start;

        for (String barcode : barcodes) {
            byte[] curCommand = CommandFactor.getByteArr("BE", count + ",1", barcode);
            count++;
            out.write(curCommand);
            out.flush();

            try {
                String response = buf.readLine();
                System.out.println(response);
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
            }
        }

        // check status
        int status = start;
        while(status != start + 80) {
            byte[] ckStatus = CommandFactor.getByteArr("FR", "02", null);
            out.write(ckStatus);
            out.flush();
            try {
                String response = buf.readLine();
                System.out.println(response);
                response.split(",");
                status = Integer.parseInt(response.split(",")[1]);
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
                break;
            }

            Thread.sleep(2000);
        }

        byte[] ckStatus = CommandFactor.getByteArr("SR", null, null);
        out.write(ckStatus);
        out.flush();

        if(client != null) {
            client.close();
        }
    }
}
