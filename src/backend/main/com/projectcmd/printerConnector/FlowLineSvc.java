package backend.main.com.projectcmd.printerConnector;

import backend.main.com.projectcmd.csvprocessor.CommandFactor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.Scanner;

public class FlowLineSvc {
    private static final int TIME_OUT = 100000;
    private static String ip;
    private static int port;
    private int currentCount1 = -1;
    private int currentCount2 = -1;
    private String lotNumber = "";

    public FlowLineSvc(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;

    }

    public synchronized void updateInFlow(Queue<String> barcodes, String programNum, String lotNumber, boolean startProgram) throws IOException, InterruptedException {
        Socket client = new Socket(ip, port);
        client.setSoTimeout(TIME_OUT);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));

        byte[] stopPrev = CommandFactor.getByteArr("SR", null, null);
        out.write(stopPrev);
        out.flush();
        printResponse(buf);

        byte[] cleanBuffer = CommandFactor.getByteArr("KX", null, null);
        out.write(cleanBuffer);
        out.flush();
        printResponse(buf);

        byte[] fwToProgram = CommandFactor.getByteArr("FW", programNum, null);
        out.write(fwToProgram);
        out.flush();
        printResponse(buf);

        //init count 1 to 0
        byte[] initCount1 = CommandFactor.getByteArr("KG", "1,0", null);
        out.write(initCount1);
        out.flush();
        printResponse(buf);

        //init count 2 to 0
        byte[] initCount2 = CommandFactor.getByteArr("KG", "2,0", null);
        out.write(initCount2);
        out.flush();
        printResponse(buf);

        // send be to program update barcode
        while(!barcodes.isEmpty()) {
            System.out.println("in the loop");
            byte[]  count1Status = CommandFactor.getByteArr("KH", "1", null);
            out.write(count1Status);
            out.flush();


            byte[]  count2Status = CommandFactor.getByteArr("KH", "2", null);
            out.write(count2Status);
            out.flush();

            try {
                String response = buf.readLine();

                System.out.println("KH RESPONSE: " + response);
                if (response != null) {
                    String[] curResponse = response.split(",");
                    if (curResponse[0].equals("KH") && curResponse[1].equals("1")) {
                        setCurrentCount1(Integer.parseInt(curResponse[2]));
                    }

                    if (curResponse[0].equals("KH") && curResponse[1].equals("2")) {
                        setCurrentCount2(Integer.parseInt(curResponse[2]));
                    }
                }

                if(startProgram || (getCurrentCount1() != 0 && getCurrentCount2() == 1)) {
                    out.write(initCount2);
                    out.flush();
                    printResponse(buf);

                    byte[]  setBarcode = CommandFactor.getByteArr("BH", "1", barcodes.poll());
                    out.write(setBarcode);
                    out.flush();
                    printResponse(buf);

                    Thread.sleep(100);

                    byte[]  setLotNumber = CommandFactor.getByteArr("BK", "1,0", lotNumber);
                    out.write(setLotNumber);
                    out.flush();
                    printResponse(buf);

                    byte[]  sqNumber = CommandFactor.getByteArr("SQ", null, null);
                    out.write(sqNumber);
                    out.flush();
                    printResponse(buf);
                }

                startProgram = false;
//                Scanner sc = new Scanner(System.in);

//                System.out.println("Do you want to update lotNumber?");
//                Thread.sleep(1000);
            } catch(SocketTimeoutException exception) {
                System.out.println("not response from server");
                break;
            } catch(IOException E) {
                System.out.println("io exception 1");
            }
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

    public int getCurrentCount1() {
        return currentCount1;
    }

    public void setCurrentCount1(int currentCount1) {
        this.currentCount1 = currentCount1;
    }

    public int getCurrentCount2() {
        return currentCount2;
    }

    public void setCurrentCount2(int currentCount2) {
        this.currentCount2 = currentCount2;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    private void printResponse(BufferedReader buf ) {
        try {
            String response = buf.readLine();
            System.out.println("RESPONSE: " + response);
        } catch(SocketTimeoutException exception) {
            System.out.println("fw to buffer issue");
        } catch(IOException e) {
            System.out.println("io exception");
        }
    }
}
