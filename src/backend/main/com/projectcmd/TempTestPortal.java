package backend.main.com.projectcmd;

import backend.main.com.projectcmd.csvprocessor.CSVReader;
import backend.main.com.projectcmd.printerConnector.TcpClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//RUN for test
public class TempTestPortal {
    public static void main(String[] args) {
//        CSVReader reader = new CSVReader();
//        try {
//            Set<String> readCSV
//                    = reader.getColumn("./resources/Test.csv");

            // readCSV.forEach(e -> System.out.println("current line:" + e));
            //List<byte[]> cmds = CommandFactor.getHexStrs("BH", "1", readCSV);
//            List<byte[]> cmds = new ArrayList<>();
//            cmds.add(CommandFactor.getHexStr("SB", null, null));
//
//            TcpClient client = new TcpClient("192.168.1.100", 9004);
//            client.startClientWithCommand(cmds);
//        } catch (FileNotFoundException exception) {
//            System.out.println("file not found");
//        } catch (IOException exception) {
//            System.out.println("connection issue");
//        }

        CSVReader reader = new CSVReader();
        Scanner sc = new Scanner(System.in);
        List<List<String>> buckets = null;
        try {
            buckets = reader.getColumnBuckets("./resources/Test.csv");
        } catch(FileNotFoundException e) {
            System.out.println("file not found!");
        }

        int count = 0;

        while(buckets != null && count < buckets.size()) {
            System.out.println("Do you want to continue: ");
            if(sc.hasNextLine() || sc.nextLine().toLowerCase().equals("yes")) {
                try {
                    TcpClient client = new TcpClient("192.168.1.100", 9004);
                    client.sendBarcodes(buckets.get(count), 20);
                } catch(IOException e) {
                    System.out.println("connect issue");
                } catch(InterruptedException e) {

                }
            } else if(sc.hasNextLine() || sc.nextLine().toLowerCase().equals("no")) {
                break;
            }
        }

//        CSVReader csvReader = new CSVReader();
//        Set<String> records = new HashSet<>();
//        records.add("0");
//        records.add("1");
//        records.add("2");
//        records.add("3");
//        records.add("4");
//        records.add("5");
//        records.add("6");
//        records.add("7");
//        records.add("8");
//        records.add("9");
//        records.add("10");
//        records.add("11");
//        List<List<String>> temp = csvReader.buildBucket(records);
//
//        for(List<String> t: temp) {
//            System.out.println(t.size());
//        }
    }
}
