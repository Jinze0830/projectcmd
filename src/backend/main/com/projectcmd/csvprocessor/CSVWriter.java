package backend.main.com.projectcmd.csvprocessor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    public static void writeToCSV(List<String> barcodes, String path) throws IOException {

        FileWriter fw = new FileWriter(path, true);

        for(String barcode: barcodes) {
            fw.append(barcode);
            fw.append("\n");
            fw.flush();
        }
        fw.flush();
        fw.close();
    }
}
