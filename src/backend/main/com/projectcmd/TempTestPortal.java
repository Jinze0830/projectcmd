package backend.main.com.projectcmd;

import backend.main.com.projectcmd.csvprocessor.CSVReader;

import java.io.FileNotFoundException;
import java.util.List;

public class TempTestPortal {
    public static void main(String[] args) {
        CSVReader reader = new CSVReader();
        try {
            List<String> readCSV
                    = reader.getColumn("./resources/Test.csv");

            readCSV.forEach(e -> System.out.println("current line:" + e));
        } catch (FileNotFoundException exception) {
            System.out.println("file not found");
        }
    }
}
