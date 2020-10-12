package backend.main.com.projectcmd;

import backend.main.com.projectcmd.csvprocessor.CSVReader;
import backend.main.com.projectcmd.csvprocessor.CommandFactor;
import backend.main.com.projectcmd.printerConnector.TcpClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TempTestPortal {
    public static void main(String[] args) {
        CSVReader reader = new CSVReader();
        try {
            List<String> readCSV
                    = reader.getColumn("./resources/Test.csv");

            // readCSV.forEach(e -> System.out.println("current line:" + e));
            //List<byte[]> cmds = CommandFactor.getHexStrs("BH", "1", readCSV);
            List<byte[]> cmds = new ArrayList<>();
            cmds.add(CommandFactor.getHexStr("SB", null, null));

            TcpClient client = new TcpClient("192.168.1.100", 9004);
            client.startClientWithCommand(cmds);
        } catch (FileNotFoundException exception) {
            System.out.println("file not found");
        } catch (IOException exception) {
            System.out.println("connection issue");
        }
    }
}
