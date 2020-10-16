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

    public synchronized void sendBarcodes(List<String> barcodes, int start, String lotNumber) throws IOException, InterruptedException {
        Socket client = new Socket(ip, port);
        client.setSoTimeout(TIME_OUT);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
        int beCount = start;

        byte[] stopPrev = CommandFactor.getByteArr("SR", null, null);
        out.write(stopPrev);
        out.flush();
        printResponse(buf);

        byte[] cleanBuffer = CommandFactor.getByteArr("KX", null, null);
        out.write(cleanBuffer);
        out.flush();
        printResponse(buf);

        // send be to program update barcode
        for (String barcode : barcodes) {
            byte[] beCommand = CommandFactor.getByteArr("BE", beCount + ",1", barcode);
            out.write(beCommand);
            out.flush();

//            byte[] fsCommand = CommandFactor.getByteArr("FS", beCount + ",1,0", lotNumber);
//            out.write(fsCommand);
//            out.flush();
            beCount++;
            Thread.sleep(50);
            printResponse(buf);
        }

//        int fsCount = start;
//        for (int i = 0; i < barcodes.size(); i++) {
//            byte[] curCommand = CommandFactor.getByteArr("FS", fsCount + ",1,0", lotNumber);
//            fsCount++;
//            out.write(curCommand);
//            out.flush();
//        }

        // send n + 1 fw to buffer
        int fwCount = start;
        for (int i = 0; i <= barcodes.size(); i++) {
            byte[] curCommand = CommandFactor.getByteArr("FW", fwCount + "", null);
            fwCount++;
            Thread.sleep(500);
            out.write(curCommand);
            out.flush();
            printResponse(buf);
        }

        byte[] resumePrint = CommandFactor.getByteArr("SQ", null, null);
        out.write(resumePrint);
        out.flush();
        printResponse(buf);

        // check status
        int status = start;
        while(status != start + barcodes.size()) {
            byte[] ckStatus = CommandFactor.getByteArr("FR", null, null);
            out.write(ckStatus);
            out.flush();
            try {
                String response = buf.readLine();

                System.out.println("FR RESPONSE: " + response);
                if (response != null) {
                    String[] curResponse = response.split(",");
                    if (curResponse[0].equals("FR")) {
                        status = Integer.parseInt(curResponse[1]);
                    }
                }
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
                break;
            }

            // stop 1 secs between each check
            Thread.sleep(500);
        }

        //TODO: STOP THE PROGRAM. DO WE NEED THIS?
        byte[] ckStatus = CommandFactor.getByteArr("SR", null, null);
        out.write(ckStatus);
        out.flush();
        printResponse(buf);

        if(client != null) {
            client.close();
        }
    }

    private void printResponse(BufferedReader buf ) {
        try {
            String response = buf.readLine();
            System.out.println("RESPONSE: " + response);
        } catch(SocketTimeoutException exception) {
            System.out.println("fw to buffer issue");
        } catch(IOException e) {

        }
    }
}
