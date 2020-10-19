package backend.main.com.projectcmd.printerConnector;

import backend.main.com.projectcmd.csvprocessor.CommandFactor;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;

public class FlowLineSvc {
    private static final int TIME_OUT = 100000;
    private static String ip;
    private static int port;
    private int currentCount1 = -1;
    private int currentCount2 = -1;
    private String currentBarcode;
    private SwingWorker<Void, Integer> worker;

    public FlowLineSvc(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
    }

    public void startFlow(Queue<String> barcodes,
                          String programNum,
                          String lotNumber,
                          boolean startProgram,
                          String fileName) {
        worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws IOException, InterruptedException {
                updateInFlow(barcodes, programNum, lotNumber, startProgram, fileName, isCancelled());
                return null;
            }
        };
    }

    public synchronized void updateInFlow(Queue<String> barcodes,
                                          String programNum,
                                          String lotNumber,
                                          boolean startProgram,
                                          String fileName,
                                          boolean isCancelled) throws IOException, InterruptedException {
        Socket client = new Socket(ip, port);
        client.setSoTimeout(TIME_OUT);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        // message from server
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));

        byte[] stopPrev = CommandFactor.getByteArr("SR", null, null);
        out.write(stopPrev);
        out.flush();
        getResponse(buf, "SR");

        byte[] cleanBuffer = CommandFactor.getByteArr("KX", null, null);
        out.write(cleanBuffer);
        out.flush();
        getResponse(buf, "KX");

        byte[] fwToProgram = CommandFactor.getByteArr("FW", programNum, null);
        out.write(fwToProgram);
        out.flush();
        getResponse(buf, "FW");

        //init count 1 to 0
        byte[] initCount1 = CommandFactor.getByteArr("KG", "1,0", null);
        out.write(initCount1);
        out.flush();
        getResponse(buf, "KG1");

        //init count 2 to 0
        byte[] initCount2 = CommandFactor.getByteArr("KG", "2,0", null);
        out.write(initCount2);
        out.flush();
        getResponse(buf, "KG2");

        //update lot number for barcodes
        byte[]  setLotNumber = CommandFactor.getByteArr("BK", "1,0", lotNumber);
        out.write(setLotNumber);
        out.flush();
        getResponse(buf, "BK");

        // send be to program update barcode
        while(!barcodes.isEmpty() && !isCancelled) {
            System.out.println("in the loop");

            try {
                byte[]  count1Status = CommandFactor.getByteArr("KH", "1", null);
                out.write(count1Status);
                out.flush();

                String count1response = getResponse(buf, "KH1");

                if (count1response != null) {
                    String[] curResponse = count1response.split(",");
                    if (curResponse[0].equals("KH") && curResponse[1].equals("1")) {
                        setCurrentCount1(Integer.parseInt(curResponse[2]));
                    }
                }

                byte[]  count2Status = CommandFactor.getByteArr("KH", "2", null);
                out.write(count2Status);
                out.flush();

                String count2response = getResponse(buf, "KH2");

                System.out.println("KH2 RESPONSE: " + count1response);
                if (count2response != null) {
                    String[] curResponse = count2response.split(",");
                    if (curResponse[0].equals("KH") && curResponse[1].equals("2")) {
                        setCurrentCount2(Integer.parseInt(curResponse[2]));
                    }
                }


                if(startProgram || (getCurrentCount1() != 0 && getCurrentCount2() == 1)) {
                    out.write(initCount2);
                    out.flush();
                    getResponse(buf, "KG2");

                    String barcode = barcodes.poll();
                    byte[]  setBarcode = CommandFactor.getByteArr("BH", "1", barcode);
                    out.write(setBarcode);
                    out.flush();
                    getResponse(buf, "BH");

                    //update current barcode and add in csv
                    setCurrentBarcode(barcode);
//                    CSVWriter.writeToCSV(Arrays.asList(barcode), "./resources/printed/" + fileName);

                    Thread.sleep(100);

                    byte[]  sqNumber = CommandFactor.getByteArr("SQ", null, null);
                    out.write(sqNumber);
                    out.flush();
                    getResponse(buf, "SQ");
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
        getResponse(buf, "SR");

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

    public String getCurrentBarcode() {
        return currentBarcode;
    }

    public void setCurrentBarcode(String currentBarcode) {
        this.currentBarcode = currentBarcode;
    }


    public SwingWorker<Void, Integer> getWorker() {
        return worker;
    }

    private String getResponse(BufferedReader buf, String command) {
        String response = null;
        try {
            response = buf.readLine();
            System.out.println(command + " RESPONSE: " + response);
        } catch(SocketTimeoutException exception) {
            System.out.println("fw to buffer issue");
        } catch(IOException e) {
            System.out.println("io exception");
        }

        return response;
    }
}
